package com.leag.scratchpad;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Sam on 10/7/2014.
 */
public class PagesActivity extends FragmentActivity {
    public static final String NOTEBOOK_NAME = "NOTEBOOK_NAME";
    public static String NoteBookname = "";
    TextView BookName, CurrentPageNumber, TotalPageNumber;
    int totalPages = 0;
    ArrayList<Fragment> frags;
    ViewPager pager;
    PagesAdapter PagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pages_activity);

        getActionBar().hide();

        NoteBookname = getIntent().getExtras().getString(NOTEBOOK_NAME);
        File RootFolder = new File(Environment.getExternalStorageDirectory(), "/Scribble/" + getIntent().getExtras().getString(NOTEBOOK_NAME) + "/");
        frags = new ArrayList<Fragment>();
        if(RootFolder.exists() && RootFolder.isDirectory()) {
            String Pages[] = RootFolder.list();
            for(int i = 0; i < Pages.length ; i++) {
                if(Pages[i].startsWith("page")) {
                    totalPages++;
                    Fragment Page = new PageItemFragment();
                    Bundle args = new Bundle();
                    args.putString(PageItemFragment.IMAGE_PATH, Pages[i]);
                    args.putString(PageItemFragment.PAGE_NUMBER, Pages[i].replace("page", "").replace(".png", ""));
                    args.putString(PageItemFragment.NOTEBOOK_NAME, getIntent().getExtras().getString(this.NOTEBOOK_NAME));
                    args.putString(PageItemFragment.MODE, PageItemFragment.MODE_NORMAL);
                    args.putInt(PageItemFragment.PAGE_ID, i);
                    Page.setArguments(args);

                    frags.add(Page);
                }
            }

            Fragment frag = new PageItemFragment();
            Bundle args = new Bundle();
            args.putString(PageItemFragment.MODE, PageItemFragment.MODE_NEW);
            args.putString(PageItemFragment.NOTEBOOK_NAME, getIntent().getExtras().getString(NOTEBOOK_NAME));
            frag.setArguments(args);

            frags.add(frag);
        }

        BookName = (TextView) findViewById(R.id.Pages_CurrentBookName);
        Typeface BEBAS = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");
        BookName.setText(getIntent().getExtras().getString(NOTEBOOK_NAME));
        BookName.setTypeface(BEBAS);

        getActionBar().setTitle(getIntent().getExtras().getString(NOTEBOOK_NAME));

        CurrentPageNumber = (TextView) findViewById(R.id.CurrentPageNumber);
        TotalPageNumber = (TextView) findViewById(R.id.total_page_number);

        CurrentPageNumber.setTypeface(BEBAS);
        TotalPageNumber.setTypeface(BEBAS);

        TotalPageNumber.setText("/" + Integer.toString(totalPages+1));

        pager = (ViewPager) findViewById(R.id.pages_viewpager);
        pager.setClipToPadding(false);
        pager.setPageMargin(24);
        /*pager.PageMargin = 12.ToPixels ();*/

        PagerAdapter = new PagesAdapter(getSupportFragmentManager(), frags);

        pager.setAdapter(PagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int pos = position;
                CurrentPageNumber.setText(Integer.toString(pager.getCurrentItem()+1));
            }

            @Override
            public void onPageSelected(int position) {
                // CurrentPageNumber.setText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent Pages = new Intent(this, AllNoteBooksActivity.class);
        this.finish();
        startActivity(Pages);
    }

    class PagesAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> frags;
        public PagesAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.frags = fragments;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return frags.get(position);
        }

        @Override
        public float getPageWidth(int position) {
            return 0.93f;
        }

        @Override
        public int getCount() {
            return frags.size();
        }
    }
}
