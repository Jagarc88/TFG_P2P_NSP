package com.tfgp2p.tfg_p2p_nsp;

import android.app.AlertDialog;
import android.content.Context;

import com.tfgp2p.tfg_p2p_nsp.View.Utils.ViewUtils;

/**
 * Created by Julio on 09/04/2018.
 */
public class AlertException extends Exception {


	public AlertException(String msg){
		super(msg);
	}


	public void showAlert(Context context){
        AlertDialog alertDialog = ViewUtils.createSimpleDialogOK(
                "Excepción",
                getMessage(),
                new AlertException.AlertExceptionDialogListener(),
                context
        );
        alertDialog.show();
	}

	public class AlertExceptionDialogListener implements ViewUtils.IOKDialogListener {
		@Override
		public void onPossitiveButtonClick() {
			// NADA
		}
	}
}
