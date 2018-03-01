package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by Julio on 24/02/2018.
 *
 * Clase que implementa la parte servidor de la aplicación.
 */

/*
 * Sería óptimo tener un hilo recibiendo las conexiones entrantes y hasta 5
 * proveyendo ficheros (a 5 clientes).
 */
public class Servidor {

	private static Servidor server = null;

	// Puerto en el que se escucha a conexiones entrantes.
	private int listenTcpPort;
	private ServerSocket listenSocket;

	// Colección de sockets conectados a los clientes.
	// TODO: Pensar mejor el tipo de datos para la colección de sockets.
	private ArrayList<Socket> clientsSockets;

	/*
	 * Conjunto de pares identificador / IP+puerto de los amigos conectados.
	 * Cuando un amigo finalice la conexión, también debe ser borrado de esta variable.
	 */
	private HashMap<String, InetSocketAddress> activeClients;





	/**
	 * Devuelve el objeto servidor. Si este no existe se crea.
	 *
	 * @param tcpPort
	 * @return objeto servidor.
	 */
	public static Servidor getInstance(int tcpPort){
		if (server == null)
			return new Servidor(tcpPort);
		else return server;
	}



	private Servidor(int tcpPort) {
		try {
			/*
			 * Con tcpPort=0 el constructor genera un puerto automáticamente.
			 * El puerto obtenido se puede ver con getLocalPort().
			 */

			// 5 conexiones pendientes como máximo por defecto.
			this.listenSocket = new ServerSocket(tcpPort, 5);
			this.listenTcpPort = tcpPort;
			this.activeClients = new HashMap<>(10);
			this.clientsSockets = new ArrayList<>(10);

			// La parte servidor lanza un hilo que se queda a la escucha.
			new Thread(new Runnable() {
				@Override
				public void run() {
					listen();
				}
			}).start();

			//}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método principal de la parte servidor. Se queda bloqueado hasta
	 * que llegue una conexión entrante.
	 */
    public void listen(){
		try {
			while (true){
				Socket incomingSocket = listenSocket.accept();
				// Probando la conexión:

				this.clientsSockets.add(incomingSocket);

				/////////////////////////////////////////////////////////////////////////

				/*// Si el que se quiere conectar no está en la lista de clientes activos...
				if (this.activeClients.containsKey())
					//this.clients.add(incomingSocket);

				// El que se quiere conectar está pero ha cambiado su IP...
				else if () {
					this.clients.remove(incomingSocket);
				}
				*/
				manageResponse();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Aquí se gestiona la respuesta. Opciones:
	 *
	 * - Si el fichero existe (porque lo sabe el cliente por los metadatos que tiene)
	 *   se proporciona.
	 * - Si no, se responde a la consulta, proporcionando el fichero buscado o inundando
	 *   a los demás con la misma consulta.
	 */
	private void manageResponse(){
		/////////////// Prueba de la recepción del fichero.////////////////////////
		// Seguramente la recepción de un fichero debería estar implementada en otro método.

		DataInputStream din;
		try {
			/*if (!this.clientsSockets.contains())
				throw new Exception("No está el socket");
			clientsSockets.indexOf();*/
			////////////////////////////////////////////
			Socket s2 = clientsSockets.get(0);
			///////////////////////////////////////////
			din = new DataInputStream(s2.getInputStream());

			FileOutputStream fos = new FileOutputStream(Utils.parseMountDirectory().getAbsolutePath() + "/de_julio.txt");
			byte[] buffer = new byte[4096];

			int read = 0;
			int totalRead = 0;
			int remaining = 1024; //Falta saber cómo mando el tamaño exacto del archivo para no liarla en el envío.
			while((read = din.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
				totalRead += read;
				remaining -= read;
				System.out.println("read " + totalRead + " bytes.");
				fos.write(buffer, 0, read);
			}

			fos.close();

			/////////////////////////////////////////////////////////////////
			din.close();
			//}
		}
		catch (IOException e) {
			e.printStackTrace();
			//din.close();
		}

		///////////////////////////////////////////////////////////////////////////
	}

	// TODO: Pensar en qué hay que hacer cuando un móvil se desconecta del móvil servidor.

}
