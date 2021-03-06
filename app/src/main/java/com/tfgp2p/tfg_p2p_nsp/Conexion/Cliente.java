package com.tfgp2p.tfg_p2p_nsp.Conexion;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.GestorSistemaAmigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.tfgp2p.tfg_p2p_nsp.Utils.*;


/**
 * Created by Julio on 25/02/2018.
 *
 * Clase que implementa la parte cliente de la aplicación.
 */

public class Cliente {

	private static Cliente client = null;

	// Colección de amigos que contiene nombres, direcciones y puertos remotos.
	private GestorSistemaAmigos gestorSistemaAmigos;

	private static DatagramSocket socket_to_server;
	private DatagramSocket socket_to_friend;

	private static int ppIndex = 0;






	public static Cliente getInstance(){
		if (client == null)
			//client = new Cliente(Servidor.possiblePorts[ppIndex]);
			client = new Cliente();
		return client;
	}


	// TODO: Limpiar código del constructor que no debe estar.
	//private Cliente(int listenPort){
	private Cliente(){
		try {
			//this.friendsSockets = new ArrayList<>(10);
			//this.friends = new HashMap<>(10);
			this.gestorSistemaAmigos = GestorSistemaAmigos.getInstance();
			//this.socket = new DatagramSocket(listenPort);
			//this.socket_to_server = new DatagramSocket();
			socket_to_server = Servidor.getServerSocket();
			this.socket_to_friend = new DatagramSocket();

			//////// Prueba de la conexión al móvil servidor:
			/////////// BORRAR AÑADIDO MANUAL DE UN AMIGO, borrar tb los catch////////////////

			//InetSocketAddress sa = new InetSocketAddress(Inet4Address.getByName("192.168.0.12"), listenPort);
			//InetSocketAddress sa = new InetSocketAddress(Inet4Address.getByName(""), 50000);
			String friendName = "Manolito";
			//this.amigos.addFriend(friendName, sa);


			//String fileName = "serie";
			String fileName = "5megas.pdf";
			//String fileName = "de_julio.txt";

			//String name = "Manolito";
			//InetSocketAddress friendAddr = amigos.getFriendAddr(name);
			//////////////////////////////////////////////////////////////////////////////////
			//////// BORRAR ENVÍO MANUAL DE PETICIÓN DE FICHERO //////////////////////////////
			try {
				byte[] friendNameBytes = friendName.getBytes();
				byte[] friendNameLen = {(byte) friendNameBytes.length};
				//byte[] nameLen = new byte[] {(byte) name.length()};
				//byte[] nameBytes = name.getBytes();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
// TODO: Como ahora hay que pasar por el servidor, en el paquete hay que meter quién es el destino, al menos en el primer paquete.
// TODO: Puedo guardar los usuarios de la conexión en la clase Connection del programa servidor.
				/* Datos para la primera comunicación:
				 * "HOLA, quiero hablar con MANOLITO cuyo nombre tiene esta otra LONGITUD".
				 */
				// TODO: Faltaría enviar tb la clave del amigo.
				baos.write(HELLO);
				//baos.write(nameLen);
				//baos.write(nameBytes);
				baos.write(friendNameLen);
				baos.write(friendNameBytes);
				byte[] nameBuff = baos.toByteArray();
				DatagramPacket p = new DatagramPacket(nameBuff, nameBuff.length, Servidor.getServerInfo());
				socket_to_server.send(p);
			}
			catch (IOException e){
				e.printStackTrace();
			}
			///////////////////////////////////////////////

			// TODO: La petición debe ir en el sendRequest(). Ya lo meteré cuando organice el envío de todo tipo de peticiones.
			try {
				// Este método se bloquea hasta que recibe la dirección y el puerto de la máquina destino.
				connect_to_friend();

				///////////////////////////////////////////////////////////

				ByteArrayOutputStream nameBAOS = new ByteArrayOutputStream();
				byte[] myName = GestorSistemaAmigos.getMyName().getBytes();
				byte[] myNameLen = {(byte) myName.length};
				// Lo que se enviará es la longitud de mi nombre y mi nombre, en este orden.
				nameBAOS.write(myNameLen);
				nameBAOS.write(myName);
				byte[] buff = nameBAOS.toByteArray();

				/*DatagramPacket hey_its_me = new DatagramPacket(buff, buff.length,
						socket_to_friend.getInetAddress(), socket_to_friend.getPort());
				socket_to_friend.send(hey_its_me);
				*/
				//////////////////////////////////////////////
				DatagramPacket hey_its_me = new DatagramPacket(buff, buff.length,
						socket_to_server.getInetAddress(), socket_to_server.getPort());
				socket_to_server.send(hey_its_me);
				/////////////////////////////////////////////

				byte[] resp = new byte[1];
				DatagramPacket pac = new DatagramPacket(resp, resp.length);
				// TODO: Hacer algo aquí para que no se quede bloqueado el receive si la comunicación ha salido mal.
				//socket.receive(pac);
				//socket_to_friend.receive(pac);
				socket_to_server.receive(pac);
				if (resp[0] == HELLO_FRIEND) {
					requestFile(fileName, friendName);
					receiveFile(fileName);
				}
				// TODO: Si no es amigo pensar por qué ha llegado a este punto. No debería poder hacer peticiones a no amigos.
				else if (resp[0] == NO_FRIEND) {
					throw new AlertException(friendName + " no es tu amigo.");
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (SocketException e){
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			if (ppIndex < 4)
				//new Cliente(Servidor.possiblePorts[++ppIndex]);
				// TODO: Seguramente no haga falta usar puertos predeterminados por el programador. Debería bastar con el puerto
				// TODO: elegido al azar al crear el socket con el constructor del socket sin argumentos.
				new Cliente();
			else
				e.printStackTrace();
		}
		/*catch (UnknownHostException e){
			e.printStackTrace();
		}*/
		catch (AlertException e){
			e.showAlert();
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
	private void connect_to_friend() throws IOException {
		// Tamaño del buffer: 4 bytes para la IP (raw byte[4]) y 4 bytes del puerto (int).
		byte[] friendInfo = new byte[8];
		DatagramPacket friendInfoPacket = new DatagramPacket(friendInfo, friendInfo.length);
		socket_to_server.receive(friendInfoPacket);

		byte[] IParray = new byte[4];
		System.arraycopy(friendInfo, 0, IParray, 0, 4);
		InetAddress friendIP = InetAddress.getByAddress(IParray);

		byte[] portArray = new byte[4];
		System.arraycopy(friendInfo, 4, portArray, 0, 4);
		int friendPort = Utils.byteArrayToInt(portArray);

		//socket_to_friend.connect(friendIP, friendPort);
		socket_to_server.connect(friendIP, friendPort);
		// En principio la conexión sale bien.
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
	 * @param name Nombre del amigo que tiene el fichero.
	 */
	private void requestFile(String fileName, String name) {
		try{
			// TODO FALTA REESCRIBIR BIEN ESTE METODO!!!! edit: puede que así valga.
			// Se envia FILE_REQ + nombre del archivo.

			InetSocketAddress addr = gestorSistemaAmigos.getFriendAddr(name);
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
			//socket_to_friend.send(request);
			socket_to_server.send(request);
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
	private void receiveFile(String fileName){
		try {
			///////////////////////  Prueba de la recepción del archivo ///////////////////////////
			// De momento pillo aquí el buffer de metadatos.
			// TODO: Para recibir un archivo no hay que devolver el nombre en el metadataBuffer, solo el tamaño.
			byte[] metadataBuffer = new byte[100];
			DatagramPacket metadataPacket = new DatagramPacket(metadataBuffer, metadataBuffer.length);
			///////////////////////////////////////////////////////////////////////////////////

			//socket_to_friend.receive(metadataPacket);
			socket_to_server.receive(metadataPacket);

			byte[] aux = new byte[4];
			for (int i = 0; i < 4; i++)
				aux[i] = metadataBuffer[i];

			byte[] dataBuffer = new byte[MAX_BUFF_SIZE];
			DatagramPacket dataPacket = new DatagramPacket(dataBuffer, dataBuffer.length);

			//socket_to_friend.receive(dataPacket);
			socket_to_server.receive(dataPacket);

			FileOutputStream fos = new FileOutputStream(Utils.parseMountDirectory().getAbsolutePath() + '/' + fileName);
			boolean exit = false;

			while (!exit) {
				//TODO Implementar algún tipo de verificación de paquetes. Por ejemplo, cuando se reciba 1 mandar mensaje de confirmación.
				/* Habría que implementarlo con un nextIsLast como en la parte Servidor, pero así funciona.
				 * MUY IMPORTANTE USAR dataPacket.getLength() Y NO dataBuffer.length PARA DESCARTAR LO QUE SOBRA
				 * DEL dataBuffer de escrituras anteriores. Para hacerlo como en la parte Servidor habría que
				 * limpiar el buffer y así como está no lo estamos haciendo. Si no se limpia el dataBuffer
				 * tiene la longitud MAX_BUFF_SIZE y, por tanto, datos antiguos sobre los que no se han escrito
				 * datos nuevos con el último paquete.
				 */
				fos.write(dataBuffer, 0, dataPacket.getLength());

				if (dataPacket.getLength() < MAX_BUFF_SIZE)
					exit = true;
				else
					//socket_to_friend.receive(dataPacket);
					socket_to_server.receive(dataPacket);
			}

			fos.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


	/**
	 * Obtiene el nombre del fichero en un String a partir de un buffer de metadatos.
	 *
	 * @param metadataBuffer
	 * @return
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
