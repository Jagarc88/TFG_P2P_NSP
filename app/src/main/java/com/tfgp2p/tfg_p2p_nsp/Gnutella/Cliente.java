package com.tfgp2p.tfg_p2p_nsp.Gnutella;

import com.tfgp2p.tfg_p2p_nsp.Utils;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Julio on 25/02/2018.
 *
 * Clase que implementa la parte cliente de la aplicación.
 */

public class Cliente {

	private static Cliente client = null;

	// Colección de sockets conectados a los servidores.
	// TODO: Pensar mejor el tipo de datos para la colección de sockets.
	private ArrayList<Socket> friendsSockets;






	public static Cliente getInstance(){
		if (client == null)
			return new Cliente();
		else return client;
	}


	private Cliente(){
		try {
			this.friendsSockets = new ArrayList<>(10);

			//////// Prueba de la conexión al móvil servidor:
			// Con el constructor que he estado probando hasta ahora:
			//Socket s = new Socket("192.168.0.11", 1103);
			// Con el constructor más completo:
			InetAddress address = InetAddress.getByName("2.153.114.70");
			Socket s = new Socket(address, 1103);
			//Socket s = new Socket(address, 1103, dirlocal, puertolocal);
			//

			this.friendsSockets.add(s);
			sendFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}



	// TODO: Pensar en qué tipo de excepción debe lanzar si el envío falla.
	public void sendFile(){
		try{
			////////////////////////////////////////
			Socket s1 = this.friendsSockets.get(0);
			////////////////////////////////////////

			DataOutputStream dout= new DataOutputStream(s1.getOutputStream());

			// Escribir los datos del archivo aquí.
			String path = Utils.parseMountDirectory().getAbsolutePath() + "/de_julio.txt";
			FileInputStream fis = new FileInputStream(path);
			byte[] buffer = new byte[4096];

			while (fis.read(buffer) > 0) {
				dout.write(buffer);
			}
			dout.flush();
			fis.close();

			///////////////////////////////////////////////////////////////////////////
			//dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
