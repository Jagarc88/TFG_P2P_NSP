package com.tfgp2p.tfg_p2p_nsp.Modelo;

import com.tfgp2p.tfg_p2p_nsp.AlertException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * Created by Julio on 09/04/2018.
 */
public class Amigos {

	private static String myName;

	// TODO: ¿Cuáles son los pasos para añadir un amigo?
	// TODO: Implementar el guardado de amigos en un fichero o base de datos local.
	// TODO: Implementar la carga de amigos.

	// Colección de amigos que contiene nombres, direcciones y puertos remotos.
	// TODO: Quitar dirección de los amigos y poner alguna clave aleatoria.
	private HashMap<String, InetSocketAddress> friendsMap;

	private static Amigos amigos = null;



	public static Amigos getInstance(){
		if (amigos == null)
			amigos = new Amigos();
		return amigos;
	}


	private Amigos(){
		/* De momento ponemos nuestro nombre desde el código. Habría que poner una opción en la
		 * configuración de la aplicación. Tb deberíamos hacer que el usuario introduzca su nombre
		 * la primera vez que ejecuta el programa.
		 */
		myName = "Pedro";
		//myName = "Manolito";

		// Hasta 16 amigos por defecto.
		this.friendsMap = new HashMap<>();
	}


	public void addFriend(String name, InetAddress addr, int port) throws AlertException {
		if (!this.friendsMap.containsKey(name)) {
			InetSocketAddress iaddr = new InetSocketAddress(addr, port);
			this.friendsMap.put(name, iaddr);
		}
		else
			throw new AlertException(name + " ya existe, introduce otro nombre o modifica el antiguo amigo.");
	}


	public void updateFriendName(String name, String newName) throws AlertException{
		if (this.friendsMap.containsKey(name)){
			InetSocketAddress addr = this.friendsMap.get(name);
			this.friendsMap.remove(name);
			this.friendsMap.put(newName, addr);
		}
		else throw new AlertException(name + " no existe.");
	}


	public void updateFriendAddr(String name, InetSocketAddress newAddr) throws AlertException{
		if (this.friendsMap.containsKey(name)){
			this.friendsMap.remove(name);
			this.friendsMap.put(name, newAddr);
		}
		else throw new AlertException(name + " no existe.");
	}


	public void removeFriend(String name) throws AlertException{
		if (this.friendsMap.containsKey(name)){
			this.friendsMap.remove(name);
		}
		else throw new AlertException(name + " no existe.");
	}


	public HashMap<String, InetSocketAddress> getFriendsMap(){
		return this.friendsMap;
	}


	public InetSocketAddress getFriendAddr(String name) throws AlertException{
		if (this.friendsMap.containsKey(name)){
			return this.friendsMap.get(name);
		}
		else throw new AlertException(name + " no existe.");
	}


	public boolean isFriend(String name, InetAddress addr){
		try {
			InetAddress localAddr = friendsMap.get(name).getAddress();
			return localAddr.equals(addr);
		}
		catch (NullPointerException e){
			return false;
		}
	}


	public static String getMyName(){
		return myName;
	}


	public static void setMyName(String newname){
		myName = newname;
	}



}
