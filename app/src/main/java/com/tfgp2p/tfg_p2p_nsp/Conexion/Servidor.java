package com.tfgp2p.tfg_p2p_nsp.Conexion;

import android.util.Pair;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Amigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.tfgp2p.tfg_p2p_nsp.Utils.*;



/**
 * Created by Julio on 24/02/2018.
 *
 * Clase que implementa la parte servidor de la aplicación.
 */

	// TODO: Cambiar nombre a carpeta Gnutella.

	// TODO: Sería óptimo tener un hilo recibiendo las conexiones entrantes y hasta n (pequeño) proveyendo ficheros (a n clientes).
public class Servidor {

	// Instancia del objeto Servidor.
	private static Servidor server = null;

	// Puerto en el que se escucha a conexiones entrantes.
	//private int listenTCPPort;
	private int listenPort;

	//private ServerSocket listenSocket;
	private static DatagramSocket listenSocket;
	private DatagramSocket socket_to_client;

	// Cola que guarda el nombre del amigo y la petición.
	private Queue<Pair<String, byte[]>> requestQueue;

	/*
	 * Conjunto de pares identificador / IP+puerto de los amigos conectados.
	 * Cuando un amigo finalice la conexión, también debe ser borrado de esta variable.
	 */
	//private HashMap<String, InetSocketAddress> activeClients;

	// TODO: Debería ser final, pero en android no me permite dejarla en blanco e inicializarla en el constructor por ejemplo...
	private static InetSocketAddress serverInfo;


	/**
	 * Devuelve el objeto servidor. Si este no existe se crea.
	 *
	 * @return objeto servidor.
	 */
	public static Servidor getInstance(){
		if (server == null)
			server = new Servidor();
		return server;
	}



	private Servidor(){
		try {
			// TODO: poner la direccion del servidor.
			serverInfo = new InetSocketAddress(Inet4Address.getByName("2.153.114.70"), 61516);
			this.listenSocket = new DatagramSocket();
			this.listenSocket.setReuseAddress(true);

			// Para chequear si asigna bien el puerto:
			this.listenPort = this.listenSocket.getLocalPort();

			this.socket_to_client = new DatagramSocket();
			this.requestQueue = new ArrayDeque<>();
			//this.activeClients = new HashMap<>(10);

			loginServer();

			// La parte servidor lanza dos hilos: Uno que se queda a la escucha y otro que atiende peticiones encoladas.
			new Thread(new Runnable() {
				@Override
				public void run() {
					listen();
				}
			}).start();
			new Thread(new Runnable() {
				@Override
				public void run() {
					serve();
				}
			}).start();

		} catch (IOException e) {
			new Servidor();
		}
	}


