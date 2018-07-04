package com.tfgp2p.tfg_p2p_nsp.View.Utils;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
}
