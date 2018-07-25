package com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD;


import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.Exception.DatabaseNotLoadedException;
import com.tfgp2p.tfg_p2p_nsp.Modelo.DAO;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.Amigo;
import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaAmigos.GestorSistemaAmigos;

import java.util.HashMap;
import java.util.Map;

public class DHAmigos extends ADHprofile {

    private static DHAmigos dhAmigos = null;

    public static final String COL_ID_AMIGO="ID_AMIGO";
    public static final String COL_NOMBRE_AMIGO="NOMBRE_AMIGO";

    private static String TABLE_NAME="AMIGOS";

    private DHAmigos(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }

    public DHAmigos(){}

    public static DHAmigos createInstance(DatabaseHelper databaseHelper){
        dhAmigos = new DHAmigos(databaseHelper);
        return dhAmigos;
    }

    public static DHAmigos getInstance() {
        return dhAmigos;
    }

    @Override
    public String onCreateQuery() {
        return "create table if not exists "+TABLE_NAME+" ("+COL_ID_AMIGO+" VARCHAR(25) PRIMARY KEY, "+COL_NOMBRE_AMIGO+" TEXT)";
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Object getKeyElement() {
        return COL_ID_AMIGO;
    }

    @Override
    public String[] getColumnNamesInOrder() {
        return new String[]{DHAmigos.COL_NOMBRE_AMIGO,DHAmigos.COL_ID_AMIGO};
    }

    @Override
    public int[] getColumnTypesInOrder() {
        return new int[]{DatabaseHelper.FIELD_TYPE_STRING,DatabaseHelper.FIELD_TYPE_STRING};
    }

    @Override
    public Object[] getContentsInOrder(IAlmacenableBBDD almacenableBBDD) {
        return almacenableBBDD.getContentsInOrder();
    }

    public HashMap<String, Amigo> getAllAmigos(){
        HashMap<String, Amigo> amigoMap = new HashMap<>();
        try {
            for (Map<String,Object> mapObj : DAO.databaseAmigos().getAllData().values()){
                Amigo amigo = new Amigo(
                        (String)mapObj.get(DHAmigos.COL_NOMBRE_AMIGO),
                        (String)mapObj.get(DHAmigos.COL_ID_AMIGO),
                        GestorSistemaAmigos.STATE_USER_OFFLINE);
                amigoMap.put(amigo.getNombreAmigo(),amigo);
            }
        }catch (DatabaseNotLoadedException e){
            e.printStackTrace();
        }
        return amigoMap;
    }
}
