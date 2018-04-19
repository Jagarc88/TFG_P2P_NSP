package com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import com.tfgp2p.tfg_p2p_nsp.R;

public enum TipoFichero {

    // Solo estos dos por ahora, en el futuro habra mas
    MP3, AVI, FOLD;

    /**
     * Devuelve el tipo fichero apartir del nombre del fichero
     * @param nombreFichero
     * @return
     */
    public static TipoFichero obtainTipoFichero(String nombreFichero){

        // Separa el String para tener la terminacion
        String[] nombreDesglosado = nombreFichero.split("\\.");

        for(int i=0;i<nombreDesglosado.length;i++){
            System.out.println(nombreDesglosado[i]);
        }

        TipoFichero tipoFichero = FOLD;

        if(nombreDesglosado.length > 1) {
            // Cogemos el ultimo de todos lo datos
            switch (nombreDesglosado[nombreDesglosado.length-1]) {
                case "mp3": tipoFichero = MP3; break;
                case "avi": tipoFichero = AVI; break;
                default: tipoFichero = FOLD; break;
            }
        }
        return tipoFichero;
    }

    public Drawable getDrawable(Context context){

        Drawable drawable = null;

        switch(this){
            case MP3:{
                drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_music_note_black_24dp, null);
            }break;
            case AVI:{
                drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_movie_black_24dp, null);
            }break;
        }

        return drawable;
    }

    public static Drawable obtainDrawable(TipoFichero tipoFichero, Context context){

        Drawable drawable = null;

        switch(tipoFichero){
            case MP3:{
                drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_music_note_black_24dp, null);
            }break;
            case AVI:{
                drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_movie_black_24dp, null);
            }break;
        }

        return drawable;
    }
}
