package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos;

import com.tfgp2p.tfg_p2p_nsp.AlertException;
import com.tfgp2p.tfg_p2p_nsp.Controlador.AplicacionMain;
import com.tfgp2p.tfg_p2p_nsp.Controlador.Observers.Amigo.OberserverAmigos;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHAmigos;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.Exception.DatabaseNotLoadedException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.DAO;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorSistemaAmigos {

    public static final int STATE_USER_OFFLINE = 0;
    public static final int STATE_USER_ONLINE = 1;

    private static HashMap<String, Amigo> friendsMap;

    private static GestorSistemaAmigos gestorSistemaAmigos = null;

    private static String myName;

    // TODO: ¿Cuáles son los pasos para añadir un amigo?
    // Colección de amigos que contiene nombres, direcciones y puertos remotos.
    // TODO: Quitar dirección de los amigos y poner alguna clave aleatoria.

    private GestorSistemaAmigos(){
        /* De momento ponemos nuestro nombre desde el código. Habría que poner una opción en la
         * configuración de la aplicación. Tb deberíamos hacer que el usuario introduzca su nombre
         * la primera vez que ejecuta el programa.
         */
        //myName = "Pedro";
        myName = "Manolito";

        // Hasta 16 amigos por defecto.
        this.friendsMap = getFriendsMap();
    }

    public static List<Amigo> getAmigoList(){
        return new ArrayList(getFriendsMap().values());
    }

    public static GestorSistemaAmigos getInstance(){
        if(gestorSistemaAmigos==null){
            gestorSistemaAmigos=new GestorSistemaAmigos();
        }
        return gestorSistemaAmigos;
    }

    public static void addFriend(Amigo amigo) throws AlertException {
        if(amigo.getInetSocketAddress()!=null){
            addFriend(
                    amigo.getNombreAmigo(),
                    amigo.getId(),
                    amigo.getInetSocketAddress().getAddress(),
                    amigo.getInetSocketAddress().getPort());
        } else{
            addFriend(
                    amigo.getNombreAmigo(),
                    amigo.getId());
        }
        OberserverAmigos.refreshAllViewsAmigosList();
    }

    public static void addFriend(String name, String id, InetAddress addr, int port) throws AlertException {
        if (!friendsMap.containsKey(name)) {
            InetSocketAddress iaddr = new InetSocketAddress(addr, port);
            Amigo amigo = new Amigo(name,id,GestorSistemaAmigos.STATE_USER_OFFLINE,iaddr);
            friendsMap.put(name, amigo);
            try {
                DAO.databaseAmigos().addElement(amigo);
            }catch(DatabaseNotLoadedException e){
                e.printStackTrace();
            }
            OberserverAmigos.refreshAllViewsAmigosList();
        }
        else
            throw new AlertException(name + " ya existe, introduce otro nombre o modifica el antiguo amigo.");
    }

    public static void addFriend(String name, String id) throws AlertException {
        if (!friendsMap.containsKey(name)) {
            Amigo amigo = new Amigo(name,id,GestorSistemaAmigos.STATE_USER_OFFLINE);
            friendsMap.put(name, amigo);
            try {
                DAO.databaseAmigos().addElement(amigo);
            }catch(DatabaseNotLoadedException e){
                e.printStackTrace();
            }
            OberserverAmigos.refreshAllViewsAmigosList();
        }
        else
            throw new AlertException(name + " ya existe, introduce otro nombre o modifica el antiguo amigo.");
    }

    public void updateFriendName(String name, String newName) throws AlertException{
        if (this.friendsMap.containsKey(name)){
            Amigo amigo = this.friendsMap.get(name);
            this.friendsMap.remove(name);
            this.friendsMap.put(name, amigo);
             try{
                DAO.databaseAmigos().updateElement(amigo);
            }catch(DatabaseNotLoadedException e){
                e.printStackTrace();
            }
            OberserverAmigos.refreshAllViewsAmigosList();
        }
        else throw new AlertException(name + " no existe.");
    }

    public static void deleteFriend(Amigo amigo) throws AlertException{
        String name = amigo.getNombreAmigo();
        if (friendsMap.containsKey(name)) {
            friendsMap.remove(name);
            try {
                System.out.println("Numero de elementos eliminados: "+DAO.databaseAmigos().deleteElementByKey(amigo.getId()));
            }catch(DatabaseNotLoadedException e){
                e.printStackTrace();
            }
            OberserverAmigos.refreshAllViewsAmigosList();
        }
        else
            throw new AlertException(name + " no existe, revise la seleccion.");
    }

    public static void deleteFriendsList(List<Amigo> listAmigos){
        try {
            for (Amigo selectedAmigo: listAmigos ) {
                GestorSistemaAmigos.deleteFriend(selectedAmigo);
            }
        } catch (AlertException e) {
            e.printStackTrace();
        }
    }

    public void updateFriendAddr(String name, InetSocketAddress newAddr) throws AlertException{
        if (this.friendsMap.containsKey(name)){
            this.friendsMap.get(name).setInetSocketAddress(newAddr);
        }
        else throw new AlertException(name + " no existe.");
    }


    public static HashMap<String, Amigo> getFriendsMap(){
        friendsMap = DAO.databaseAmigos().getAllAmigos();
        return friendsMap;
    }


    public InetSocketAddress getFriendAddr(String name) throws AlertException{
        if (this.friendsMap.containsKey(name)){
            return this.friendsMap.get(name).getInetSocketAddress();
        }
        else throw new AlertException(name + " no existe.");
    }


    public boolean isFriend(String name, InetAddress addr){
        try {
            InetAddress localAddr = friendsMap.get(name).getInetSocketAddress().getAddress();
            return localAddr.equals(addr);
        }
        catch (NullPointerException e){
            return false;
        }
    }

    /**
     * Carga los datos desde la base de datos
     */
    public HashMap<String,Amigo> loadAmigosFromBBDD(){
        DHAmigos dhAmigos = DAO.databaseAmigos();
        try {
            dhAmigos.reloadBBDD();
            Collection<Map<String, Object>> amigoCol = dhAmigos.getAllData().values();
            for (Map<String, Object> amigoMapBBDD : amigoCol) {
                Amigo amigo = new Amigo(
                        (String) amigoMapBBDD.get(DHAmigos.COL_NOMBRE_AMIGO),
                        (String) amigoMapBBDD.get(DHAmigos.COL_ID_AMIGO),
                        GestorSistemaAmigos.STATE_USER_OFFLINE);
                friendsMap.put(amigo.getNombreAmigo(), amigo);
            }
        } catch(DatabaseNotLoadedException e){
            e.printStackTrace();
        }
        return friendsMap;
    }

    public static String getMyName(){
        return myName;
    }


    public static void setMyName(String newname){
        myName = newname;
    }

    public static List<Fichero> getFicheroListFromAmigo(Amigo amigo){
        // TODO Obtener la lista de ficheros de un amigo usando la conexion
        List<Fichero> ficheroList = new ArrayList<>();
/*
        if(amigo.getNombreAmigo().equals("Manuel")){
            ficheroList.add(new Fichero("fichero1.mp3","/downloads/manuel"));
            ficheroList.add(new Fichero("fichero2.mp3","/downloads/manuel"));
            ficheroList.add(new Fichero("fichero3.mp3","/downloads/manuel"));
        }
        if(amigo.getNombreAmigo().equals("Antonio")){
            ficheroList.add(new Fichero("fichero11.mp3","/downloads/antonio"));
            ficheroList.add(new Fichero("fichero12.mp3","/downloads/antonio"));
        }
        if(amigo.getNombreAmigo().equals("Sonia")){
            ficheroList.add(new Fichero("fichero21.mp3","/downloads/sonia"));
            ficheroList.add(new Fichero("fichero22.mp3","/downloads/sonia"));
            ficheroList.add(new Fichero("fichero23.mp3","/downloads/sonia"));
            ficheroList.add(new Fichero("fichero24.mp3","/downloads/sonia"));
        }
*/
        return ficheroList;
    }
}
