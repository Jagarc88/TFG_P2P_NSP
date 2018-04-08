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
			InetSocketAddress addr = this.friends.get("Manolito");

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

			// Se envía primero el nombre y el tamaño del archivo.
			sendMetadata(file, addr, fileLength);

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
				else bytesRead = fis.read(buffer, 0, MAX_BUFF_SIZE);
				totalBytesRead += bytesRead;
				//DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr.getAddress(), addr.getPort());
				//packet.setData(buffer, totalBytesRead-bytesRead, bytesRead);
				packet.setData(buffer);
				datagramSocket.send(packet);
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
