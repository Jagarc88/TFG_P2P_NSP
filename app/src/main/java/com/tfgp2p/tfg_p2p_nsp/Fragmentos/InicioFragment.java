package com.tfgp2p.tfg_p2p_nsp.Fragmentos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tfgp2p.tfg_p2p_nsp.Fragmentos.PestanaFragments.PestanaFragment;
import com.tfgp2p.tfg_p2p_nsp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class InicioFragment extends Fragment {

    private PestanaFragment pestanaFragmentTop;
    private PestanaFragment pestanaFragmentBottom;

    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewInicio = inflater.inflate(R.layout.fragment_inicio, container, false);

        LinearLayout topLayout = viewInicio.findViewById(R.id.layout_pestana_top);
        LinearLayout bottomLayout = viewInicio.findViewById(R.id.layout_pestana_bottom);

        pestanaFragmentTop = new PestanaFragment();

        pestanaFragmentTop.getTextTitutloHeadPestana().setText("MOVIDOTE");

        topLayout.addView(pestanaFragmentTop.getView());

        pestanaFragmentBottom = new PestanaFragment();

        pestanaFragmentBottom.getTextTitutloHeadPestana().setText("MOVIDOTE2");

        bottomLayout.addView(pestanaFragmentBottom.getView());

        return viewInicio;
    }


}
