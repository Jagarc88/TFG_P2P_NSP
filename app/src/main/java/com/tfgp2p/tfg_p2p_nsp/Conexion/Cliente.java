package com.tfgp2p.tfg_p2p_nsp.Conexion;

import android.content.Context;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Amigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

import static com.tfgp2p.tfg_p2p_nsp.Utils.*;


/**
 * Created by Julio on 25/02/2018.
 *
 * Clase que implementa la parte cliente de la aplicación.
 */

public class Cliente {

	private static Cliente client = null;

	private byte[] address;
	private int localPort;
	private SocketAddress localSA;

	private Context context;

	// Colección de amigos que contiene nombres, direcciones y puertos remotos.
	private Amigos amigos;

	private DatagramSocket socket;

	private static int ppIndex = 0;






	public static Cliente getInstance(Context c){
		if (client == null)
			//client = new Cliente(Servidor.possiblePorts[ppIndex]);
			client = new Cliente(c);
		return client;
	}



	// TODO: Limpiar código del constructor que no debe estar.
	//private Cliente(int listenPort){
	private Cliente(Context c){
		try {
			this.context = c;
			this.amigos = Amigos.getInstance(context);

			socket = new DatagramSocket();
			localPort = socket.getLocalPort();

			loginServer();

			//////// Prueba de la conexión al móvil servidor:
			/////////// BORRAR AÑADIDO MANUAL DE UN AMIGO, borrar tb los catch////////////////

			String friendName = "Manolito";

			//String fileName = "serie";
			String fileName = "5megas.pdf";
			//String fileName = "de_julio.txt";

			//////////////////////////////////////////////////////////////////////////////////
			//////// BORRAR ENVÍO MANUAL DE PETICIÓN DE FICHERO //////////////////////////////
			try {
				byte[] friendNameBytes = friendName.getBytes();
				byte[] friendNameLen = {(byte) friendNameBytes.length};

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				/* Datos para la primera comunicación:
				 * "HOLA, quiero hablar con MANOLITO cuyo nombre tiene esta otra LONGITUD".
				 */
				// TODO: Faltaría enviar tb la clave/id del amigo.
				baos.write(HELLO);
				//baos.write(nameLen);
				//baos.write(nameBytes);
				baos.write(friendNameLen);
				baos.write(friendNameBytes);

				byte[] nameBuff = baos.toByteArray();
				DatagramPacket p = new DatagramPacket(nameBuff, nameBuff.length, Amigos.getServerInfo());
				socket.send(p);
			}
			catch (IOException e){
				e.printStackTrace();
			}
			//////////////////////////////////////////////////////////////

			// TODO: La petición debe ir en el sendRequest(). Ya lo meteré cuando organice el envío de todo tipo de peticiones.
			try {
				// Este método se bloquea hasta que recibe la dirección y el puerto de la máquina destino.
				connect_to_friend();

				ByteArrayOutputStream nameBAOS = new ByteArrayOutputStream();
				byte[] myName = Amigos.getMyName().getBytes();
				byte[] myNameLen = {(byte) myName.length};

				// Lo que se enviará es la longitud de mi nombre y mi nombre, en este orden.
				nameBAOS.write(myNameLen);
				nameBAOS.write(myName);
				byte[] buff = nameBAOS.toByteArray();

				DatagramPacket hey_its_me = new DatagramPacket(buff, buff.length,
						socket.getInetAddress(), socket.getPort());
				socket.send(hey_its_me);

				byte[] resp = new byte[1];
				DatagramPacket pac = new DatagramPacket(resp, resp.length);
				socket.receive(pac);

				if (resp[0] == HELLO_FRIEND) {
					requestFile(fileName, friendName);
					receiveFile(fileName);
				}
				else if (resp[0] == NO_FRIEND) {
					throw new AlertException(friendName + " no es tu amigo.", context);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		catch (IllegalArgumentException e) {
			if (ppIndex < 4) {
				e.printStackTrace();
				new Cliente(c);
			}
			else
				e.printStackTrace();
		}
		catch (SocketException e){
			e.printStackTrace();
		}
		catch (AlertException e){
			e.showAlert();
		}

	}



	/**
	 * Necesitamos enviar el puerto del socket del cliente para que la tabla NAT
	 * tras la que está el contrario lo guarde y pueda pasar los paquetes que se
	 * reciban a su destino.
	 */
	private void loginServer(){
		try {
			InetSocketAddress serverAddr = new InetSocketAddress(Amigos.getServerInfo().getAddress(), Amigos.getServerInfo().getPort());
			socket.connect(serverAddr);
			localSA = socket.getLocalSocketAddress();

			String myName = Amigos.getMyName();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(SERVER_CONNECT);
			baos.write((byte) myName.length());
			baos.write(myName.getBytes());
			baos.write(IS_CLIENT_SOCKET);

			byte[] connectionBuffer = baos.toByteArray();
			DatagramPacket p = new DatagramPacket(connectionBuffer, connectionBuffer.length,
					Amigos.getServerInfo().getAddress(), Amigos.getServerInfo().getPort());
			socket.send(p);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Conectar al amigo. Sólo se debe llamar a este método cuando se espera que el servidor
	 * envíe a este dispositivo la IP y el puerto del amigo.
	 * Con la llamada a connect nos aseguramos de que cuando creamos el paquete
	 * con los datos lo enviamos al destino correcto. Si la dirección o el puerto
	 * puesto en la creación del paquete es distinto a los asignados al socket
	 * con la llamada a connect, saltará una IllegalArgumentException.
	 *
	 * @throws IOException
	 */
	private void connect_to_friend() throws IOException, AlertException {
		// Tamaño del buffer: 4 bytes para la IP (raw byte[4]) y 4 bytes del puerto (int).
		byte[] friendInfo = new byte[8];

		DatagramPacket friendInfoPacket = new DatagramPacket(friendInfo, friendInfo.length);
		socket.receive(friendInfoPacket);

		if (friendInfo[0] == NO_FRIEND)
			throw new AlertException("Ha habido un problema en la comunicación.", context);

		byte[] IParray = new byte[4];
		System.arraycopy(friendInfo, 0, IParray, 0, 4);
		InetAddress friendIP = InetAddress.getByAddress(IParray);

		byte[] portArray = new byte[4];
		System.arraycopy(friendInfo, 4, portArray, 0, 4);
		int friendPort = Utils.byteArrayToInt(portArray);

		// TODO: Repasar este comentario por si al final funciona de forma distinta:
		/* Con esto damos tiempo a que el dispositivo que actúa como proveedor del archivo le dé
		 * tiempo a mandar el paquete que hará que la NAT tras la que se encuentra registre la
		 * traducción IP+puerto privados <-> IP+puerto públicos y pueda recibir ya el primer
		 * paquete que le envíe el cliente.
		 */
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		socket.connect(friendIP, friendPort);
	}



	public void sendRequest(){
		// TODO: Implementar una cola de espera de salida de peticiones para cuando el móvil destino está sin conexión.

	}


	/**
	 * Solicita el fichero seleccionado en la carpeta compartida del amigo y lo descarga.
	 * El paquete que se envía con la petición contiene la siguiente información en este orden:
	 * 1º Tipo de petición (FILE_REQ).
	 * 2º Nombre del usuario que manda la petición.
	 * 3º Nombre del fichero solicitado.
	 *
	 * @param fileName Nombre del fichero solicitado.
	 * @param friendName Nombre del amigo que tiene el fichero.
	 */
	private void requestFile(String fileName, String friendName) {
		try{
			// TODO FALTA REESCRIBIR BIEN ESTE METODO. edit: puede que así valga.
			// TODO: Borrar añadido manual del amigo:
			this.amigos.addFriend(friendName, socket.getInetAddress(), socket.getPort());
			/////////////////////////////////////////
			// Se envia FILE_REQ + nombre del archivo.

			InetSocketAddress addr = amigos.getFriendAddr(friendName);
			// Hay que enviar en el byte[] FILE_REQ, la longitud del archivo, y el nombre del archivo.
			byte[] reqType = new byte[1];
			reqType[0] = Utils.FILE_REQ;
			byte[] fLength = {(byte) fileName.length()};
			byte[] fnBuffer = fileName.getBytes();

			ByteArrayOutputStream s = new ByteArrayOutputStream();
			s.write(reqType);
			s.write(fLength);
			s.write(fnBuffer);

			byte[] completeBuffer = s.toByteArray();
			DatagramPacket request = new DatagramPacket(completeBuffer, completeBuffer.length, addr.getAddress(), addr.getPort());
			socket.send(request);
		}
		catch (AlertException e){
			e.showAlert();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


	/**
	 * Descarga un fichero.
	 */
	private void receiveFile(String fileName) throws AlertException{
		// TODO: quitar lo de "copia de".
		try (FileOutputStream fos = new FileOutputStream(Utils.parseMountDirectory().getAbsolutePath() + '/' + fileName)) {
			// TODO: Esto está sin probar:

			final int bufferSize = 12+MAX_BUFF_SIZE;
			byte[] data = new byte[bufferSize];
			DatagramPacket dataPacket = new DatagramPacket(data, data.length);
			byte[] ackBuffer = new byte[1];
			DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
			Checksum checksum = new Adler32();
			long calculatedCS = 0;
			long receivedCS = 1;
			byte retries = 1;
			byte[] received_checksum_array = new byte[8];
			int count = MAX_BUFF_SIZE;
			// Vamos a esperar hasta 5 segundos al primer paquete.
			socket.setSoTimeout(5000);
			boolean firstRun = true;

			// todo: repasar este comentario.
			/* Se envía:
			 * Checksum =                   8 bytes +
			 * Tamaño de los datos leídos = 4 bytes +
			 * Datos =                   1024 bytes = 1036 bytes
			 */
			while (count == MAX_BUFF_SIZE){
				while (calculatedCS != receivedCS) {
					while (retries > 0){
						try {
							socket.receive(dataPacket);
						} catch (SocketTimeoutException e) {
							e.printStackTrace();
							// ¿QUÉ RESPUESTA MANDO AL SERVIDOR?

							socket.send(ackPacket);
							--retries;
						}
					}
					// TODO: Si da tiempo implementar que las descargas se puedan pausar (por el usuario o por pérdida de la red).
					if (retries == 0) throw new AlertException("Se ha agotado el tiempo de espera", context);
					retries = 5;

					checksum.update(data, 0, data.length);
					calculatedCS = checksum.getValue();
					System.arraycopy(data, 0, received_checksum_array, 0, received_checksum_array.length);
					receivedCS = byteArrayToLong(received_checksum_array);
				}

				byte[] size = new byte[4];
				System.arraycopy(data, 8, size, 0, 4);
				count = Utils.byteArrayToInt(size);
				fos.write(data, 12, count);

				// ESTO NO VA A FUNCIONAR. CREO QUE NECESITAMOS LOS NÚMEROS DE SECUENCIA.
				ackBuffer[0] = PACKET_OK;
				socket.send(ackPacket);

				if (firstRun){
					firstRun = false;
					socket.setSoTimeout(1000);
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}




	/**
	 * Obtiene el nombre del fichero en un String a partir de un buffer de metadatos.
	 *
	 * @param metadataBuffer Array de metadatos
	 * @return Nombre del archivo.
	 */
	private String getFileNameFromBuffer(byte[] metadataBuffer) {
		int count = 0;
		while (metadataBuffer[count+4] != 0) {
			++count;
		}

		byte[] aux = new byte[count];
		for (int j=0; j<aux.length; j++){
			aux[j] = metadataBuffer[j+4];
		}

		return new String(aux);
	}


	// TODO: (Opcional) Implementar cancelación y pausa de descargas.
}
