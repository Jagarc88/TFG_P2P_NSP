package com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD;

import com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD.Exception.DatabaseNotLoadedException;

import java.util.Map;

public abstract class ADHprofile {

    private static DatabaseHelper databaseHelper;

    public abstract String getTableName();
    public abstract Object getKeyElement();
    public abstract String onCreateQuery();
    public abstract String[] getColumnNamesInOrder();
    public abstract int[] getColumnTypesInOrder();
    public abstract Object[] getContentsInOrder(IAlmacenableBBDD almacenableBBDD);

    public ADHprofile(DatabaseHelper databaseHelper){
        ADHprofile.databaseHelper = databaseHelper;
    }

    public ADHprofile(){}

    public Map<String,Object> getDataByID(String idObject) throws DatabaseNotLoadedException{
        return getDatabaseHelper().getDataByID(idObject, this);
    }

    public Map<Object,Map<String,Object>> getAllData() throws DatabaseNotLoadedException{
        return getDatabaseHelper().getAllData(this);
    }

    public int reloadBBDD() throws DatabaseNotLoadedException{
        return getDatabaseHelper().reloadBBDD(this);
    }

    public int deleteElementByKey(Object key) throws DatabaseNotLoadedException{
        return getDatabaseHelper().deleteElementByKey(key, this);
    }

    public int updateElement(IAlmacenableBBDD iAlmacenableBBDD) throws DatabaseNotLoadedException{
        return getDatabaseHelper().updateElement(this, iAlmacenableBBDD);
    }

    public boolean addElement(IAlmacenableBBDD iAlmacenableBBDD) throws DatabaseNotLoadedException{
        return getDatabaseHelper().addElement(this, iAlmacenableBBDD);
    }

    private DatabaseHelper getDatabaseHelper() throws DatabaseNotLoadedException{
        if(databaseHelper != null){
            return databaseHelper;
        } else{
            throw new DatabaseNotLoadedException();
        }
    }
}
