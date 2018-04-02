package com.tfgp2p.tfg_p2p_nsp;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Created by Julio on 01/03/2018.
 */

public class Utils {

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



}
