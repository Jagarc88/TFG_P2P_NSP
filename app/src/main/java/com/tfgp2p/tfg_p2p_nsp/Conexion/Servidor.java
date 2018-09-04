package com.tfgp2p.tfg_p2p_nsp.Conexion;

import android.content.Context;
import android.util.Pair;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Amigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

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

	private int listenPort;
	private byte[] address;
	private SocketAddress localSA;

	private DatagramSocket socket;
	private DatagramSocket socket_to_client;

	// Cola que guarda el nombre del amigo y la petición.
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

			this.socket = new DatagramSocket();
			this.socket.setReuseAddress(true);
			this.socket_to_client = new DatagramSocket(null	);
			this.socket_to_client.setReuseAddress(true);

			this.address = new byte[4];
			this.requestQueue = new ArrayDeque<>();

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
		String myName = Amigos.getMyName();

		try {
			InetSocketAddress serverAddr = new InetSocketAddress(Amigos.getServerInfo().getAddress(), Amigos.getServerInfo().getPort());
			socket.connect(serverAddr);
			listenPort = socket.getLocalPort();
			localSA = socket.getLocalSocketAddress();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(SERVER_CONNECT);
			baos.write((byte) myName.length());
			baos.write(myName.getBytes(), 0, myName.length());
			this.address = Utils.getIP(socket, context);
			//byte[] localPort = Utils.intToByteArray(listenPort);

			baos.write(address, 0, address.length);
			//baos.write(localPort, 0, localPort.length);
			byte[] connectionBuffer = baos.toByteArray();
			DatagramPacket p = new DatagramPacket(connectionBuffer, connectionBuffer.length, serverAddr);
			socket.send(p);


			// Primero hay que mandar el tamaño del stream que tiene que leer el servidor:
			//DataOutputStream dos = new DataOutputStream(peerSocket.getOutputStream());
			//serverOutput.writeInt(baos.size());

			// Ahora se escribe la información:
			//baos.writeTo(serverOutput);
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
		DatagramPacket friendInfoPacket = new DatagramPacket(friendInfo, friendInfo.length);

		socket.receive(friendInfoPacket);

		byte[] IParray = new byte[4];
		System.arraycopy(friendInfo, 0, IParray, 0, 4);
		InetAddress friendIP = InetAddress.getByAddress(IParray);

		byte[] portArray = new byte[4];
		System.arraycopy(friendInfo, 4, portArray, 0, 4);
		int friendPort = Utils.byteArrayToInt(portArray);

		socket_to_client.bind(localSA);
		socket_to_client.connect(friendIP, friendPort);

		//TODO: revisar este comentario.
		/* Ahora entra en acción el Hole Punching. Se deben enviar 2 paquetes:
		 *
		 * El primero es el que abre el camino hacia nuestro dispositivo. Cuando el router toma este
		 * paquete para reenviarlo crea en su tabla NAT la traducción IP_origen/puerto_origen ;
		 * IP_destino/puerto_destino. Así cuando reciba un paquete desde el destino hasta el origen
		 * registrados lo pasará.
		 *
		 * El segundo paquete tiene como objetivo asegurarse de que la NAT del otro dispositivo
		 * ya tiene guardada la traducción correspondiente en su tabla de traducciones y que desde
		 * el momento que obtiene respuesta ya se puede comenzar con la comunicación de los datos
		 * que nos interesan.
		 */

		byte[] sendPunchArray = {PUNCH};
		DatagramPacket sendPunch = new DatagramPacket(sendPunchArray, 1, socket_to_client.getInetAddress(), socket_to_client.getPort());
		socket_to_client.send(sendPunch);
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
			DatagramPacket p = new DatagramPacket(new_req, new_req.length);

			while (new_req[0] != NEW_REQ){
				new_req[0] = 0;
				socket.receive(p);
			}

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
			 * y después el nombre, que se guarda en requestorFriendName.
			 */

			DatagramPacket reqFriendPacket = new DatagramPacket(requestorFriendName, requestorFriendName.length);
			socket_to_client.receive(reqFriendPacket);
			byte nameSize = requestorFriendName[0];
			String friendName = new String(requestorFriendName).substring(1, nameSize+1);


			Amigos amigos = Amigos.getInstance(context);
			//////////////////// BORRAR AÑADIDO MANUAL DEL AMIGO //////////////////////
			amigos.addFriend(friendName, reqFriendPacket.getAddress(), reqFriendPacket.getPort());
			///////////////////////////////////////////////////////////////////////////

			if (!amigos.isFriend(friendName, reqFriendPacket.getAddress())){
			// Valor -1 no válido para provocar fallo en caso de petición incorrecta.
				request[0] = -1;
				byte[] no = {NO_FRIEND};
				DatagramPacket resp = new DatagramPacket(no, no.length, reqFriendPacket.getAddress(), reqFriendPacket.getPort());
				socket_to_client.send(resp);
				throw new AlertException("Error, alguien ha realizado una petición sin ser tu amigo.", context);
				// TODO: (Opcional) Implementar bloqueo de usuarios que no son amigos o sí y/o realizan peticiones a saco.
				// TODO: (Opcional) Implementar HashMap de usuarios bloqueados.
				// TODO: Escribir aquí el código que decide esto.

			}
			else{
				// Se manda un saludo al amigo para hacerle saber que la conexión ha ido bien y que se espera su petición:
				byte[] ok = {HELLO_FRIEND};
				DatagramPacket resp = new DatagramPacket(ok, ok.length, reqFriendPacket.getAddress(), reqFriendPacket.getPort());
				socket_to_client.send(resp);

				// Se recibe su petición:
				DatagramPacket reqPacket = new DatagramPacket(request, request.length);
				socket_to_client.receive(reqPacket);
				int reqSize = reqPacket.getLength();

				/* Se mete en la cola el amigo y la petición entera, incluído un nombre
				 * de fichero si es eso lo que solicita.
				 */
				if (isValidRequest(request[0])){
					synchronized (requestQueue) {
						byte[] aux = new byte[reqSize];
						System.arraycopy(request, 0, aux, 0, reqSize);
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
		// TODO: Poner aquí bien la ruta de la carpeta compartida.
		String sharedFolder = Utils.parseMountDirectory().getAbsolutePath();
		File file = new File(sharedFolder + '/' + fileName);

		try (FileInputStream fis = new FileInputStream(file)){
			// Escribir los datos del archivo aquí.
			// <1 KB.
			//String path = Utils.parseMountDirectory().getAbsolutePath() + "/de_julio.txt";
			// >500 KB.
			//String path = Utils.parseMountDirectory().getAbsolutePath() + "/Resumen ASOR.pdf";
			// 9 KB.


			// TODO: Implementar almacenamiento de metadatos de ficheros ajenos.
			// TODO: ¿Quitar sendFileMetadata() de aquí?
			/*
			 * Si el usuario ha seleccionado el fichero que quiere descargar es porque ya tiene
			 * los metadatos necesarios.
			 */

			// TODO: Esto está sin probar:
			final int bufferSize = 16+MAX_BUFF_SIZE;
			byte[] buffer = new byte[bufferSize];
			byte[] readDataSize;
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr.getAddress(), addr.getPort());
			int count;
			Checksum checksum = new Adler32();
			long longCS;
			byte[] checksumArray;
			int seqNum = 0;
			byte[] seqArray;
			byte[] answer = new byte[5];
			DatagramPacket answerPacket = new DatagramPacket(answer, answer.length);
			int lostPacketNum;
			socket_to_client.connect(addr);
			/* Los paquetes enviados se guardan (¿temporalmente? llamar a clear()) para poder reenviarlos en caso
			 * de que al cliente no le hayan llegado alguno.
			 */
			HashMap<Integer, DatagramPacket> packetsSent = new HashMap<>(16);
			socket_to_client.setSoTimeout(2000);

			while ((count = fis.read(buffer, 16, MAX_BUFF_SIZE)) > 0) {
				// TODO: repasar este comentario.
				/* Se envía:
				 * Checksum =                   8 bytes +
				 * Nº de secuencia =            4 bytes +
				 * Tamaño de los datos leídos = 4 bytes +
				 * Datos =                   1024 bytes = 1040 bytes
				 */

				// Copia de nº de secuenia:
				seqArray = Utils.intToByteArray(++seqNum);
				System.arraycopy(seqArray, 0, buffer, 8, seqArray.length);

				// Copia de los datos leídos:
				readDataSize = Utils.intToByteArray(count);
				System.arraycopy(readDataSize, 0, buffer, 12, readDataSize.length);

				// Copia del checksum (Va al principio del buffer y se calcula al final):
				checksum.update(buffer, 8, bufferSize-8);
				longCS = checksum.getValue();
				checksumArray = longToByteArray(longCS);
				System.arraycopy(checksumArray, 0, buffer, 0, checksumArray.length);

				socket_to_client.send(packet);
				packetsSent.put(seqNum, packet);

				try {
					socket_to_client.receive(answerPacket);
					/* answer[] podría recibir la señal de si hay paquetes corruptos o perdidos,
					 * cuántos son y el nº del primero (o directamente todos los números).
					 */
					if (answer[0] != PACKET_OK) {
						while (answer[0] == PACKET_CORRUPT_OR_LOST) {
							try {
								System.arraycopy(answer, 1, seqArray, 0, seqArray.length);
								lostPacketNum = byteArrayToInt(seqArray);
								packet = packetsSent.get(lostPacketNum);
								socket_to_client.send(packet);
								socket_to_client.receive(answerPacket);
							} catch (SocketTimeoutException e){
								e.printStackTrace();
							}
						}
					}
				} catch (SocketTimeoutException e) {
					/* Si no se recibe respuesta lo más probable es que todo haya ido bien y se
					 * continua con al ejecución normal. En caso de que se haya perdido algún paquete
					 * ya lo pedirá el cliente a continuación y se recibirá dicha petición en la
					 * siguiente iteración.
					 */
					e.printStackTrace();
				}

				if (packetsSent.size() == 16)
					packetsSent.clear();
			}

			/* En cada recepción de paquetes pueden ocurrir varias cosas:
			 * 1- Que llegue bien.
			 * 2- Que llegue mal o que no llegue => Enviarlo de nuevo.
			 *
			 * En caso de implementarlo de forma que no se espere a una respuesta por parte del cliente
			 * hay que tener en cuenta los casos descritos a continuación.
			 * Para que funcione mejor se podrían enviar 8 paquetes (por ejemplo) y haríamos que el
			 * cliente comprobara que todos están llegando bien cada 8 paquetes.
			 *
			 * 1- Que no llegue 1 o más y haya que calcular cuántos se han perdido =>
			 *    => Hay que guardarlos en una estructura y mandarlos de nuevo.
			 * 2- Que llegue alguno duplicado => Descartarlo.
			 * 3- Que llegue corrupto (comprobar checksum) => Mandarlo de nuevo.
			 */

		} catch (IOException | NullPointerException | IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		System.out.println("Envío completado");
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
		byte[] aux = file.getName().getBytes();
		System.arraycopy(aux, 0, metadataBuffer, 4, aux.length);

		DatagramPacket metadataPacket = new DatagramPacket(metadataBuffer, metadataBuffer.length, addr.getAddress(), addr.getPort());
		socket.send(metadataPacket);
	}


	/**
	 * Escanea la carpeta compartida y devuelve un byte[] con el tamaño y el nombre de todos los ficheros.
	 * @return tamaño y nombre de los ficheros de la carpeta compartida.
	 */
	private byte[] getMetadataFromSharedFolder() {
		// TODO: FALTAN 2 COSAS: MANDAR EL Nº DE FICHEROS TOTALES Y EL TAMAÑO DEL NOMBRE DE CADA UNO.
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
			socket.send(p);
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


	public byte[] getAddress(){
		return this.address;
	}

}
