package com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "pp2frs.db";

    private List<ADHprofile> databasesProfiles = Arrays.asList(new ADHprofile[]{
            new DHAmigos(),
            new DHConfiguration()
    });

    private Map<Object,Map<String,Object>> loadedBBDD = null;

    public static final int FIELD_TYPE_STRING=Cursor.FIELD_TYPE_STRING;
    public static final int FIELD_TYPE_INTEGER=Cursor.FIELD_TYPE_INTEGER;
    public static final int FIELD_TYPE_FLOAT=Cursor.FIELD_TYPE_FLOAT;
    public static final int FIELD_TYPE_BLOB=Cursor.FIELD_TYPE_BLOB;

    public static DatabaseHelper databaseHelper;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        databaseHelper = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (ADHprofile dhProfile: databasesProfiles) {
            db.execSQL(dhProfile.onCreateQuery());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    private boolean insertData(String[] columnNames, Object[] toInsert, int[] typeColumn, String tableName){
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
        success = db.insert(tableName, null, contentValues)!=-1;
        db.setTransactionSuccessful();
        db.endTransaction();
        loadData(tableName);
        return success;
    }

    /**
     * El primer objeto es el ID
     * @return
     */
    private int updateData(String[] columnNames,Object[] toInsert,int[] typeColumn, String tableName){
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
        numbChanged = db.update(tableName, contentValues, columnNames[0]+"=?",new String[]{toInsert[0].toString()});
        db.setTransactionSuccessful();
        db.endTransaction();
        loadData(tableName);
        return numbChanged;
    }

    private Map<Object,Map<String,Object>> loadData(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("select * from "+tableName,null);
        Map<Object,Map<String,Object>> result = new HashMap<>();
        Map<String,Object> tempObtained;
        try {
            Object valueTemp=null;
            Object idTemp=null;
            while(cursor.moveToNext()) {
                tempObtained = new HashMap<>();
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
                System.out.println("AAA"+ tempObtained);
            }
        } finally {
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return result;
    }

    public Map<String,Object> getDataByID(String idObject,ADHprofile ADHprofile){
        loadedBBDD=loadData(ADHprofile.getTableName());
        return loadedBBDD.get(idObject);
    }

    public Map<Object,Map<String,Object>> getAllData(ADHprofile ADHprofile){
        loadedBBDD=loadData(ADHprofile.getTableName());
        return loadedBBDD;
    }

    public int reloadBBDD(ADHprofile ADHprofile){
        loadedBBDD=loadData(ADHprofile.getTableName());
        return loadedBBDD.size();
    }

    public int deleteElementByKey(Object key,ADHprofile ADHprofile){
        String columnKey = ADHprofile.getKeyElement().toString();
        String tableName = ADHprofile.getTableName();
        String keyString = key.toString();
        int numChanged = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        numChanged = db.delete(tableName, columnKey+"=?",new String[]{keyString});

        db.setTransactionSuccessful();
        db.endTransaction();
        loadData(tableName);

        return numChanged;
    }

    public int updateElement(ADHprofile ADHprofile, IAlmacenableBBDD iAlmacenableBBDD){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        int result = updateData(ADHprofile.getColumnNamesInOrder(),
                ADHprofile.getContentsInOrder(iAlmacenableBBDD),
                ADHprofile.getColumnTypesInOrder(),
                ADHprofile.getTableName());

        db.setTransactionSuccessful();
        db.endTransaction();

        return result;
    }

    public boolean addElement(ADHprofile ADHprofile, IAlmacenableBBDD iAlmacenableBBDD){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        boolean result = insertData(ADHprofile.getColumnNamesInOrder(),
                ADHprofile.getContentsInOrder(iAlmacenableBBDD),
                ADHprofile.getColumnTypesInOrder(),
                ADHprofile.getTableName());

        db.setTransactionSuccessful();
        db.endTransaction();

        return result;
    }
}