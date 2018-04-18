package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros;

public class Fichero implements Comparable<Fichero>{

    private TipoFichero tipoFichero;
    private String path;
    private String nombre;

    public Fichero(){
        this.nombre = null;
        this.path = null;
        this.tipoFichero = null;
    }

    public Fichero(String nombre, String path){
        this.nombre = nombre;
        this.path = path;
        this.tipoFichero = TipoFichero.obtainTipoFichero(nombre);
    }

    public int compareTo(Fichero o) {
        if(this.nombre != null)
            return this.nombre.toLowerCase().compareTo(o.getNombre().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
    //-------------------------------------------

    

    //---------- GETTERS && SETTERS -------------
    public TipoFichero getTipoFichero() {
        return tipoFichero;
    }

    public void setTipoFichero(TipoFichero tipoFichero) {
        this.tipoFichero = tipoFichero;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
