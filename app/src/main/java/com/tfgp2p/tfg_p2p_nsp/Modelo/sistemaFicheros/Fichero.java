package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros;

public class Fichero implements Comparable<Fichero>{

    private TipoFichero tipoFichero;
    private String path;
    private String nombre;
    private long cantidadDescargada;
    private long tamanyoTotal;
    private boolean incompleto;

    public Fichero(){
        this.nombre = null;
        this.path = null;
        this.tipoFichero = null;
    }

    public Fichero(String nombre, String path){
        this.nombre = nombre;
        this.path = path;
        this.tipoFichero = TipoFichero.obtainTipoFichero(nombre);
        incompleto = false;
        completarInformacionDescarga(tipoFichero);
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

    public long getCantidadDescargada() {
        return cantidadDescargada;
    }

    public void setCantidadDescargada(long cantidadDescargada) {
        this.cantidadDescargada = cantidadDescargada;
    }

    public long getTamanyoTotal() {
        return tamanyoTotal;
    }

    public void setTamanyoTotal(long tamanyoTotal) {
        this.tamanyoTotal = tamanyoTotal;
    }

    public boolean isIncompleto() {
        return incompleto;
    }

    public void setIncompleto(boolean incompleto) {
        this.incompleto = incompleto;
    }

    public float getProcentageDescargado(){
        return 100*cantidadDescargada/tamanyoTotal;
    }

    /**
     * Rellena los datos del fichero en incompleto en descarga si es necesario
     * @param tipoFichero
     * @return
     */
    private void completarInformacionDescarga(TipoFichero tipoFichero){
        if(tipoFichero == TipoFichero.DOWNLOADING){
            incompleto = true;
        }

        // TODO Determinar la extraccion de la información de los datos
    }
}
