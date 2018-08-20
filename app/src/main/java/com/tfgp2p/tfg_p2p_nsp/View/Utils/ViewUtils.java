package com.tfgp2p.tfg_p2p_nsp.View.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.tfgp2p.tfg_p2p_nsp.Controlador.AplicacionMain;

public class ViewUtils {


    /**
     *   <ScrollView
     *        android:layout_width="match_parent"
     *        android:layout_height="match_parent">

     <LinearLayout
     android:id="@+id/elemento_listafileslots"
     android:layout_width="match_parent"
     android:layout_height="wrap_content"
     android:orientation="vertical" />
     </ScrollView>
     * @param linearLayoutContainer
     * @return
     */
    public static LinearLayout createConteiner(LinearLayout linearLayoutContainer, int idContainer){

        Context context = linearLayoutContainer.getContext();

        //Scroller que contendra los ficheros
        ScrollView scrollView = new ScrollView(context);
        ScrollView.LayoutParams layoutParamsScrollView =
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(layoutParamsScrollView);

        //
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setId(idContainer);
        LinearLayout.LayoutParams layoutParamsLinearLayout =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(layoutParamsLinearLayout);

        scrollView.addView(linearLayout);
        linearLayoutContainer.addView(scrollView);
        return linearLayout;
    }

    /**
     * Crea un diálogo de alerta sencillo OK - Cancelar
     * @return Nuevo diálogo
     */
    public static AlertDialog createSimpleDialogOKCancelar(String titulo,String mensaje,final IOKCancelarDialogListener listener,Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onPossitiveButtonClick();
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onNegativeButtonClick();
                            }
                        });

        return builder.create();
    }


    public interface IOKCancelarDialogListener{
        void onPossitiveButtonClick();
        void onNegativeButtonClick();
    }

    /**
     * Crea un diálogo de alerta sencillo OK
     * @return Nuevo diálogo
     */
    public static AlertDialog createSimpleDialogOK(String titulo,String mensaje,final IOKDialogListener listener,Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onPossitiveButtonClick();
                            }
                        });

        return builder.create();
    }

    public interface IOKDialogListener{
        void onPossitiveButtonClick();
    }
}
