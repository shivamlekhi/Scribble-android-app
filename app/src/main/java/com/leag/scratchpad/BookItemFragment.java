package com.leag.scratchpad;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Shivam Lekhi on 10/10/2014.

 **/
public class BookItemFragment extends Fragment {
    TextView Name, NoPagesYetText;
    ImageView MainImage;
    RelativeLayout MainLayout;

    public static final String POSITION = "POSITION";
    public static final String POSITION_FIXED = "POSITION_FIXED";
    public static final String POSITION_FLUID = "POSITION_FLUID";

    public static final String PASSED_NAME = "PASSED_NAME";
    public static final String PASSED_IMAGE = "PASSED_IMAGE";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notebookfragment, container, false);

        MainLayout = (RelativeLayout) rootView.findViewById(R.id.book_layout);

        Name = (TextView) rootView.findViewById(R.id.book_name);
        MainImage = (ImageView) rootView.findViewById(R.id.book_image);
        NoPagesYetText = (TextView) rootView.findViewById(R.id.no_pages_yet);

        if(getArguments().getString(POSITION).equals(POSITION_FLUID)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 200);
            params.topMargin = (int) getResources().getDimension(R.dimen.book_margin);

            MainLayout.setLayoutParams(params);
        }

        Typeface BEBAS = Typeface.createFromAsset(getActivity().getAssets(), "fonts/BebasNeue.otf");

        // MainImage.setImageResource(getArguments().getInt(PASSED_IMAGE));
        String NamePassed = getArguments().getString(PASSED_NAME);
        Name.setTypeface(BEBAS);
        Name.setText(NamePassed);

        final String FrontImageSources = Environment.getExternalStorageDirectory() + "/Scribble/" +
                getArguments().getString(PASSED_NAME) + "/";

        String[] files = new File(FrontImageSources).list();

        if(files.length > 0) {
            Bitmap bmp = BitmapFactory.decodeFile(FrontImageSources + files[0]);
            MainImage.setImageBitmap(bmp);
        } else {
            NoPagesYetText.setVisibility(View.VISIBLE);
        }

        MainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder DeleteDialog = new AlertDialog.Builder(getActivity());
                DeleteDialog.setTitle("Delete Notebook");
                DeleteDialog.setMessage("Are you sure you want to delete this notebook?");
                DeleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File NotebookDir = new File(Environment.getExternalStorageDirectory() + "/Scribble/" + getArguments().getString(PASSED_NAME));
                        if(NotebookDir.exists() && NotebookDir.isDirectory()) {
                            if(NotebookDir.list().length == 0) NotebookDir.delete();
                            else {
                                String[] files = NotebookDir.list();
                                for(String filePath : files) {
                                    File file = new File(FrontImageSources + filePath);
                                    file.delete();
                                }

                                NotebookDir.delete();
                            }
                        }

                        Intent NotebooksActivity = new Intent(getActivity(), AllNoteBooksActivity.class);
                        getActivity().finish();
                        startActivity(NotebooksActivity);
                    }
                });
                DeleteDialog.setNegativeButton("No" ,null);
                DeleteDialog.show();
                return true;
            }
        });

        MainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pages = new Intent(getActivity(), PagesActivity.class);
                pages.putExtra(PagesActivity.NOTEBOOK_NAME, getArguments().getString(PASSED_NAME));
                getActivity().finish();
                startActivity(pages);
            }
        });

        return rootView;
    }
}
