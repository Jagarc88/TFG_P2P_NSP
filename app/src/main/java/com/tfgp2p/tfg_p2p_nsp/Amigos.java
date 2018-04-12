package com.tfgp2p.tfg_p2p_nsp;

import java.net.InetSocketAddress;
import java.util.HashMap;

/**
 * Created by Julio on 09/04/2018.
 */
public class Amigos {

	// TODO: Implementar el guardado de amigos en un fichero o base de datos local.
	// TODO: Implementar la carga de amigos.

	// Colección de amigos que contiene nombres, direcciones y puertos remotos.
	private HashMap<String, InetSocketAddress> friendsMap;

	private static Amigos amigos = null;



	public static Amigos getInstance(){
		if (amigos == null)
			amigos = new Amigos();
		return amigos;
	}


	private Amigos(){
		// Hasta 16 amigos por defecto.
		this.friendsMap = new HashMap<>();
	}


	public void addFriend(String name, InetSocketAddress iaddr) throws AlertException{
		if (!this.friendsMap.containsKey(name))
			this.friendsMap.put(name, iaddr);
		else
			throw new AlertException(name + " ya existe, introduce otro nombre o modifica el antiguo amigo.");
	}


	public void editFriendName(String name, String newName) throws AlertException{
		if (this.friendsMap.containsKey(name)){
			InetSocketAddress addr = this.friendsMap.get(name);
			this.friendsMap.remove(name);
			this.friendsMap.put(newName, addr);
		}
		else throw new AlertException(name + " no existe.");
	}


	public void editFriendAddr(String name, InetSocketAddress newAddr) throws AlertException{
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


	public boolean isFriend(String name, InetSocketAddress addr){
		// get() Devuelve null si no existe.
		InetSocketAddress localAddr = friendsMap.get(name);
		return ((localAddr != null) && (localAddr == addr));
	}

}
