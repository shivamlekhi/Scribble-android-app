package com.leag.scratchpad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Shivam Lekhi on 10/10/2014.
 */
public class TourActivity extends FragmentActivity implements View.OnClickListener{
    Button NextButton, SkipButton;
    ViewPager TourPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tour_activity);

        int[] images = {R.drawable.notebooks, R.drawable.tools, R.drawable.draw_image, R.drawable.share};
        final String Titles[] = {"Unlimited Notebooks", "All The tools you need", "Draw On Photos", "Share your masterpiece"};
        String Descriptions[] = {
          "Create Unlimited number of notebooks, with unlimited number of pages and unleash the inner artist in you",
          "Scrible provides you with all the tools that you need to craft your masterpiece. Draw on photos or create you own",
          "Draw on Images From the Gallery or the Camera with the super easy to use interface",
          "Share What you make to your friends, weather its your own masterpiece or an image that you edited"
        };

        getActionBar().hide();

        NextButton = (Button) findViewById(R.id.tour_next_button);
        SkipButton = (Button) findViewById(R.id.tour_skip_button);

        TourPager = (ViewPager) findViewById(R.id.tour_pager);

        ArrayList<Fragment> frags = new ArrayList<Fragment>();
        for(int i = 0 ; i < images.length ; i++) {
            TourItem frag = new TourItem();
            Bundle args = new Bundle();
            args.putInt(TourItem.IMAGE, images[i]);
            args.putString(TourItem.TITLE, Titles[i]);
            args.putString(TourItem.DESCRIPTION, Descriptions[i]);
            frag.setArguments(args);

            frags.add(frag);
        }

        TourAdapter adapter = new TourAdapter(getSupportFragmentManager(), frags);
        TourPager.setAdapter(adapter);

        NextButton.setOnClickListener(this);
        SkipButton.setOnClickListener(this);

        TourPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == (Titles.length-1)) {
                    NextButton.setBackgroundColor(getResources().getColor(R.color.default_brush_color));
                    NextButton.setText("DONE");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tour_next_button:
                if(TourPager.getCurrentItem() < 3) {
                    TourPager.setCurrentItem(TourPager.getCurrentItem() + 1);
                } else if(NextButton.getText() == "DONE") {
                    Intent NotesActivity = new Intent(this, AllNoteBooksActivity.class);
                    this.finish();
                    startActivity(NotesActivity);
                }
                break;
            case R.id.tour_skip_button:
                Intent NotesActivity = new Intent(this, AllNoteBooksActivity.class);
                this.finish();
                startActivity(NotesActivity);
                break;
        }
    }

    class TourAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> frags;

        public TourAdapter(FragmentManager fm, ArrayList<Fragment> fragsment) {
            super(fm);
            this.frags = fragsment;
        }

        @Override
        public Fragment getItem(int position) {
            return this.frags.get(position);
        }

        @Override
        public int getCount() {
            return this.frags.size();
        }
    }
}
