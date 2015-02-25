package com.leag.scratchpad;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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

/**
 * Created by Shivam Lekhi on 10/12/2014.
 */
public class PageItemFragment extends Fragment {
    public static final String MODE = "MODE";
    public static final String MODE_NEW = "MODE_NEW";
    public static final String MODE_NORMAL = "MODE_NORMAL";
    public static final String PAGE_ID = "PAGE_ID";

    public static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String PAGE_NUMBER = "PAGE_NUMBER";
    public static final String NOTEBOOK_NAME = "NOTEBOOK_NAME";
    ImageView frontImage, backgroundImage;
    ImageButton DeletePageButton;
    TextView PageNumberText;
    RelativeLayout MainLayout;
    View rootView;

    public PageItemFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getArguments().getString(MODE).equals(MODE_NORMAL)) {

            rootView = inflater.inflate(R.layout.page_item, container, false);

            frontImage = (ImageView) rootView.findViewById(R.id.page_front_image);
            DeletePageButton = (ImageButton) rootView.findViewById(R.id.delete_page_button);

            final String FrontImageSource = Environment.getExternalStorageDirectory() + "/Scribble/" +
                    getArguments().getString(NOTEBOOK_NAME) +
                    "/page" + getArguments().getString(PAGE_NUMBER) + ".png";

            final Bitmap bmp = BitmapFactory.decodeFile(FrontImageSource);
            frontImage.setImageBitmap(bmp);

            MainLayout = (RelativeLayout) rootView.findViewById(R.id.page_item_main_Layout);

            MainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = null;
                    Intent whiteboard = new Intent(getActivity(), WhiteBoardActivity.class);
                    whiteboard.putExtra(WhiteBoardActivity.NOTEBOOK_NAME, getArguments().getString(NOTEBOOK_NAME));
                    whiteboard.putExtra(WhiteBoardActivity.MODE, WhiteBoardActivity.MODE_EDIT);
                    whiteboard.putExtra(WhiteBoardActivity.PAGE_FILENAME, FrontImageSource);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        b = ActivityOptions.makeThumbnailScaleUpAnimation(frontImage, bmp, 0, 0).toBundle();
                        getActivity().startActivity(whiteboard, b);
                    } else {
                        startActivity(whiteboard);
                    }
                }
            });

            DeletePageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FILEPATH", FrontImageSource);
                    final AlertDialog.Builder DeleteDialog = new AlertDialog.Builder(getActivity());
                    DeleteDialog.setTitle("Delete All");
                    DeleteDialog.setMessage("Are you sure you want to delete you master piece?");
                    DeleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            File currentPage = new File(Environment.getExternalStorageDirectory(), "/Scribble/" +
                                    getArguments().getString(NOTEBOOK_NAME) +
                                    "/page" + getArguments().getString(PAGE_NUMBER) + ".png");
                            currentPage.delete();

                            Toast.makeText(getActivity(), "Page Deleted", Toast.LENGTH_SHORT).show();

                            Intent pagesActivity = new Intent(getActivity(), PagesActivity.class);
                            pagesActivity.putExtra(PagesActivity.NOTEBOOK_NAME, getArguments().getString(NOTEBOOK_NAME));

                            getActivity().finish();
                            startActivity(pagesActivity);
                        }
                    });
                    DeleteDialog.setNegativeButton("No" ,null);
                    DeleteDialog.show();
                }
            });

        } else if(getArguments().getString(MODE).equals(MODE_NEW)) {
            rootView = inflater.inflate(R.layout.new_page_fragment, container, false);

            LinearLayout MainLayout = (LinearLayout) rootView.findViewById(R.id.add_new_page_layout);
            MainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent whiteboard = new Intent(getActivity(), WhiteBoardActivity.class);
                    whiteboard.putExtra(WhiteBoardActivity.NOTEBOOK_NAME, getArguments().getString(PagesActivity.NOTEBOOK_NAME));
                    whiteboard.putExtra(WhiteBoardActivity.MODE, WhiteBoardActivity.MODE_NEW);

                    getActivity().finish();
                    startActivity(whiteboard);
                }
            });
        }

        return rootView;
    }
}