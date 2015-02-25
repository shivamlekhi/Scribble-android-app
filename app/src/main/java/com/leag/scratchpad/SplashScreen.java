package com.leag.scratchpad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * Created by Shivam Lekhi on 10/10/2014.
 */
public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        getActionBar().hide();

        TextView LogoText = (TextView) findViewById(R.id.splash_screen_text);
        Typeface BEBAS = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");
        LogoText.setTypeface(BEBAS);

        final SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preferences_file_name), Context.MODE_PRIVATE);

        final boolean FirstVisit = sharedPref.getBoolean(
                getResources().getString(R.string.preferences_first_visit),
                getResources().getBoolean(R.bool.preferences_first_visit_default_value)
        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirstVisit) {
                    Intent Tour = new Intent(SplashScreen.this, TourActivity.class);
                    startActivity(Tour);
                } else {
                    Intent Books = new Intent(SplashScreen.this, AllNoteBooksActivity.class);
                    startActivity(Books);


/*
                    Intent whiteboard = new Intent(SplashScreen.this, WhiteBoardActivity.class);
                    whiteboard.putExtra(WhiteBoardActivity.NOTEBOOK_NAME, "Notes");
                    whiteboard.putExtra(WhiteBoardActivity.MODE, WhiteBoardActivity.MODE_NEW);
*/
                }

                final SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getResources().getString(R.string.preferences_first_visit), false);
                editor.commit();

                // close this activity
                finish();
            }
        }, 1000);
    }
}
