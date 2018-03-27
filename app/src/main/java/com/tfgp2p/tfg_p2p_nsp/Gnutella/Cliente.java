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

	private static int ppIndex = 0;






	public static Cliente getInstance(){
		if (client == null)
			return new Cliente(Servidor.possiblePorts[ppIndex]);
		else return client;
	}


	// TODO: De momento lo conecto al puerto de escucha y pruebo el envío por ese mismo.
	private Cliente(int listenPort){
		try {
			this.friendsSockets = new ArrayList<>(10);

			//////// Prueba de la conexión al móvil servidor:
			Socket s = new Socket(poner una IP, listenPort);
			s.setReuseAddress(true);

			this.friendsSockets.add(s);
			sendFile();

		} catch (IOException e) {
			if (ppIndex < 4)
				new Cliente(Servidor.possiblePorts[++ppIndex]);
			else
				//TODO: Poner algún tipo de notificación o solución a los puertos cerrados. ¿Abrir otro?
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
