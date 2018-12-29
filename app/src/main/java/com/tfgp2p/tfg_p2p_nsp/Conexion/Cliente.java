package com.tfgp2p.tfg_p2p_nsp.Conexion;

import android.content.Context;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.MyAlert;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Amigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import static com.tfgp2p.tfg_p2p_nsp.Utils.*;


/**
 * Created by Julio on 25/02/2018.
 *
 * Clase que implementa la parte cliente de la aplicación.
 */

public class Cliente {

	private static Cliente client = null;

	//private byte[] localAddress;
	//private int localPort;
	private SocketAddress localSocketAddress;

	private Context context;

	// Colección de amigos que contiene nombres, direcciones y puertos remotos.
	private Amigos amigos;

	private ServerSocket tcpListenSocket;
	private Socket tcpSocket2Server;
	private Socket tcpSocket;
	//private DatagramSocket udpSocket;
	//private Socket peerConnectingSocket;
	private DataOutputStream serverOutput;
	private DataInputStream serverInput;

	private DataOutputStream peerOutput;
	private DataInputStream peerInput;






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

			// Se crea el socket UDP y se asocia a un puerto disponible con el constructor por defecto:
			/*this.udpSocket = new DatagramSocket();
			this.udpSocket.setReuseAddress(true);


			//this.localAddress = udpListenSocket.getLocalAddress().getAddress();
			this.localAddress = Utils.getIP(udpSocket, context);
			this.localPort = udpSocket.getLocalPort();
			*/

			this.tcpSocket = new Socket();
			//this.tcpSocket.bind(udpSocket.getLocalSocketAddress());
			this.tcpSocket.setReuseAddress(true);
			this.tcpSocket2Server = new Socket();
			this.tcpSocket2Server.setReuseAddress(true);

			/*this.tcpListenSocket = new ServerSocket(localPort);
			this.tcpListenSocket.setReuseAddress(true);
			*/

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
				 * En la versión TCP no hace falta enviar la longitud del nombre porque ya
				 * le estamos enviando la longitud total del buffer. Además habría que
				 * mandar también el nombre del cliente.
				 */
				// TODO: Faltaría enviar tb la clave/id del amigo.
				// Quizá esto debería meterlo en el loginServer.
				baos.write(HELLO);
				baos.write(friendNameLen);
				baos.write(friendNameBytes);
				// Si es TCP:
				baos.write(Amigos.getMyName().getBytes());

				serverOutput.writeInt(baos.size());
				baos.writeTo(serverOutput);

				// Si es UDP:
				/*byte[] nameBuff = baos.toByteArray();
				DatagramPacket p = new DatagramPacket(nameBuff, nameBuff.length, Amigos.getServerInfo());
				udpSocket.send(p);
				*/
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

				nameBAOS.write(myName);

