package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;


/**
 * Created by Julio on 25/02/2018.
 *
 * Clase que implementa la parte cliente de la aplicación.
 */

public class Cliente {

	private static Cliente client = null;

	// Colección de amigos que contiene nombres, direcciones y puertos remotos.
	// TODO: Pensar mejor el tipo de datos para la colección de sockets.
	private HashMap<String, InetSocketAddress> friends;

	private DatagramSocket datagramSocket;

	private static int ppIndex = 0;






	public static Cliente getInstance(){
		if (client == null)
			return new Cliente(Servidor.possiblePorts[ppIndex]);
		else return client;
	}


	// TODO: De momento lo conecto al puerto de escucha y pruebo el envío por ese mismo.
	private Cliente(int listenPort){
		try {
			//this.friendsSockets = new ArrayList<>(10);
			this.friends = new HashMap<>(10);
			this.datagramSocket = new DatagramSocket(listenPort);

			//////// Prueba de la conexión al móvil servidor:
			/*Socket s = new Socket(poner una IP, listenPort);
			s.setReuseAddress(true);
			this.friendsSockets.add(s);*/


			InetSocketAddress sa = new InetSocketAddress(Inet4Address.getByName("192.168.0.10"), listenPort);
			this.friends.put("Manolito", sa);

			// TODO: Solicitar el archivo aquí y que lo envíe el servidor.
			sendFile();

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

	}


	/**
	 * Se envía un archivo al dispositivo remoto.
	 */
	public void sendFile(){
		try{
			////////////////////////////////////////
			//Socket s1 = this.friendsSockets.get(0);
			////////////////////////////////////////
			InetSocketAddress addr = this.friends.get("Manolito");

			//DataOutputStream dout= new DataOutputStream(s1.getOutputStream());

			// Escribir los datos del archivo aquí.
			String path = Utils.parseMountDirectory().getAbsolutePath() + "/de_julio.txt";
			//String path = Utils.parseMountDirectory().getAbsolutePath() + "/Resumen ASOR.pdf";
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			int fileLength = (int) file.length();

			// Se envía primero el nombre y el tamaño del archivo.
			sendMetadata(file, addr, fileLength);


			/*ByteBuffer metadataBuffer = ByteBuffer.allocate(file.getName().length() + 4);
			metadataBuffer.putChar()*/

			// Si el archivo es más grande de 64k va a dar problemas.
			byte[] buffer = new byte[1024];
			int totalBytesRead = 0;
			int bytesRead = 0;

			while ((totalBytesRead < fileLength) && (bytesRead != -1)) {
				bytesRead = fis.read(buffer, totalBytesRead, 1024);
				totalBytesRead += bytesRead;
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr.getAddress(), addr.getPort());
				datagramSocket.send(packet);
			}



			/*dout.write(buffer);
			dout.flush();
			*/
			fis.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
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
	private void sendMetadata(File file, InetSocketAddress addr, int fileLength) throws IOException{
		byte[] metadataBuffer = new byte[file.getName().length() + 4];

		// Tamaño del fichero.
		byte[] len = Utils.intToByteArray(fileLength);
		for (int i=0; i<4 ;i++)
			metadataBuffer[i] = len[i];

		// Nombre del fichero.
		byte[] aux = file.getName().getBytes(Charset.forName("UTF-8"));
		//byte[] aux = file.getName().getBytes();
		for (int i=0; i<aux.length; i++)
			metadataBuffer[i+4] = aux[i];

		DatagramPacket metadataPacket = new DatagramPacket(metadataBuffer, metadataBuffer.length, addr.getAddress(), addr.getPort());
		datagramSocket.send(metadataPacket);
	}


}
