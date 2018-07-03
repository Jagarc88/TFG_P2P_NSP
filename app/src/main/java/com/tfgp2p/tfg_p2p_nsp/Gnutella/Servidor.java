package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import android.util.Pair;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Amigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

import static com.tfgp2p.tfg_p2p_nsp.Utils.MAX_BUFF_SIZE;
import static com.tfgp2p.tfg_p2p_nsp.Utils.FILE_REQ;
import static com.tfgp2p.tfg_p2p_nsp.Utils.METADATA_REQ_ALL;
import static com.tfgp2p.tfg_p2p_nsp.Utils.METADATA_REQ_ONE;
import static com.tfgp2p.tfg_p2p_nsp.Utils.PACKET_ACK;
import static com.tfgp2p.tfg_p2p_nsp.Utils.intToByteArray;
import static com.tfgp2p.tfg_p2p_nsp.Utils.isValidRequest;



/**
 * Created by Julio on 24/02/2018.
 *
 * Clase que implementa la parte servidor de la aplicación.
 */


	// TODO: Sería óptimo tener un hilo recibiendo las conexiones entrantes y hasta n (pequeño) proveyendo ficheros (a n clientes).
public class Servidor {

	// Instancia del objeto Servidor.
	private static Servidor server = null;

	// Puerto en el que se escucha a conexiones entrantes.
	//private int listenTCPPort;
	private int listenPort;

	//private ServerSocket listenSocket;
	private DatagramSocket listenSocket;

	// TODO: Si la info del clienteActivo se comparte entre hilos mejor usar sólo la cola, supongo.
	private DatagramSocket activeClientSocket;

	// Cola que guarda el nombre del amigo y el tipo de petición.
	// Integer debería ser un byte.
	private Queue<Pair<String, Byte[]>> requestQueue;

	// Puertos posibles en los que va a estar a la escucha el serverSocket.
	static final int possiblePorts[] = {61516, 62516, 63516, 64516};
	/*
	 * Índice del puerto a escoger. Es útil para llamar de nuevo al constructor del objeto
	 * Servidor si el puerto al que apunta el índice está cerrado o en uso.
	 */
	private static int ppIndex = 0;
	// Colección de sockets conectados a los clientes.
	//private ArrayList<Socket> clientsSockets;
	// TODO: Pensar mejor el tipo de datos para la colección de sockets, si la necesitáramos. Si no borrarla.
	// Puede que ya no sea necesario almacenar los sockets si trabajamos con UDP.

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
	public static Servidor getInstance(){
		if (server == null)
			server = new Servidor(possiblePorts[ppIndex]);
		return server;
	}



