package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Julio on 25/02/2018.
 *
 * Clase que implementa la parte cliente de la aplicación.
 */

public class Cliente {

	private static Cliente client = null;

	// Colección de amigos con dirección y puerto remoto.
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


			InetSocketAddress sa = new InetSocketAddress(Inet4Address.getByName("192.168.0.11"), listenPort);
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



	// TODO: Pensar en qué tipo de excepción debe lanzar si el envío falla.
	public void sendFile(){
		try{
			////////////////////////////////////////
			//Socket s1 = this.friendsSockets.get(0);
			////////////////////////////////////////
			InetSocketAddress addr = this.friends.get("Manolito");

			//DataOutputStream dout= new DataOutputStream(s1.getOutputStream());

			// Escribir los datos del archivo aquí.
			String path = Utils.parseMountDirectory().getAbsolutePath() + "/de_julio.txt";
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			int fileLength = (int) file.length();

			// Se envía primero el nombre y el tamaño del archivo.
			byte[] metadataBuffer = new byte[file.getName().length() + 4];
			metadataBuffer = file.getName().getBytes(Charset.forName("UTF-8"));
			byte[] len = toByteArray(fileLength);
			// TODO: Falta meter el tamaño del archivo en el buffer de metadatos, enviarlo, y gestionarlo en el servidor.

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

	private byte[] toByteArray(int fileLength) {
		byte[] array = new byte[4];
		sdasdfasdf

				
	}


}
