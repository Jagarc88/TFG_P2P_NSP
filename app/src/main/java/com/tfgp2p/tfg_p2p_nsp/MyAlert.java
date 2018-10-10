package com.tfgp2p.tfg_p2p_nsp;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Julio on 09/04/2018.
 */
public class MyAlert extends AsyncTask<Void, Void, Void> {

	private Context context;
	private String message;


	public MyAlert(String msg, Context c){
		message = msg;
		context = c;
	}


	@Override
	protected Void doInBackground(Void... voids) {
		return null;
	}


	@Override
	protected void onPostExecute(Void voids) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
	}
}
