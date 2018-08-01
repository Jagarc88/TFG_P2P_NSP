package com.tfgp2p.tfg_p2p_nsp;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Julio on 09/04/2018.
 */
public class AlertException extends Exception {

	private Context context;
	private String message;
	private final int duration = Toast.LENGTH_SHORT;


	public AlertException(String msg, Context c){
		super(msg);
		message = msg;
		context = c;
	}


	public void showAlert(){
		/*Toast toast = Toast.makeText(context, message, duration);
		toast.show();
		*/
	}

}
