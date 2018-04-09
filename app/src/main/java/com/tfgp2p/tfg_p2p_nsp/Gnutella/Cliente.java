package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Amigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;

import static com.tfgp2p.tfg_p2p_nsp.Utils.MAX_BUFF_SIZE;


/**
 * Created by Julio on 25/02/2018.
 *
 * Clase que implementa la parte cliente de la aplicación.
 */

public class Cliente {

	private static Cliente client = null;

	// Colección de amigos que contiene nombres, direcciones y puertos remotos.
	// TODO: Pensar mejor el tipo de datos para la colección de sockets.
	// TODO: borrar de aquí cuando haya terminado de pasar la implementación a la clase Amigos.
	//private HashMap<String, InetSocketAddress> friends;
	private Amigos amigos;

	private DatagramSocket socket;

	private static int ppIndex = 0;






	public static Cliente getInstance(){
		if (client == null)
			client = new Cliente(Servidor.possiblePorts[ppIndex]);
		return client;
	}


	// TODO: Limpiar código del constructor que no debe estar.
	private Cliente(int listenPort){
		try {
			//this.friendsSockets = new ArrayList<>(10);
			//this.friends = new HashMap<>(10);
			this.amigos = Amigos.getInstance();
			this.socket = new DatagramSocket(listenPort);

			//////// Prueba de la conexión al móvil servidor:

			InetSocketAddress sa = new InetSocketAddress(Inet4Address.getByName("192.168.0.10"), listenPort);
			this.amigos.addFriend("Manolito", sa);

			String fileName = "contacts.vcf";
			String name = "Manolito";
			InetSocketAddress friend = amigos.getFriendAddr(name);
			requestFile(fileName, name);
			//sendFile();




		}
		catch (SocketException e){
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			if (ppIndex < 4)
				new Cliente(Servidor.possiblePorts[++ppIndex]);
			else
				e.printStackTrace();
		}
		catch (UnknownHostException e){
			e.printStackTrace();
		}
		catch (AlertException e){
			e.showAlert();
		}
		catch (IOException e){
			e.printStackTrace();
		}

	}

	/**
	 * Solicita el fichero seleccionado en la carpeta compartida del amigo y lo descarga.
	 *
	 * @param fileName Nombre del fichero solicitado.
	 * @param name Nombre del amigo que tiene el fichero.
	 */
	private void requestFile(String fileName, String name) {
		try{
			InetSocketAddress addr = amigos.getFriendAddr(name);
			byte[] fnBuffer = fileName.getBytes();
			DatagramPacket request = new DatagramPacket(fnBuffer, fnBuffer.length, addr.getAddress(), addr.getPort());
			socket.send(request);
			// TODO: Implementar receiveFile(...).
			receiveFile(fileName);
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
			// TODO: Para pedir un archivo no hay que devolver el nombre en el metadataBuffer, solo el tamaño.
			byte[] metadataBuffer = new byte[100];
			DatagramPacket metadataPacket = new DatagramPacket(metadataBuffer, metadataBuffer.length);
			///////////////////////////////////////////////////////////////////////////////////

			socket.receive(metadataPacket);

			byte[] aux = new byte[4];
			for (int i = 0; i < 4; i++)
				aux[i] = metadataBuffer[i];
			int size = Utils.byteArrayToInt(aux);
			byte[] dataBuffer = new byte[MAX_BUFF_SIZE];
			DatagramPacket dataPacket = new DatagramPacket(dataBuffer, dataBuffer.length);

			socket.receive(dataPacket);

			// TODO: no es necesario el nombre...
			//String fileName = getFileNameFromBuffer(metadataBuffer);
			// TODO: Quitar lo de +".txt".
			FileOutputStream fos = new FileOutputStream(Utils.parseMountDirectory().getAbsolutePath() + '/' + fileName + ".txt");
			int n = 0;
			boolean exit = false;

			while (!exit) {
				//TODO Implementar algún tipo de verificación de paquetes. Por ejemplo, cuando se reciba 1 mandar mensaje de confirmación.
				//byte[] data = dataPacket.getData();
				//fos.write(data, dataPacket.getOffset(), data.length);
				//fos.write(data, n*MAX_BUFF_SIZE, data.length);
				fos.write(dataBuffer, 0, dataBuffer.length);
				//++n;
				// TODO: Comprobar que con esta condición se reciben los datos correspondientes al final del archivo.
				if (dataPacket.getLength() < MAX_BUFF_SIZE)
					socket.receive(dataPacket);
				else
					exit = true;
			}

			fos.close();

					/*String fileName = getFileNameFromBuffer(metadataBuffer);
					manageResponse(dataPacket, fileName);
					*/
			///////////////////////////////////////////////////////////////////
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


}
