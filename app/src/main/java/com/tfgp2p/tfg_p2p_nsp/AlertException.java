package com.tfgp2p.tfg_p2p_nsp;

import android.content.Context;

/**
 * Created by Julio on 09/04/2018.
 */
public class AlertException extends Exception {

	private Context context;
	private String message;


	public AlertException(String msg, Context c){
		super(msg);
		message = msg;
		context = c;
	}


	public void showAlert(){
		MyAlert a = new MyAlert(message, context);
		Void[] params = new Void[0];
		a.execute(params);
	}

}
