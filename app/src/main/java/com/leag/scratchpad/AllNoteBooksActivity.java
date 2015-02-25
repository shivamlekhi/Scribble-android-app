package com.leag.scratchpad;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Sam on 10/5/2014.
 */
public class AllNoteBooksActivity extends Activity implements View.OnClickListener {
    Typeface Bosun;
    RelativeLayout Book1, Book2,Book3,Book4,Book5,Book6,Book7 ,HolderLayout;
    LinearLayout NoteBookHolder, NewNotebookpopup;
    View NewNotebookOverlay;
    EditText NewNoteBookInput;
    ImageButton NewNoteBookDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notebooks_activity);
        Bosun = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");

        HolderLayout = (RelativeLayout) findViewById(R.id.notebook_holder_layout);

        getActionBar().setDisplayShowTitleEnabled(false);

        initBooks();
        initPopup();
    }

    private void initPopup() {
        NewNotebookpopup = (LinearLayout) findViewById(R.id.new_notebook_popup);
        NewNotebookOverlay = findViewById(R.id.new_notebook_overlay);
        NewNoteBookDoneButton = (ImageButton) findViewById(R.id.BookNameDoneButton);

        NewNoteBookDoneButton.setOnClickListener(this);

        NewNoteBookInput = (EditText) findViewById(R.id.new_notebook_input);
        NewNoteBookInput.setTypeface(Bosun);
    }

    private void initBooks() {
        File RootDirectory = CheckForFolder();

        String[] files = RootDirectory.list();

        for(String file : files) {
            File notebookFolder = new File(file);

            if(notebookFolder.isDirectory()) {
                Log.d("CUSTOM", "project folder found: " + notebookFolder.toString());
            }
        }

        NoteBookHolder = (LinearLayout) findViewById(R.id.notebooks_holder);

        int ids[] = {R.id.book1,R.id.book2,R.id.book3,R.id.book4,R.id.book5,R.id.book6,R.id.book7};
        android.app.FragmentTransaction trans = getFragmentManager().beginTransaction();

        for(int i = 0 ; i < files.length ; i++) {
            Fragment frag = new BookItemFragment();
            Bundle args = new Bundle();
            args.putString(BookItemFragment.PASSED_NAME, files[i]);

            if(i >= 7) {
                args.putString(BookItemFragment.POSITION, BookItemFragment.POSITION_FLUID);
                frag.setArguments(args);
                trans.add(R.id.more_notebooks_layout, frag);
            } else {
                args.putString(BookItemFragment.POSITION, BookItemFragment.POSITION_FIXED);

                frag.setArguments(args);
                trans.add(ids[i], frag);
            }

        }

        trans.commit();
    }

    private File CheckForFolder() {
        File MainDirectory = new File(Environment.getExternalStorageDirectory(), "/Scribble");
        if(MainDirectory.exists()) {
            Log.d("Custom com.leag.scribble", "DIrectory Already Exists");
            return MainDirectory;
        } else {
            MainDirectory.mkdir();
            Log.d("Custom com.leag.scribble", "DIrectory Created");
            return MainDirectory;
        }
    }

    private boolean CreateNotebook(String NewName) {

        File NewFolder = new File(Environment.getExternalStorageDirectory(), "/Scribble/"+NewName);

        if(NewFolder.exists()) {
            Log.d("FOLDER: ", "notebook folder could not be created");
            return false;
        } else {
            NewFolder.mkdir();
            Log.d("FOLDER: ", "notebook folder created");
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notebook_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add_notebook_action:
                NewNotebookpopup.setVisibility(View.VISIBLE);

                NewNotebookOverlay.setAlpha(0.4f);
                NewNotebookOverlay.setVisibility(View.VISIBLE);
                NewNoteBookInput.findFocus();
                break;
            case R.id.view_zoomer:
                Intent image = new Intent(this, ImageViewer.class);
                startActivity(image);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        if(NewNotebookOverlay.getVisibility() == View.VISIBLE) {
            NewNotebookOverlay.setVisibility(View.GONE);
            NewNotebookpopup.setVisibility(View.GONE);
        } else {
            // do nothing
            this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BookNameDoneButton:
                if(NewNoteBookInput.getText().toString().equals("")) {
                    NewNoteBookInput.setError("Please Choose A Name");
                } else {
                    if(CreateNotebook(NewNoteBookInput.getText().toString())) {
                        // CreateNotebook(NewNoteBookInput.getText().toString());
                        Intent whiteboard = new Intent(this, WhiteBoardActivity.class);
                        whiteboard.putExtra(WhiteBoardActivity.MODE, WhiteBoardActivity.MODE_NEW);
                        whiteboard.putExtra(WhiteBoardActivity.NOTEBOOK_NAME, NewNoteBookInput.getText().toString());
                        startActivity(whiteboard);
                    }
                    else {
                        NewNoteBookInput.setError("Please Choose A Different Name");
                    }
                }
                break;
        }
    }

}