	/**
	 * Implementa la conexión al servidor nada más abrir la aplicación para que este dispositivo
	 * quede registrado en el servidor y a disposición de las solicitudes de los amigos.
	 *
	 * La información enviada es SERVER_CONNECT, el tamaño del nombre del cliente y el nombre del cliente.
	 */
	private void loginServer(){
		String myName = Amigos.getMyName();
		byte[] connectionBuffer = new byte[2+myName.length()];
		connectionBuffer[0] = SERVER_CONNECT;
		connectionBuffer[1] = (byte) myName.length();
		System.arraycopy(myName.getBytes(), 0, connectionBuffer, 2, myName.length());
		DatagramPacket p = new DatagramPacket(connectionBuffer, connectionBuffer.length,
				serverInfo.getAddress(), serverInfo.getPort());
		try {
			listenSocket.connect(serverInfo.getAddress(), serverInfo.getPort());
			listenSocket.send(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Método principal de la parte servidor. Se queda bloqueado hasta
	 * que llegue una conexión entrante.
	 *
	 *
	 * Tipos de conexión que pueden querer hacer los clientes:
	 *
	 * 1- De recolección de metadatos de la carpeta compartida para poder ver el contenido
	 *    del dispositivo servidor en su dispositivo.
	 *
	 * 2- De solicitud de descarga de un fichero tras seleccionarlo en su dispositivo después
	 *    de haber descargado los metadatos de la carpeta previamente.
	 */
    private void listen(){
		try {
			while (true){
				// Se quedará bloqueado con una llamada a receive.
				//byte request = waitRequest();
				waitRequest();
				// manageResponse se encargará de responder adecuadamente según el tipo de solicitud.
				/*if ((isValidRequest(request)))
					// Si la petición es válida se encola dentro de waitRequest().
					// Si es la primera de la cola se atiende en un hilo nuevo.
					new Thread(new Runnable(){
						public void run(){

						// Hay que pasarle a manage el amigo cogido de la cola y la petición.
						manageResponse(friend, request);
						}
					}).start();
				else
					throw new AlertException("Error, ID de paquete no válida.");
				*/
			}
		} catch (AlertException e){
			e.showAlert();
		}
	}


	/**
	 * Este es el método que irá sirviendo las peticiones que realizaron los amigos y que
	 * han sido previamente encoladas.
	 *
	 * - Mientras no haya peticiones en la cola se bloquea el hilo.
	 * - Cuando el hilo a la escucha encole una petición se reanuda.
	 */
	private void serve(){
		// TODO: usar wait() y notify(). Mientras la cola esté vacía wait()...
		try{
			synchronized (requestQueue) {
				while (requestQueue.isEmpty())
					requestQueue.wait();

				Pair<String, byte[]> req = requestQueue.poll();
				manageResponse(req.first, req.second);
			}
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
	}


	/**
	 * La función de este método es esperar algún tipo de petición por parte de los amigos.
	 * La petición recibida se mete en la cola de espera para atenderla cuando sea su turno.
	 * Si la petición se realiza desde un dispositivo que no es amigo se muestra error.
	 *
	 * @return Identificador de la petición.
	 */
	private void waitRequest() throws AlertException{
		// Las solicitudes entrantes han de pasar por la cola de entrada SIEMPRE.
		/*
		 * Con la cola de espera estoy forzando a que las peticiones se atiendan de una en una.
		 * Para que se atiendan todas habría que lanzar un thread por cada petición atendida.
		 */
		// TODO: implementar envío de peticiones desde la parte Cliente.
		// TODO: Si un dispositivo hace una petición y no es amigo, hay que rechazar la petición.
		/////////// BORRAR AÑADIDO MANUAL DE UN AMIGO, borrar tb los catch ////////////////
		/*try{
			Amigos amigos = Amigos.getInstance();
			InetSocketAddress addr = new InetSocketAddress(Inet4Address.getByName("192.168.0.10"), listenPort);
			amigos.addFriend("Manolito", addr);

		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (AlertException e){
			e.showAlert();
		}*/
		////////////////////////////////////////////////////////////

		try{
			// Mientras el servidor no envíe aviso de nueva petición el hilo no avanza.
			byte[] new_req = new byte[1];
			DatagramPacket p = new DatagramPacket(new_req, new_req.length);
			while (new_req[0] != NEW_REQ){
				new_req[0] = 0;
				listenSocket.receive(p);
			}


			// TODO: Para hacerlo mejor, recibir tb el nombre en el connect_to_friend y guardarlo en una variable privada.
			connect_to_friend();

			byte[] requestorFriendName = new byte[32];
			byte[] request = new byte[64];
			request[0] = -1;
			/*
			Hacer las peticiones de archivos en 2 pasos:
			1 - Soy Manolito
			2 - Quiero un archivo y es este
			 */

			/* Primero hay que recibir el nombre del amigo que hará la petición.
			 * Se recibe en reqFriendPacket el primer byte con la longitud del nombre
			 * y después el nombre.
			 */
			DatagramPacket reqFriendPacket = new DatagramPacket(requestorFriendName, requestorFriendName.length);
			//listenSocket.receive(reqFriendPacket);
			//socket_to_client.receive(reqFriendPacket);
			listenSocket.receive(reqFriendPacket);
			//DatagramPacket reqPacket = new DatagramPacket(request, request.length);
			byte nameSize = requestorFriendName[0];
			String friendName = new String(requestorFriendName).substring(1, nameSize+1);

			Amigos amigos = Amigos.getInstance();
			//////////////////// BORRAR AÑADIDO MANUAL DEL AMIGO //////////////////////
			amigos.addFriend(friendName, reqFriendPacket.getAddress(), reqFriendPacket.getPort());
			///////////////////////////////////////////////////////////////////////////
			if (!amigos.isFriend(friendName, reqFriendPacket.getAddress())){
			// Valor -1 no válido para provocar fallo en caso de petición incorrecta.
				request[0] = -1;
				byte[] no = {NO_FRIEND};
				// TODO: ¿Mejor socket_to_client.getAddress() y getPort()?
				DatagramPacket resp = new DatagramPacket(no, no.length, reqFriendPacket.getAddress(), reqFriendPacket.getPort());
				//listenSocket.send(resp);
				//socket_to_client.send(resp);
				listenSocket.send(resp);
				throw new AlertException("Error, alguien ha realizado una petición sin ser tu amigo.");
				// TODO: (Opcional) Implementar bloqueo de usuarios que no son amigos y realizan peticiones a saco.
				// TODO: (Opcional) Implementar HashMap de usuarios bloqueados.
				// TODO: Escribir aquí el código que decide esto.

			}
			else{
				/* Se mete en la cola el amigo y la petición entera, incluído un nombre
				 * de fichero si es eso lo que solicita.
				 */
				byte[] ok = {HELLO_FRIEND};
				// TODO: Repasar la dicección y el puerto que estoy poniendo en este paquete.
				DatagramPacket resp = new DatagramPacket(ok, ok.length, reqFriendPacket.getAddress(), reqFriendPacket.getPort());
				//socket_to_client.send(resp);
				listenSocket.send(resp);

				DatagramPacket reqPacket = new DatagramPacket(request, request.length);
				//socket_to_client.receive(reqPacket);
				listenSocket.receive(reqPacket);

				if (isValidRequest(request[0])){
					synchronized (requestQueue) {
						//Byte[] aux = new Byte[request.length];
						byte[] aux = new byte[request.length];
						System.arraycopy(request, 0, aux, 0, request.length);
						requestQueue.add(new Pair<>(friendName, aux));
						requestQueue.notify();
					}
				}
				else throw new AlertException("Error, petición incorrecta.");
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		catch (Exception e){
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
	private void connect_to_friend() throws IOException {
		// Tamaño del buffer: 4 bytes para la IP (raw byte[4]) y 4 bytes del puerto (int).
		byte[] friendInfo = new byte[8];
		DatagramPacket friendInfoPacket = new DatagramPacket(friendInfo, friendInfo.length);
		listenSocket.receive(friendInfoPacket);

		byte[] IParray = new byte[4];
		System.arraycopy(friendInfo, 0, IParray, 0, 4);
		InetAddress friendIP = InetAddress.getByAddress(IParray);

		byte[] portArray = new byte[4];
		System.arraycopy(friendInfo, 4, portArray, 0, 4);
		int friendPort = Utils.byteArrayToInt(portArray);

		//socket_to_client.connect(friendIP, friendPort);
		listenSocket.connect(friendIP, friendPort);
	}


	/**
	 * Gestiona la respuesta que tiene que dar según el tipo de solicitud atendida.
	 */
	private void manageResponse(String friend, byte[] request){
		switch (request[0]){
			// TODO: BORRAR ESTE COMENTARIO cuando esté implementado:
			/*
			 * Cuando un amigo se conecta a otro y quiere ver su carpeta solicita TODOS los
			 * metadatos de los ficheros. Si un usuario modifica o elimina un fichero DEBE mandar
			 * a sus amigos los nuevos metadatos, si están conectados.
			 * No se me ocurre el caso en el que se soliciten los metadatos de sílo 1 fichero.
			 */
			case METADATA_REQ_ONE:
				// TODO: Pensar cuándo puede darse el caso de solicitar metadatos de sólo 1 fichero.
				break;

			case METADATA_REQ_ALL:
				sendAllFilesMetadata();
				break;

			case FILE_REQ:
				try{
					/* request[0] : Tipo de petición.
					 * request[1] = N : Longitud del nombre del fichero.
					 * request[2..N] : Nombre del fichero.
					 */
					InetSocketAddress addr = Amigos.getInstance().getFriendAddr(friend);
					//int fileNamePosition = 1 + friend.length();
					//String fileName = request.toString().substring(fileNamePosition);
					String fileName = new String(request).substring(2, 2+request[1]);
					sendFile(fileName, addr);
				}
				catch (AlertException e){
					e.showAlert();
				}
				break;

			case PACKET_ACK:
				// TODO: Puede que packetACK no sea útil aquí...
				break;

			default:
				// Petición no admitida.
				// TODO: deberíamos hacer que en el destinatario se mostrara un popup de error, por ejemplo con un ERROR_popup.
				break;
		}
	}


	/**
	 * Se envía un archivo al dispositivo remoto.
	 *
	 * @param fileName Nombre del archivo que se enviará.
	 * @param addr Dirección IP y puerto al que se envía el archivo.
	 */
	public void sendFile(String fileName, InetSocketAddress addr){
		try{
			//InetSocketAddress addr = this.friends.get("Manolito");

			// Escribir los datos del archivo aquí.
			// <1 KB.
			//String path = Utils.parseMountDirectory().getAbsolutePath() + "/de_julio.txt";
			// >500 KB.
			//String path = Utils.parseMountDirectory().getAbsolutePath() + "/Resumen ASOR.pdf";
			// 9 KB.

			/*String path = Utils.parseMountDirectory().getAbsolutePath() + "/contacts.vcf";
			File file = new File(path);*/
			///////////////////////////////////////
			// TODO: Poner aquí bien la ruta de la carpeta compartida.
			String sharedFolder = Utils.parseMountDirectory().getAbsolutePath();
			File file = new File(sharedFolder + '/' + fileName);
			///////////////////////////////////////
			FileInputStream fis = new FileInputStream(file);
			int fileLength = (int) file.length();

			// TODO: Implementar almacenamiento de metadatos de ficheros ajenos.
			// TODO: ¿Quitar sendFileMetadata() de aquí?
			/*
			 * Si el usuario ha seleccionado el fichero que quiere descargar es porque ya tiene
			 * los metadatos necesarios.
			 */
			sendFileMetadata(file, addr, fileLength);

			byte[] buffer = new byte[MAX_BUFF_SIZE];
			int totalBytesRead = 0;
			int bytesRead = 0;

			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr.getAddress(), addr.getPort());

			boolean nextIsLast = false;
			int bytesRemaining = fileLength;

			while ((totalBytesRead < fileLength) && (bytesRead != -1)) {
				if (buffer == null) {
					if (!nextIsLast) {
						buffer = new byte[MAX_BUFF_SIZE];
						bytesRead = fis.read(buffer, 0, MAX_BUFF_SIZE);
					}
					else {
						buffer = new byte[bytesRemaining];
						bytesRead = fis.read(buffer, 0, bytesRemaining);
					}
				}
				else
					bytesRead = fis.read(buffer, 0, MAX_BUFF_SIZE);

				totalBytesRead += bytesRead;
				packet.setData(buffer);
				// TODO: Utilizar otro socket (otro puerto).
				listenSocket.send(packet);
				//////////////////////////////////////////////
				/*try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e){ e.printStackTrace();}
				*/
				//////////////////////////////////////////////
				buffer = null;

				bytesRemaining = fileLength - totalBytesRead;
				if ((bytesRemaining) < MAX_BUFF_SIZE)
					nextIsLast = true;
			}

			fis.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}



	public static InetSocketAddress getServerInfo(){
		return serverInfo;
	}


	/**
	 * Descripción: Envía el nombre y la longitud de un fichero a un dispositivo remoto.
	 * Este método es útil para la recolección de metadatos de los archivos de las carpetas compartidas.
	 * Se debería llamar a este método cada vez que se modifique un fichero.
	 *
	 * IMPORTANTE: El buffer de metadatos está pensado para que los 4 primeros bytes contengan el tamaño
	 * del fichero (en un int traducido a byte[4]) y en los siguientes el nombre. Por ahora se usa un
	 * int, por lo que se admite un tamaño máximo de archivo de unos 2 GB.
	 *
	 * @param file Fichero al que pertenecer los metadatos.
	 * @param addr Dirección a la que se envía la información.
	 * @param fileLength Longitud (tamaño) del archivo.
	 * @throws IOException
	 */
	private void sendFileMetadata(File file, InetSocketAddress addr, int fileLength) throws IOException{
		byte[] metadataBuffer = new byte[file.getName().length() + 4];

		// Tamaño del fichero.
		byte[] len = Utils.intToByteArray(fileLength);
		for (int i=0; i<4 ;i++)
			metadataBuffer[i] = len[i];

		// Nombre del fichero.
		//byte[] aux = file.getName().getBytes(Charset.forName("UTF-8"));
		byte[] aux = file.getName().getBytes();
		System.arraycopy(aux, 0, metadataBuffer, 4, aux.length);

		DatagramPacket metadataPacket = new DatagramPacket(metadataBuffer, metadataBuffer.length, addr.getAddress(), addr.getPort());
		listenSocket.send(metadataPacket);
	}


	/**
	 * Escanea la carpeta compartida y devuelve un byte[] con el tamaño y el nombre de todos los ficheros.
	 * @return tamaño y nombre de los ficheros de la carpeta compartida.
	 */
	private byte[] getMetadataFromSharedFolder() {
		// TODO: Poner bien la carpeta compartida.
		File filesPath =  new File(Utils.parseMountDirectory().getAbsolutePath());
		File[] files = filesPath.listFiles();
		int[] sizes = new int[files.length];

		int bufferSize = 0;
		// Para cada fichero necesitamos 4 bytes para guardar el tamaño + tantos bytes como caracteres tenga el nombre.
		for (int i=0; i<files.length; i++) {
			sizes[i] = (int) files[i].length();
			bufferSize += 4 + files[i].getName().length();
		}

		byte[] metadataBuffer = new byte[bufferSize];

		int i = 0;
		int j = 0;
		while ((i < metadataBuffer.length) && (j < files.length)) {
			byte[] byteArraySize = intToByteArray(sizes[j]);
			byte[] name = files[j].getName().getBytes();
			// Copiar Tamaño:
			System.arraycopy(byteArraySize, 0, metadataBuffer, i, 4);
			i += 4;
			// Copiar nombre:
			System.arraycopy(name, j, metadataBuffer, i, name.length);
			i += name.length;
			++j;
		}

		return metadataBuffer;
	}


	/**
	 * Envía el tamaño de los ficheros y sus nombres en un solo paquete al amigo para
	 * que pueda ver el contenido de la carpeta compartida remota.
	 */
	private void sendAllFilesMetadata() {
		try {
			byte[] allMetadata = getMetadataFromSharedFolder();
			DatagramPacket p = new DatagramPacket(allMetadata, allMetadata.length);
			listenSocket.send(p);
			// TODO: Creo que no me falta nada en este método...

		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


	public static DatagramSocket getServerSocket(){
		return listenSocket;
	}


}
