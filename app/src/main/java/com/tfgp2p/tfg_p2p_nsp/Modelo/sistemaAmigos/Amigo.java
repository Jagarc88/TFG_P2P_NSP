package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos;

public class Amigo {

    private String nombreAmigo;
    private int id;
    private int state;

    public Amigo(String nombreAmigo, int id, int state) {
        this.nombreAmigo = nombreAmigo;
        this.id = id;
        this.state = state;
    }

    public String getNombreAmigo() {
        return nombreAmigo;
    }

    public void setNombreAmigo(String nombreAmigo) {
        this.nombreAmigo = nombreAmigo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
