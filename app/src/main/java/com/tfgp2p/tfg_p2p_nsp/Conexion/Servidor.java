package com.tfgp2p.tfg_p2p_nsp.Conexion;

import android.content.Context;
import android.util.Pair;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Amigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.Queue;

import static com.tfgp2p.tfg_p2p_nsp.Utils.*;



/**
 * Created by Julio on 24/02/2018.
 *
 * Clase que implementa la parte servidor de la aplicación.
 */

	// TODO: Sería óptimo tener un hilo recibiendo las conexiones entrantes y hasta n (pequeño) proveyendo ficheros (a n clientes).
public class Servidor {

	// Instancia del objeto Servidor.
	private static Servidor server = null;

	private Context context;

	// Puerto en el que se escucha a conexiones entrantes.
	//private int listenTCPPort;
	private int listenPort;
	private byte[] address;
	private SocketAddress localSA;

	private ServerSocket listenSocket;
	private Socket socket;
	// TODO: Seguramente haga falta unos cuantos sockets para servir a varios clientes a la vez...
	private Socket peerSocket;
	private Socket peerConnectingSocket;
	private DataOutputStream serverOutput;
	private DataInputStream serverInput;
	private DataOutputStream peerOutput;
	private DataInputStream peerInput;
	//private DatagramSocket listenSocket;
	//private DatagramSocket socket_to_client;

	// Cola que guarda el nombre del amigo y la petición.
	// TODO: En su lugar seguramente tendría que implementar una cola de hilos, cada uno con el socket al cliente corespondiente...
	private Queue<Pair<String, byte[]>> requestQueue;

	/*
	 * Conjunto de pares identificador / IP+puerto de los amigos conectados.
	 * Cuando un amigo finalice la conexión, también debe ser borrado de esta variable.
	 */
	//private HashMap<String, InetSocketAddress> activeClients;



	/**
	 * Devuelve el objeto servidor. Si este no existe se crea.
	 *
	 * @return objeto servidor.
	 */
	public static Servidor getInstance(Context c){
		if (server == null)
			server = new Servidor(c);
		return server;
	}



	private Servidor(Context c){
		try {
			this.context = c;
			// TODO: poner la direccion del servidor.
			//serverInfo = new InetSocketAddress(Inet4Address.getByName("2.153.114.70"),44200);
			//this.listenSocket = new DatagramSocket();

			this.socket = new Socket();
			this.socket.setReuseAddress(true);
			this.peerSocket = new Socket();
			this.peerSocket.setReuseAddress(true);
			this.peerConnectingSocket = new Socket();
			this.peerConnectingSocket.setReuseAddress(true);

			// Para chequear si asigna bien el puerto:
			//this.listenPort = this.listenSocket.getLocalPort();
			this.address = new byte[4];

			//this.socket_to_client = new DatagramSocket();
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
			new Servidor(c);
		}
	}


