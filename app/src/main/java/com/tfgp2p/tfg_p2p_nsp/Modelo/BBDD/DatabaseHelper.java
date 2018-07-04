package com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

public abstract class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "p2friends.db";

    private Map<Object,Map<String,Object>> loadedBBDD = null;

    public static final int FIELD_TYPE_STRING=Cursor.FIELD_TYPE_STRING;
    public static final int FIELD_TYPE_INTEGER=Cursor.FIELD_TYPE_INTEGER;
    public static final int FIELD_TYPE_FLOAT=Cursor.FIELD_TYPE_FLOAT;
    public static final int FIELD_TYPE_BLOB=Cursor.FIELD_TYPE_BLOB;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public boolean insertData(String[] columnNames,Object[] toInsert,int[] typeColumn){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        boolean success;
        for (int i = 0; i < columnNames.length; i++) {
            switch (typeColumn[i]) {
                case FIELD_TYPE_STRING: {
                    contentValues.put(columnNames[i], (String)toInsert[i]);
                }
                break;
                case FIELD_TYPE_INTEGER: {
                    contentValues.put(columnNames[i], (int)toInsert[i]);
                }
                break;
                case FIELD_TYPE_FLOAT: {
                    contentValues.put(columnNames[i], (float)toInsert[i]);
                }
                break;
                case FIELD_TYPE_BLOB: {
                    contentValues.put(columnNames[i], (byte[]) toInsert[i]);
                }
                break;
            }
        }
        String tableName = getTableName();
        success = db.insert(tableName, null, contentValues)!=-1;
        db.setTransactionSuccessful();
        db.endTransaction();
        loadData();
        return success;
    }

    /**
     * El primer objeto es el ID
     * @return
     */
    public int updateData(String[] columnNames,Object[] toInsert,int[] typeColumn){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        int numbChanged=0;
        for (int i = 0; i < columnNames.length; i++) {
            switch (typeColumn[i]) {
                case FIELD_TYPE_STRING: {
                    contentValues.put(columnNames[i], (String)toInsert[i]);
                }
                break;
                case FIELD_TYPE_INTEGER: {
                    contentValues.put(columnNames[i], (int)toInsert[i]);
                }
                break;
                case FIELD_TYPE_FLOAT: {
                    contentValues.put(columnNames[i], (float)toInsert[i]);
                }
                break;
                case FIELD_TYPE_BLOB: {
                    contentValues.put(columnNames[i], (byte[]) toInsert[i]);
                }
                break;
            }
        }
        String tableName = getTableName();
        numbChanged = db.update(tableName, contentValues, columnNames[0]+"=?",new String[]{toInsert[0].toString()});
        db.endTransaction();
        loadData();
        return numbChanged;
    }

    protected abstract String getTableName();

    private Map<Object,Map<String,Object>> loadData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select * from "+getTableName(),null);
        Map<Object,Map<String,Object>> result = new HashMap<>();
        Map<String,Object> tempObtained = new HashMap<>();
        try {
            Object valueTemp=null;
            Object idTemp=null;
            while(cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    switch (cursor.getType(i)) {
                        case Cursor.FIELD_TYPE_STRING: {
                            valueTemp=cursor.getString(i);
                            tempObtained.put(cursor.getColumnName(i),valueTemp);
                        }
                        break;
                        case Cursor.FIELD_TYPE_INTEGER: {
                            valueTemp=cursor.getInt(i);
                            tempObtained.put(cursor.getColumnName(i),valueTemp);
                        }
                        break;
                        case Cursor.FIELD_TYPE_FLOAT: {
                            valueTemp=cursor.getFloat(i);
                            tempObtained.put(cursor.getColumnName(i),valueTemp);
                        }
                        break;
                        case Cursor.FIELD_TYPE_BLOB: {
                            valueTemp=cursor.getBlob(i);
                            tempObtained.put(cursor.getColumnName(i),valueTemp);
                        }
                        break;
                    }
                    if(i==0){
                        idTemp=valueTemp;
                    }
                }
                result.put(idTemp, tempObtained);
            }
        } finally {
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return result;
    }

    public Map<String,Object> getDataByID(String idObject){
        if(loadedBBDD==null){
            loadedBBDD=loadData();
        }
        return loadedBBDD.get(idObject);
    }

    public Map<Object,Map<String,Object>> getAllData(){
        if(loadedBBDD==null){
            loadedBBDD=loadData();
        }
        return loadedBBDD;
    }

    public int reloadBBDD(){
        loadedBBDD=loadData();
        return loadedBBDD.size();
    }
}