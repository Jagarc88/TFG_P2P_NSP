package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos;


import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.IAlmacenableBBDD;

import java.net.InetSocketAddress;

public class Amigo implements IAlmacenableBBDD{

    private String nombreAmigo;
    private String id;
    private int state;
    private InetSocketAddress inetSocketAddress;

    public Amigo(String nombreAmigo, String id, int state) {
        this.nombreAmigo = nombreAmigo;
        this.id = id;
        this.state = state;
        this.inetSocketAddress = null;
    }

    public Amigo(String nombreAmigo, String id, int state, InetSocketAddress inetSocketAddress) {
        this.nombreAmigo = nombreAmigo;
        this.id = id;
        this.state = state;
        this.inetSocketAddress = inetSocketAddress;
    }

    public String getNombreAmigo() {
        return nombreAmigo;
    }

    public void setNombreAmigo(String nombreAmigo) {
        this.nombreAmigo = nombreAmigo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public Object[] getContentsInOrder() {
        return new Object[]{nombreAmigo,id};
    }
}
