package com.tfgp2p.tfg_p2p_nsp;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Julio on 01/03/2018.
 */

public class Utils {

	/**
	 * Longitud máxima del buffer de datos que se manejará.
	 */
	public static final int MAX_BUFF_SIZE = 1024;
	/**
	 * Constante que identifica un paquete como paquete de solicitud de metadatos de 1 fichero.
	 */
	public static final byte METADATA_REQ_ONE = 1;
	/**
	 * Constante que identifica un paquete como paquete de solicitud de metadatos de todos los
	 * ficheros de la carpeta compartida.
	 */
	public static final byte METADATA_REQ_ALL = 2;
	/**
	 * Constante que identifica un paquete como paquete de solicitud de un fichero.
	 */
	public static final byte FILE_REQ = 3;
	/**
	 * Constante que identifica un paquete como paquete de confirmación de recepción de datos.
	 */
	public static final byte PACKET_ACK = 4;
	/**
	 * Constante que identifica un paquete como paquete de envío de metadatos de 1 fichero.
	 */
	public static final byte METADATA_SEND_1 = 5;
	/**
	 * Cuando el servidor recibe una petición tiene que comprobar si procede de un amigo.
	 * En caso afirmativo envía de vuelta HELLO_FRIEND. En caso negativo envía NO_FRIEND.
	 */
	public static final byte HELLO_FRIEND = 6;
	public static final byte NO_FRIEND = 7;
	/**
	 * Identificador para la primera comunicación entre 2 dispositivos, se le puede llamar PAQUETE DE SALUDO.
	 * Es relevante para la aplicación en el servidor, que debe saber que se trata del inicio de una
	 * comunicación y no una petición.
	 * El segundo dispositivo responderá con HELLO_FRIEND o bien NO_FRIEND.
	 */
	public static final byte HELLO = 8;
	/**
	 * Identificador que la aplicación del servidor debe enviar en primer lugar cuando se detecte que
	 * el cliente quiere realizar algún tipo de petición.
	 */
	public static final byte NEW_REQ = 9;
	/**
	 * Identificador utilizado cuando un dispositivo inicia la app y se conecta al servidor.
	 */
	public static final byte SERVER_CONNECT = 10;
	public static final byte IS_CLIENT_SOCKET = 11;
	public static final byte PUNCHED = 12;

	// TODO: Cada vez que se cree un tipo de identificador de paquete DEBE SER AÑADIDO MANUALMENTE AL HashSet.
	/**
	 * Constante que se usa para comprobar de manera eficiente si un ID de un paquete
	 * de solicitud es válido. Se utiliza en el método isValidRequest().
	 */
	private static final HashSet<Byte> packetID = new HashSet<>(Arrays.asList(
			METADATA_REQ_ONE, METADATA_REQ_ALL, FILE_REQ, PACKET_ACK, METADATA_SEND_1
	));



	/**
	 * Obtiene la dirección del sdcard en el dispositivo que se encuentra abierto en ese momento.
	 * @return
	 */
	public static File parseMountDirectory() {
		File dir_00 = new File("/storage/extSdCard");
		File dir_01 = new File("/storage/sdcard1");
		File dir_1 = new File("/storage/usbcard1");
		File dir_2 = new File("/storage/sdcard0");
		File dir_3 = new File("/mnt/sdcard");
		return dir_01.exists() ? dir_01 : dir_00.exists() ? dir_00 :
				dir_1.exists() ? dir_1 : dir_2.exists() ? dir_2 : dir_3.exists() ? dir_3 :
						null;
	}


	/**
	 * Transforma un int en un array de 4 bytes.
	 *
	 * @param n
	 * @return
	 */
	public static byte[] intToByteArray(int n) {
		return new byte[] {
			(byte)(n >>> 24),
			(byte)(n >>> 16),
			(byte)(n >>> 8),
			(byte) n};
	}


	/**
	 * Transforma un byte[] en un int.
	 *
	 * @param array
	 * @return
	 */
	public static int byteArrayToInt(byte[] array){
		return ByteBuffer.wrap(array).getInt();
	}



	public static boolean isValidRequest(byte req){
		return (packetID.contains(req));
	}

}
