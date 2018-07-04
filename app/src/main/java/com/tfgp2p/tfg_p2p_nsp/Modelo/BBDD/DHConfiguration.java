package com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DHConfiguration extends DatabaseHelper {

    public static final String COL_ID_PROPERTY_NAME="PROPERTY_NAME";
    public static final String COL_VALUE="VALUE";

    private static String TABLE_NAME="configuracion";

    public DHConfiguration(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" ("+COL_ID_PROPERTY_NAME+" VARCHAR(25) PRIMARY KEY, "+COL_VALUE+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    /*    db.execSQL("drop table if exists "+TABLE_NAME);
        onCreate(db);
        */
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }
}
