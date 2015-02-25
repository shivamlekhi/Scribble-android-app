package com.leag.scratchpad;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shivam Lekhi on 11/10/2014.
 */
public class TourItem extends Fragment {
    public static final String IMAGE = "IMAGE";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tour_item, container, false);

        Typeface BEBAS = Typeface.createFromAsset(getActivity().getAssets(), "fonts/BebasNeue.otf");

        ImageView MainImage = (ImageView) rootView.findViewById(R.id.tour_item_image);
        TextView Title = (TextView) rootView.findViewById(R.id.tour_item_title);
        TextView Description = (TextView) rootView.findViewById(R.id.tour_item_dessription);

        Title.setTypeface(BEBAS);

        MainImage.setImageResource(getArguments().getInt(IMAGE));
        Title.setText(getArguments().getString(TITLE));
        Description.setText(getArguments().getString(DESCRIPTION));

        return rootView;
    }
}
