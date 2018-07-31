package com.tfgp2p.tfg_p2p_nsp.Modelo;

import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.IAlmacenableBBDD;

public class Property  implements IAlmacenableBBDD {

    private String property;
    private String key;

    public Property(String key, String property) {
        this.key = key;
        this.property = property;
    }

    @Override
    public Object[] getContentsInOrder() {
        return new Object[]{key, property};
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
