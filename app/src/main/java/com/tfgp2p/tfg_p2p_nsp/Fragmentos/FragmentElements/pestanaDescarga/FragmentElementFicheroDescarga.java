package com.tfgp2p.tfg_p2p_nsp.Fragmentos.FragmentElements.pestanaDescarga;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tfgp2p.tfg_p2p_nsp.Modelo.sistemaFicheros.Fichero;
import com.tfgp2p.tfg_p2p_nsp.R;

public class FragmentElementFicheroDescarga extends Fragment {

    private static Fichero ficheroCargado;

    private TextView text_indicador_numerador;
    private TextView text_indicador_denominador;
    private TextView text_descarga_nombrefichero;
    private ProgressBar progressBar_descarga;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInicio = inflater.inflate(R.layout.download_file_layout, container, false);

        text_indicador_numerador = viewInicio.findViewById(R.id.text_indicador_numerador);
        text_indicador_denominador = viewInicio.findViewById(R.id.text_indicador_denominador);
        text_descarga_nombrefichero = viewInicio.findViewById(R.id.test_descarga_nombrefichero);
        progressBar_descarga = viewInicio.findViewById(R.id.progressBar_descarga);

        loadData();

        return viewInicio;
    }

    public void loadData(){
        text_indicador_numerador.setText(Long.toString(ficheroCargado.getCantidadDescargada()));
        text_indicador_denominador.setText(Long.toString(ficheroCargado.getTamanyoTotal()));
        progressBar_descarga.setMax(100);
        progressBar_descarga.setProgress(Math.round(ficheroCargado.getProcentageDescargado()));
        text_descarga_nombrefichero.setText(ficheroCargado.getNombre());
    }

    public TextView getText_indicador_numerador() {
        return text_indicador_numerador;
    }

    public TextView getText_indicador_denominador() {
        return text_indicador_denominador;
    }

    public TextView getText_descarga_nombrefichero() {
        return text_descarga_nombrefichero;
    }

    public ProgressBar getProgressBar_descarga() {
        return progressBar_descarga;
    }

    public Fichero getFicheroCargado() {
        return ficheroCargado;
    }

    public void setFicheroCargado(Fichero ficheroCargado) {
        this.ficheroCargado = ficheroCargado;
    }
}