	/**
	 * Implementa la conexión al servidor nada más abrir la aplicación para que este dispositivo
	 * quede registrado en el servidor y a disposición de las solicitudes de los amigos.
	 *
	 * La información enviada es SERVER_CONNECT, el tamaño del nombre del cliente y el nombre del cliente.
	 */
	private void loginServer(){
		// TODO: Ver la IP local antes y después de conectarse.
		//listenSocket.connect(serverInfo.getAddress(), serverInfo.getPort());
		String myName = Amigos.getMyName();
		/*byte[] connectionBuffer = new byte[2+myName.length()];
		connectionBuffer[0] = SERVER_CONNECT;
		connectionBuffer[1] = (byte) myName.length();
		System.arraycopy(myName.getBytes(), 0, connectionBuffer, 2, myName.length());
		*/
		try {
			InetSocketAddress serverAddr = new InetSocketAddress(Amigos.getServerInfo().getAddress(), Amigos.getServerInfo().getPort());
			socket.connect(serverAddr);
			listenPort = socket.getLocalPort();
			localSA = socket.getLocalSocketAddress();
			//listenSocket = new ServerSocket(listenPort);
			//listenSocket.setReuseAddress(true);
			//peerSocket = new Socket(serverInfo.getAddress(), serverInfo.getPort());

			serverOutput = new DataOutputStream(socket.getOutputStream());
			serverInput = new DataInputStream(socket.getInputStream());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(SERVER_CONNECT);
			baos.write((byte) myName.length());
			baos.write(myName.getBytes(), 0, myName.length());
			this.address = Utils.getIP(socket, context);
			//byte[] localPort = Utils.intToByteArray(listenPort);

			baos.write(address, 0, address.length);
			//baos.write(localPort, 0, localPort.length);
			/*byte[] connectionBuffer = baos.toByteArray();
			DatagramPacket p = new DatagramPacket(connectionBuffer, connectionBuffer.length,
					serverInfo.getAddress(), serverInfo.getPort());

			listenSocket.send(p);
			*/

			// Primero hay que mandar el tamaño del stream que tiene que leer el servidor:
			//DataOutputStream dos = new DataOutputStream(peerSocket.getOutputStream());
			serverOutput.writeInt(baos.size());

			// Ahora se escribe la información:
			baos.writeTo(serverOutput);
			//dos.close();
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
		while (true) {
			// TODO: usar wait() y notify(). Mientras la cola esté vacía wait()...
			try {
				synchronized (requestQueue) {
					while (requestQueue.isEmpty())
						requestQueue.wait();

					Pair<String, byte[]> req = requestQueue.poll();
					manageResponse(req.first, req.second);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

		serverInput.readFully(friendInfo);
		ByteArrayInputStream bais = new ByteArrayInputStream(friendInfo);

		byte[] IParray = new byte[4];
		bais.read(IParray, 0, 4);
		InetAddress friendIP = InetAddress.getByAddress(IParray);

		byte[] portArray = new byte[4];
		bais.read(portArray, 0, 4);
		int friendPort = Utils.byteArrayToInt(portArray);

		peerSocket.bind(localSA);
		peerConnectingSocket.bind(localSA);

		final InetSocketAddress peerISA = new InetSocketAddress(friendIP, friendPort);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					peerSocket.connect(peerISA);
					// Este no se conecta, parece que sólo sirve para el HP.
					System.out.println("Direccion: " + peerSocket.getInetAddress() + ", puerto: " + peerSocket.getPort());
					//todo: Comprobar si se ha conectado al puerto local o a otro. Habría que conectarlo al mismo que se conectó al servidor.
				} catch (IOException e){e.printStackTrace();}
			}
		}).start();


		/////////////////////////////////////////////////////////////////
		serverOutput.writeInt(1);
		serverOutput.writeByte(CLOSE_SOCKET);
		listenSocket = new ServerSocket(listenPort);
		//listenSocket.bind(localSA);
		listenSocket.setReuseAddress(true);
		peerSocket = listenSocket.accept();
		////////////////////////////////////////////////////////////////

		peerOutput = new DataOutputStream(peerSocket.getOutputStream());
		peerInput = new DataInputStream(peerSocket.getInputStream());

	}





