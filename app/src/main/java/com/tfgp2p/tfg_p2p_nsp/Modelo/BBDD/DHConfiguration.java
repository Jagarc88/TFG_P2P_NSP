package com.tfgp2p.tfg_p2p_nsp.Modelo.BBDD;


public class DHConfiguration extends ADHprofile {

    private static DHConfiguration dhConfiguration;

    public static final String COL_ID_PROPERTY_NAME="PROPERTY_NAME";
    public static final String COL_VALUE="VALUE";

    private static String TABLE_NAME="configuracion";

    private DHConfiguration(DatabaseHelper databaseHelper) {
        super(databaseHelper);
    }

    public DHConfiguration(){}

    public static DHConfiguration createInstance(DatabaseHelper databaseHelper){
        dhConfiguration = new DHConfiguration(databaseHelper);
        return dhConfiguration;
    }

    public static DHConfiguration getInstance(){
        return dhConfiguration;
    }

    @Override
    public String onCreateQuery() {
        return "create table if not exists "+TABLE_NAME+" ("+COL_ID_PROPERTY_NAME+" VARCHAR(35) PRIMARY KEY, "+COL_VALUE+" TEXT)";
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Object getKeyElement() {
        return COL_ID_PROPERTY_NAME;
    }

    @Override
    public String[] getColumnNamesInOrder() {
        return new String[]{COL_ID_PROPERTY_NAME,COL_VALUE};
    }

    @Override
    public int[] getColumnTypesInOrder() {
        return new int[]{DatabaseHelper.FIELD_TYPE_STRING,DatabaseHelper.FIELD_TYPE_STRING};
    }

    @Override
    public Object[] getContentsInOrder(IAlmacenableBBDD almacenableBBDD) {
        return almacenableBBDD.getContentsInOrder();
    }
}
