package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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


	// TODO: Sería óptimo tener un hilo recibiendo las conexiones entrantes y hasta 5 proveyendo ficheros (a 5 clientes).
public class Servidor {

	// Instancia del objeto Servidor.
	private static Servidor server = null;

	// Puerto en el que se escucha a conexiones entrantes.
	//private int listenTCPPort;
	private int listenUDPPort;

	//private ServerSocket listenSocket;
	private DatagramSocket listenSocket;

	// Puertos posibles en los que va a estar a la escucha el serverSocket.
	static final int possiblePorts[] = {61516, 62516, 63516, 64516};
	/*
	 * Índice del puerto a escoger. Es útil para llamar de nuevo al constructor del objeto
	 * Servidor si el puerto al que apunta el índice está cerrado o en uso.
	 */
	private static int ppIndex = 0;
	// Colección de sockets conectados a los clientes.
	//private ArrayList<Socket> clientsSockets;
	// TODO: Pensar mejor el tipo de datos para la colección de sockets.
	// Puede que ya no sea necesario almacenar los sockets si trabajamos con UDP.

	/*
	 * Conjunto de pares identificador / IP+puerto de los amigos conectados.
	 * Cuando un amigo finalice la conexión, también debe ser borrado de esta variable.
	 */
	private HashMap<String, InetSocketAddress> activeClients;





	/**
	 * Devuelve el objeto servidor. Si este no existe se crea.
	 *
	 * @return objeto servidor.
	 */
	public static Servidor getInstance(){
		if (server == null)
			return new Servidor(possiblePorts[ppIndex]);
		else return server;
	}



	private Servidor(int port) {
		try {
			this.listenSocket = new DatagramSocket(port);
			this.listenSocket.setReuseAddress(true);

			// Para chequear si asigna bien el puerto:
			this.listenUDPPort = this.listenSocket.getLocalPort();

			this.activeClients = new HashMap<>(10);

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
	 */
    public void listen(){
		try {
			while (true){
				//TODO: Crear método que distinga entre byte[] de metadatos y byte[] de datos de un fichero completo.

				////////////////////////////////////////////////////////////////////////////////////
				// De momento pillo aquí el buffer de metadatos.
				byte[] metadataBuffer = new byte[100];
				DatagramPacket metadataPacket = new DatagramPacket(metadataBuffer, metadataBuffer.length);
				///////////////////////////////////////////////////////////////////////////////////

				listenSocket.receive(metadataPacket);

				byte[] aux = new byte[4];
				for (int i=0; i<4; i++)
					aux[i] = metadataBuffer[i];
				int size = Utils.byteArrayToInt(aux);
				byte[] dataBuffer = new byte[size];
				DatagramPacket dataPacket = new DatagramPacket(dataBuffer, dataBuffer.length);

				// TODO: Hacer bucle while para recibir varios paquetes.
				listenSocket.receive(dataPacket);

				String fileName = getFileNameFromBuffer(metadataBuffer);
				FileOutputStream fos = new FileOutputStream(Utils.parseMountDirectory().getAbsolutePath() + '/' + fileName);
				/*
				 * Si el offset del paquete es == a la longitud del buffer en el que se almacenan los datos
				 * entonces es el último paquete.
				 * Si no, se sigue en bucle recibiendo paquetes.
				 */
				if (dataPacket.getOffset() < size){
					// TODO: Comprobar que es de 1024 bytes.
					byte[] data = dataPacket.getData();
					fos.write(data, dataPacket.getOffset(), data.length);
				}
				else {
				}

				/*String fileName = getFileNameFromBuffer(metadataBuffer);
				manageResponse(dataPacket, fileName);
				*/
				// TODO: Acordarme de cerrar el socket en el método que cierre la conexión. Por ahora lo cierro aquí.

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	/**
	 * Gestiona la respuesta que se le da al cliente.
	 *
	 * @param packet
	 */
	// TODO: Hacer este método más genérico. Que distinga entre solicitud y envío de metadatos de la carpeta compartida, y envío de un fichero.
	// TODO: No vale con pasarle 1 packet puesto que pueden venir más.
	private void manageResponse(DatagramPacket packet, String name){
		////////////////BORRAR///// Prueba de la recepción del fichero.////////////////////////

		try {
			byte[] data = packet.getData();
			FileOutputStream fos = new FileOutputStream(Utils.parseMountDirectory().getAbsolutePath() + '/' + name);
			fos.write(data);

		}
		catch (IOException e) {
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

}
