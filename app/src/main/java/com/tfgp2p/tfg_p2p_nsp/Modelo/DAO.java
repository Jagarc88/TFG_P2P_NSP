package com.tfgp2p.tfg_p2p_nsp.Modelo;

import android.content.Context;

import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DatabaseHelper;
import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.DHConfiguration;

public class DAO {

    private static DatabaseHelper databaseConfiguration;

    private DAO(){}

    public static DatabaseHelper databaseConfiguration(Context context){
        if(DAO.databaseConfiguration==null) {
            DAO.databaseConfiguration = new DHConfiguration(context);
        }
        return databaseConfiguration;
    }
}
