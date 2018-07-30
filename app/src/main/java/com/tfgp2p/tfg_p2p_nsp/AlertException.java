package com.tfgp2p.tfg_p2p_nsp;

import android.content.Context;

/**
 * Created by Julio on 09/04/2018.
 */
public class AlertException extends Exception {

	private Context context;

	public AlertException(String msg, Context c){
		super(msg);
		context = c;
	}


	public void showAlert(){
		// TODO: implementar alertDialog o Toast para el usuario.

	}

}
