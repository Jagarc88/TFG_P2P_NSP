package com.tfgp2p.tfg_p2p_nsp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tfgp2p.tfg_p2p_nsp.Conexion.Servidor;
import com.tfgp2p.tfg_p2p_nsp.Modelo.Amigos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
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
	public static final byte CLOSE_SOCKET = 12;
	public static final byte TRY_CONNECT = 13;
	public static final byte PUNCH = 14;
	public static final byte TAKE_IP_FROM_HEADER = 0;
	public static final byte PACKET_OK = -1;
	public static final byte CORRUPT_OR_LOST_PACKET = -2;
	//public static final byte MISSING_FROM_NUMBER= -3;

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
	 * Transforma un long en un array de 8 bytes.
	 *
	 * @param n
	 * @return
	 */
	public static byte[] longToByteArray(long n) {
		return new byte[] {
				(byte)(n >>> 56),
				(byte)(n >>> 48),
				(byte)(n >>> 40),
				(byte)(n >>> 32),
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


	/**
	 * Transforma un byte[] en un long.
	 *
	 * @param array
	 * @return
	 */
	public static long byteArrayToLong(byte[] array){
		return ByteBuffer.wrap(array).getLong();
	}



	public static boolean isValidRequest(byte req){
		return (packetID.contains(req));
	}


	/**
	 * Devuelve la dirección IP pública del dispositivo.
	 */
	public static byte[] getIP(DatagramSocket socket, Context context){
		byte[] ip = new byte[1];
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		/*boolean isConnected = activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();
		*/
		int connectionType = activeNetwork.getType();

		switch (connectionType){
			// Si se está conectado a Internet por wifi:
			case ConnectivityManager.TYPE_WIFI:
				ip[0] = TAKE_IP_FROM_HEADER;
				break;
				/*try {
					URL URL = new URL("http://www.whatismyip.org/");
					HttpURLConnection conn = (HttpURLConnection) URL.openConnection();
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(7000);
					InputStream inStream = conn.getInputStream();
					InputStreamReader isr = new InputStreamReader(inStream);
					BufferedReader br = new BufferedReader(isr);
					String str = br.readLine();
					ip = str.getBytes();
					// No funciona. Devuelve null.

				} catch (IOException e){
					e.printStackTrace();
				}
				break;
			*/
			// Si se está conectado a través de la red móvil:
			case ConnectivityManager.TYPE_MOBILE:
				//try {
					//socket.connect(Amigos.getServerInfo(), 15000);
					ip = socket.getLocalAddress().getAddress();
				//} catch (IOException e){ e.printStackTrace();}
				break;
				/*try {
					for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
						NetworkInterface ni = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = ni.getInetAddresses(); enumIpAddr.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()) {
								// TODO: Sale una ip de 32, luego de menos y luego de 31 bytes. ¿IPv6?
								// TODO: Tb falta que pare el bucle cuando tenga la IP.
								ip = inetAddress.getHostAddress().getBytes();
							}
						}
					}
				} catch (SocketException e){
					e.printStackTrace();
				}
				break;
			*/
		}
		return ip;
	}

}
