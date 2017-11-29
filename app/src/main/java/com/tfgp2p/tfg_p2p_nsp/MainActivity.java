package com.tfgp2p.tfg_p2p_nsp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.InicioFragment;

public class MainActivity extends AppCompatActivity {

    FrameLayout frameTabContent;

    FragmentManager fragMan = getFragmentManager();
    FragmentTransaction fragTransaction = fragMan.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameTabContent = findViewById(R.id.main_content_layout);

        // TODO: TEMPORAL ----------
        // CAMBIAR el metodo de generacion de elementos, por ahora solo se vera Inicio
        fragTransaction.add(frameTabContent.getId(), new InicioFragment(), "fragment");
        findViewById(R.id.tabbar_tabinicio_layout).setBackgroundResource(R.drawable.tabbar_background_selected);
        fragTransaction.commit();
    }
}