				// Lo que se enviará es la longitud de mi nombre y mi nombre, en este orden.
				peerOutput.writeByte(nameBAOS.size());
				nameBAOS.writeTo(peerOutput);
				/////////////////////////////////////////////
				// TODO: ¡¡OJO!! A partir de aquí creo que está incompleto. Falta recibir hello_friend o no_friend, y lo siguiente.
				byte[] resp = new byte[1];
				resp[0] = peerInput.readByte();
				/*DatagramPacket pac = new DatagramPacket(resp, resp.length);
				// TODO: Hacer algo aquí para que no se quede bloqueado el receive si la comunicación ha salido mal.
				socket.receive(pac);*/
				//socket_to_friend.receive(pac);
				//socket_to_server.receive(pac);
				/*int retries = 2;
				resp[0] = PUNCH;
				while ((resp[0]==PUNCH) && (retries>=0)) {
					socket.receive(pac);
					--retries;
				}
				if (retries < 0)
					throw new MyAlert("No se ha recibido respuesta del otro dispositivo");
				*/
				if (resp[0] == HELLO_FRIEND) {
					requestFile(fileName, friendName);
					receiveFile(fileName);
				}
				// TODO: Si no es amigo pensar por qué ha llegado a este punto. No debería poder hacer peticiones a no amigos.
				else if (resp[0] == NO_FRIEND) {
					throw new AlertException(friendName + " no es tu amigo.", context);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		catch (IOException e){
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
			InetSocketAddress serverAddr = Amigos.getTcpServerInfo();
					//new InetSocketAddress(Amigos.getTcpServerInfo().getAddress(), Amigos.getTcpServerInfo().getPort());
			tcpSocket2Server.connect(serverAddr);
			InetAddress localAddress = tcpSocket2Server.getLocalAddress();
			int localPort = tcpSocket2Server.getLocalPort();
			//----------------------------------------------------------------------------------
			this.localSocketAddress = new InetSocketAddress(localAddress, localPort);
			//this.localSocketAddress = new InetSocketAddress(localPort);
			//this.localSocketAddress = tcpSocket.getLocalSocketAddress();
			//----------------------------------------------------------------------------------

			/////////////////////////////////////////////////
			serverOutput = new DataOutputStream(tcpSocket2Server.getOutputStream());
			serverInput = new DataInputStream(tcpSocket2Server.getInputStream());
			////////////////////////////////////////////////

			/*String myName = Amigos.getMyName();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(SERVER_CONNECT);
			baos.write((byte) myName.length());
			baos.write(myName.getBytes());
			baos.write(IS_CLIENT_SOCKET);

			byte[] connectionBuffer = baos.toByteArray();
			DatagramPacket p = new DatagramPacket(connectionBuffer, connectionBuffer.length,
					Amigos.getServerInfo().getAddress(), Amigos.getServerInfo().getPort());
			udpSocket.send(p);
		*/
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
		// TODO: Falta devolver algún tipo de respuesta negativa si no se está registrado en el servidor.
		// Tamaño del buffer: 4 bytes para la IP (raw byte[4]) y 4 bytes del puerto (int).
		byte[] friendInfo = new byte[9];
		serverInput.readFully(friendInfo);

		if (friendInfo[0] == CLOSE_SOCKET) {
			byte[] IParray = new byte[4];
			System.arraycopy(friendInfo, 1, IParray, 0, 4);
			InetAddress friendIP = InetAddress.getByAddress(IParray);

			byte[] portArray = new byte[4];
			System.arraycopy(friendInfo, 5, portArray, 0, 4);
			int friendPort = Utils.byteArrayToInt(portArray);

			InetSocketAddress peerISA = new InetSocketAddress(friendIP, friendPort);

			tcpSocket.bind(localSocketAddress);
			// TODO: Comprobar que el puerto usado es el mismo que con el servidor.
			/*try{
				tcpSocket.connect(peerISA, 1000);
			} catch (SocketTimeoutException e){}
			*/
			//////////////////////////////////////////////////////////////////////
			tcpSocket.connect(peerISA);
			//////////////////////////////////////////////////////////////////////
			/*ServerSocket listen = new ServerSocket();
			listen.bind(localSocketAddress);
			Socket conn = listen.accept();
			*/

			peerInput = new DataInputStream(tcpSocket.getInputStream());
			peerOutput = new DataOutputStream(tcpSocket.getOutputStream());
		}

		else if (friendInfo[0] == NO_FRIEND)
			throw new AlertException("Ha habido un problema en la comunicación.", context);


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
			this.amigos.addFriend(friendName, tcpSocket.getInetAddress(), tcpSocket.getPort());
			/////////////////////////////////////////

			// Envío del aviso de una nueva petición.
			/*byte[] newreq = {Utils.NEW_REQ};
			peerOutput.write(newreq);
			*/

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

			peerOutput.writeInt(s.size());
			s.writeTo(peerOutput);
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

			byte[] dataBuffer = new byte[MAX_BUFF_SIZE];

			// TODO: quitar lo de "copia de".
			FileOutputStream fos = new FileOutputStream(Utils.parseMountDirectory().getAbsolutePath() + "/copia_de_" + fileName);
			// TODO: Esto está sin probar:
			// La recepción del fichero se resumiría en estas líneas:
			int count;
			while ((count = peerInput.read(dataBuffer)) > 0){
				fos.write(dataBuffer, 0, count);
			}
			System.out.println("\n****************\nEnvío completado\n****************\n");
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
