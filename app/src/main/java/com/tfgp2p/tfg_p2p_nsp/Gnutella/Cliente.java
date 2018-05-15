package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Amigos;
import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.tfgp2p.tfg_p2p_nsp.Utils.*;


/**
 * Created by Julio on 25/02/2018.
 *
 * Clase que implementa la parte cliente de la aplicación.
 */

public class Cliente {

	private static Cliente client = null;

	// Colección de amigos que contiene nombres, direcciones y puertos remotos.
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
			/////////// BORRAR AÑADIDO MANUAL DE UN AMIGO, borrar tb los catch////////////////

			InetSocketAddress sa = new InetSocketAddress(Inet4Address.getByName("192.168.0.12"), listenPort);
			this.amigos.addFriend("Manolito", sa);

			String fileName = "contacts.vcf";
			String name = "Manolito";
			InetSocketAddress friendAddr = amigos.getFriendAddr(name);
			//////////////////////////////////////////////////////////////////////////////////
			//////// BORRAR ENVÍO MANUAL DE PETICIÓN DE FICHERO //////////////////////////////
			// Yo tb me llamo Manolito:
			try {
				byte[] nameLen = new byte[] {(byte) name.length()};
				byte[] nameAux = name.getBytes();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(nameLen);
				baos.write(nameAux);
				byte[] nameBuff = baos.toByteArray();
				DatagramPacket p = new DatagramPacket(nameBuff, nameBuff.length, friendAddr);
				socket.send(p);
			}
			catch (IOException e){
				e.printStackTrace();
			}
			///////////////////////////////////////////////

			// TODO: La siguiente comprobación debe ir en el sendRequest(). Ya lo meteré cuando organice el envío de todo tipo de peticiones.
			try {
				byte[] resp = new byte[1];
				DatagramPacket pac = new DatagramPacket(resp, resp.length);
				socket.receive(pac);
				if (resp[0] == OK_FRIEND) {
					requestFile(fileName, name);
					receiveFile(fileName);
				}
				// Si no es amigo pensar por qué ha llegado a este punto. No debería poder hacer peticiones a no amigos.
				else if (resp[0] == NO_FRIEND) {}
			}
			catch (IOException e) {
				e.printStackTrace();
			}


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


	}


	/* Pasos para hacer una petición:
	 * 1º) Enviar un paquete con el nombre del cliente para que el servidor pueda comprobar
	 *     si es amigo (y por tanto atenderle) o no. Se envía longitud del nombre y el nombre.
	 *
	 * 2º) En caso de que sea amigo se envía la petición como tal. Los datos enviados son el
	 *     identificador de la petición (tipo) y otros datos dependiendo del tipo. Los otros datos
	 *     son, por ejemplo, el nombre de un fichero en el caso de que la petición sea de
	 *     descarga de ese fichero.
	 */
	public void sendRequest(){
		// TODO: Implementar una cola de espera de salida de peticiones para cuando el móvil destino está sin conexión.
		// TODO: Enviar en la petición el nombre de mi dispositivo

	}


	/**
	 * Solicita el fichero seleccionado en la carpeta compartida del amigo y lo descarga.
	 * El paquete que se envía con la petición contiene la siguiente información en este orden:
	 * 1º Tipo de petición (FILE_REQ).
	 * 2º Nombre del usuario que manda la petición.
	 * 3º Nombre del fichero solicitado.
	 *
	 * @param fileName Nombre del fichero solicitado.
	 * @param name Nombre del amigo que tiene el fichero.
	 */
	private void requestFile(String fileName, String name) {
		try{
			// TODO FALTA REESCRIBIR BIEN ESTE METODO!!!! edit: puede que así valga.
			// Se envia FILE_REQ + nombre del archivo.

			InetSocketAddress addr = amigos.getFriendAddr(name);
			// Hay que enviar en el byte[] FILE_REQ (un 3), la longitud del archivo, y el nombre del archivo.
			byte[] reqType = new byte[1];
			reqType[0] = Utils.FILE_REQ;
			byte[] fLength = {(byte) fileName.length()};
			byte[] fnBuffer = fileName.getBytes();

			ByteArrayOutputStream s = new ByteArrayOutputStream();
			s.write(reqType);
			s.write(fLength);
			s.write(fnBuffer);
			byte[] completeBuffer = s.toByteArray();

			DatagramPacket request = new DatagramPacket(completeBuffer, completeBuffer.length, addr.getAddress(), addr.getPort());
			socket.send(request);
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
			// TODO: Para recibir un archivo no hay que devolver el nombre en el metadataBuffer, solo el tamaño.
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


	// TODO: (Opcional) Implementar cancelación y pausa de descargas.
}