	/**
	 * La función de este método es esperar algún tipo de petición por parte de los amigos.
	 * La petición recibida se mete en la cola de espera para atenderla cuando sea su turno.
	 * Si la petición se realiza desde un dispositivo que no es amigo se muestra error.
	 */
	private void waitRequest() throws AlertException{
		// Las solicitudes entrantes han de pasar por la cola de entrada SIEMPRE.
		/*
		 * Con la cola de espera estoy forzando a que las peticiones se atiendan de una en una.
		 * Para que se atiendan todas habría que lanzar un thread por cada petición atendida.
		 */

		try{
			// Mientras el servidor no envíe aviso de nueva petición el hilo no avanza.
			byte[] new_req = new byte[1];

			while (new_req[0] != NEW_REQ){
				new_req[0] = 0;
				new_req[0] = serverInput.readByte();
			}

			connect_to_friend();

			// todo: Ver si tengo conectado el peerConnectedSocket aquí fuera del thread.
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
			 * y después el nombre, que se guarda en requestorFriendName.
			 */

			byte nameSize = peerInput.readByte();
			peerInput.readFully(requestorFriendName, 0, nameSize);
			String friendName = new String(requestorFriendName).substring(0, nameSize);


			Amigos amigos = Amigos.getInstance(context);
			//////////////////// BORRAR AÑADIDO MANUAL DEL AMIGO //////////////////////
			amigos.addFriend(friendName, peerSocket.getInetAddress(), peerSocket.getPort());
			///////////////////////////////////////////////////////////////////////////

			if (!amigos.isFriend(friendName, peerSocket.getInetAddress())){
			// Valor -1 no válido para provocar fallo en caso de petición incorrecta.
				request[0] = -1;
				peerOutput.writeByte(NO_FRIEND);
				throw new AlertException("Error, alguien ha realizado una petición sin ser tu amigo.", context);
				// TODO: (Opcional) Implementar bloqueo de usuarios que no son amigos o sí y/o realizan peticiones a saco.
				// TODO: (Opcional) Implementar HashMap de usuarios bloqueados.
				// TODO: Escribir aquí el código que decide esto.

			}
			else{
				// Se manda un saludo al amigo para hacerle saber que la conexión ha ido bien y que se espera su petición:
				peerOutput.writeByte(HELLO_FRIEND);

				// Se recibe su petición:
				int reqSize = peerInput.readInt();
				peerInput.read(request,0, reqSize);

				/* Se mete en la cola el amigo y la petición entera, incluído un nombre
				 * de fichero si es eso lo que solicita.
				 */
				if (isValidRequest(request[0])){
					synchronized (requestQueue) {
						byte[] aux = new byte[reqSize];
						System.arraycopy(request, 0, aux, 0, request.length);
						requestQueue.add(new Pair<>(friendName, aux));
						requestQueue.notify();
					}
				}
				else throw new AlertException("Error, petición incorrecta.", context);
			}
		}
		catch (IOException e){
			e.printStackTrace();
			throw new AlertException(e.getMessage(), context);
		}
		catch (Exception e){
			e.printStackTrace();
		}

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
				// TODO: Los metadatos de un solo fichero sólo serán pedidos cuando se haga una búsqueda.
				break;

			case METADATA_REQ_ALL:
				/* Los metadatos de todos los ficheros se pedirán cuando un usuario quiera entrar
				 * en la carpeta compartida del amigo.
				 */
				sendAllFilesMetadata();
				break;

			case FILE_REQ:
				try{
					/* request[0] : Tipo de petición.
					 * request[1] = N : Longitud del nombre del fichero.
					 * request[2..N] : Nombre del fichero.
					 */
					InetSocketAddress addr = Amigos.getInstance(context).getFriendAddr(friend);
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
				// TODO: deberíamos hacer que en el destinatario se mostrara una alerta de error.
				break;
		}
	}


	/**
	 * Se envía un archivo al dispositivo remoto.
	 *
	 * @param fileName Nombre del archivo que se enviará.
	 * @param addr Dirección IP y puerto al que se envía el archivo.
	 */
	private void sendFile(String fileName, InetSocketAddress addr){
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
			/*int fileLength = (int) file.length();

			// TODO: Implementar almacenamiento de metadatos de ficheros ajenos.
			// TODO: ¿Quitar sendFileMetadata() de aquí?
			/*
			 * Si el usuario ha seleccionado el fichero que quiere descargar es porque ya tiene
			 * los metadatos necesarios.
			 */
			//sendFileMetadata(file, addr, fileLength);

			// TODO: Esto está sin probar:
			// El envío del fichero en sí se resumiría en 4 líneas:
			byte[] buffer = new byte[MAX_BUFF_SIZE];
			int count;
			while ((count = fis.read(buffer)) > 0) {
				peerOutput.write(buffer, 0, count);
			}

			//dos.close();
			//fis.close();
			/*int totalBytesRead = 0;
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
				//listenSocket.send(packet);
				//////////////////////////////////////////////
				/*try {
					Thread.sleep(1000);
				}
				catch(InterruptedException e){ e.printStackTrace();}
				*/
				//////////////////////////////////////////////
			/*	buffer = null;

				bytesRemaining = fileLength - totalBytesRead;
				if ((bytesRemaining) < MAX_BUFF_SIZE)
					nextIsLast = true;
			}*/

			//fis.close();

		} catch (IOException | NullPointerException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
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
	 * @param file Fichero al que pertenecen los metadatos.
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

		int dataSize = metadataBuffer.length;
		peerOutput.writeInt(dataSize);
		peerOutput.write(metadataBuffer, 0, dataSize);
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
			peerOutput.write(allMetadata, 0, allMetadata.length);
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


	public byte[] getAddress(){
		return this.address;
	}

}
