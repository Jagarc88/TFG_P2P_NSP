package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;


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
	private ServerSocket serverSocket;

	// Conjunto de sockets conectados a los clientes.
	private HashSet<Socket> clientsSockets;

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
	public Servidor getInstance(int tcpPort){
		if (server == null)
			return new Servidor(tcpPort);
		else return server;
	}



	private Servidor(int tcpPort){
        try {
            //if ((tcpPort>=1) && (tcpPort<=60000) && (udpPort>=1) && (udpPort<=60000)){
				/*
				Con tcpPort=0 el constructor genera un puerto automáticamente.
				El puerto obtenido se puede ver con getLocalPort().
				 */

				this.clientsSockets = new HashSet<>(10);
				// 5 conexiones pendientes como máximo por defecto.
				this.serverSocket = new ServerSocket(tcpPort, 5);
				this.listenTcpPort = tcpPort;
				this.activeClients = new HashMap<>(10);


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
				Socket incomingSocket = serverSocket.accept();

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

	}

	// TODO: Pensar en qué hay que hacer cuando un móvil se desconecta del móvil servidor.

}