	private Servidor(int port) {
		try {
			this.listenSocket = new DatagramSocket(port);
			this.listenSocket.setReuseAddress(true);

			// Para chequear si asigna bien el puerto:
			this.listenPort = this.listenSocket.getLocalPort();

			this.activeClientSocket = null;
			this.requestQueue = new ArrayDeque<>();
			//this.activeClients = new HashMap<>(10);

			// La parte servidor lanza un hilo que se queda a la escucha.
			new Thread(new Runnable() {
				@Override
				public void run() {
					listen();
				}
			}).start();

		} catch (IOException e) {
			if (ppIndex < 4)
				new Servidor(possiblePorts[++ppIndex]);
			else
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
	 *    de haber descargado los metadatos de la carptea previamente.
	 */
    public void listen(){
		try {
			while (true){
				// Se quedará bloqueado con una llamada a receive.
				byte request = 0;// = waitRequest();
				// manageResponse se encargará de responder adecuadamente según el tipo de solicitud.
				if ((isValidRequest(request)))
					// Si la petición es válida se encola. Si es la primera de la cola se atiende en un hilo nuevo.
					// TODO: Encolar petición y lanzar aquí los thread.

					// Hay que pasarle a manage el amigo cogido de la cola y la petición.
					manageResponse(request);
				else
					throw new AlertException("Error, ID de paquete no válida.");
			}
		} catch (AlertException e){
			e.showAlert();
		}
	}


	/**
	 * La función de este método es esperar algún tipo de petición por parte de los amigos.
	 * La petición recibida se mete en la cola de espera para atenderla cuando sea su turno.
	 * Si la petición se realiza desde un dispositivo que no es amigo se muestra error.
	 *
	 * @return Identificador válido de la petición.
	 */
	private void waitRequest(){
		// TODO: implementar una cola de espera de entrada (como atributo privado de la clase) para las solicitudes entrantes.
		// TODO: Las solicitudes entrantes han de pasar por la cola de entrada SIEMPRE.
		/*
		 * Con la cola de espera estoy forzando a que la peticiones se atiendan de una en una.
		 * Para que se atiendan todas habría que lanzar un thread por cada petición atendida.
		 */
		// TODO: implementar envío de peticiones desde la parte Cliente.
		// TODO: Si un dispositivo hace una petición y no es amigo, hay que rechazar la petición.
		/////////// BORRAR AÑADIDO MANUAL DE UN AMIGO, borrar tb los catch ////////////////
		try{
			Amigos amigos = Amigos.getInstance();
			InetSocketAddress addr = new InetSocketAddress(Inet4Address.getByName("192.168.0.10"), listenPort);
			amigos.addFriend("Manolito", addr);

		} catch (UnknownHostException e){
			e.printStackTrace();
		} catch (AlertException e){
			e.showAlert();
		}
		////////////////////////////////////////////////////////////

		// Valor inicial -1 no válido para provocar fallo en caso de petición incorrecta.
		// TODO: Hacer que se envíen los bytes justos en la petición desde la parte Cliente, si se puede.
		byte[] request = new byte[64];
		try{
			DatagramPacket reqPacket = new DatagramPacket(request, request.length);
			// Se recibe el tipo de petición y el nombre del que la realiza.
			listenSocket.receive(reqPacket);

			String name = new String(request).substring(1);

			if (!Amigos.getInstance().isFriend(name, reqPacket.getAddress())){
				request[0] = -1;
			}
			else {
				// TODO: Comprobar con 2 peticiones de 2 móviles que los 2 thread lanzados para atender
				// TODO: a cada uno tienen como clienteActivo al correcto y no comparten esa variable.
				// TODO: Puede que no sea necesario clienteActivo gracias a la cola.
				/* Se mete en la cola el amigo y la petición entera, incluído un nombre
				 * de fichero si es eso lo que solicita.
				 */
				Byte[] aux = new Byte[request.length];
				System.arraycopy(request, 0, aux, 0, request.length);
				this.requestQueue.add(new Pair<>(name, aux));
				// TODO: ya no es necesario devolver en este método la request entera.
				// TODO: En el manage() la podemos tomar desde la cola o tomarla antes de la llamada y pasársela.
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


	/**
	 * Gestiona la respuesta que tiene que dar según el tipo de solicitud atendida.
	 *
	 * @param request Identificador de la solicitud.
	 */
	private void manageResponse(byte request){
		switch (request){
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
				// 1º Usar el socket del clienteActivo para esperar el paquete con el nombre del archivo.
				// 2º Enviar archivo.
				//sendFile();
				break;
			case PACKET_ACK:
				// TODO: Puede que packetACK no sea útil aquí...
				break;
			default:
				// Petición no admitida.
				break;
		}
	}


	/**
	 * Se envía un archivo al dispositivo remoto.
	 *
	 * @param addr Dirección IP y puerto al que se envía el archivo.
	 */
	public void sendFile(InetSocketAddress addr){
		try{
			//InetSocketAddress addr = this.friends.get("Manolito");

			// Escribir los datos del archivo aquí.
			// <1 KB.
			//String path = Utils.parseMountDirectory().getAbsolutePath() + "/de_julio.txt";
			// >500 KB.
			//String path = Utils.parseMountDirectory().getAbsolutePath() + "/Resumen ASOR.pdf";
			// 9 KB.
			String path = Utils.parseMountDirectory().getAbsolutePath() + "/contacts.vcf";
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			int fileLength = (int) file.length();

			// TODO: Implementar almacenamiento de metadatos de ficheros ajenos.
			// TODO: ¿Quitar sendFileMetadata() de aquí?
			/*
			 * Si el usuario ha seleccionado el fichero que quiere descargar es porque ya tiene
			 * los metadatos necesarios
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
				listenSocket.send(packet);
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


	/**
	 * Descripción: Envía el nombre y la longitud de un fichero a un dispositivo remoto.
	 * Este método es útil para la recolección de metadatos de los archivos de las carpetas compartidas.
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
		// TODO: Llamar a este método cada vez que se modifique o borre un fichero.
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
			// TODO: No vale con usar el listenSocket, hay que usar otro para las comunicaciones.
			listenSocket.send(p);
			// TODO: Creo que no me falta nada en este método...

		}
		catch (IOException e){
			e.printStackTrace();
		}
	}


}