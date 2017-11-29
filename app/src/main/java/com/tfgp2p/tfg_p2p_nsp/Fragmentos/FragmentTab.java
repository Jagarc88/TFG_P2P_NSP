package com.tfgp2p.tfg_p2p_nsp.Fragmentos;

import android.app.Fragment;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Deekin on 29/11/2017.
 */

public abstract class FragmentTab extends Fragment {

    public abstract void rellenaVariables(TextView textTitutloHeadPestana, ImageView imageHeadPestana, ImageView imageExpandir, FrameLayout layoutRellenoPestana);
}
