package gr.teicm.notepad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gr.teicm.notepad.Model.CustomFileAdapter;
import gr.teicm.notepad.Model.CustomNotes;
import gr.teicm.notepad.Repository.EditNotes;
import gr.teicm.notepad.Repository.ShowNote;

public class MainActivity extends AppCompatActivity {

    private List<String> files = new ArrayList<>();
    private List<String> dates = new ArrayList<>();
    private ListView listView;
    ArrayList<CustomNotes> customNotes = new ArrayList<>();
    CustomFileAdapter customFileAdapter;

    String path ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        path = this.getFilesDir().getPath() +"/";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        date();
        listView = findViewById(R.id.fileList);
        listView.setLongClickable(true);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, EditNotes.class);
                        startActivity(intent);
                        onRestart();
            }
        });

        customFileAdapter = new CustomFileAdapter(this,R.layout.content_main,customNotes);
        listFilesForFolder();
        listView.setAdapter(customFileAdapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                View promptView = layoutInflater.inflate(R.layout.delete_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(promptView);

                alertDialogBuilder.setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                CustomNotes note = (CustomNotes) parent.getItemAtPosition(position);
                                String name = note.getName();
                                File file = new File(path+name+".txt");
                                file.delete();
                                onRestart();

                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CustomNotes note = (CustomNotes) parent.getItemAtPosition(position);
                String name = note.getName();
                Intent i = new Intent(MainActivity.this, ShowNote.class);
                i.putExtra("fileData",Open(name+".txt"));
                i.putExtra("fileName",name+ ".txt");
                startActivity(i);
            }

        });



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        customNotes.clear();
        listFilesForFolder();
        customFileAdapter = new CustomFileAdapter(this,R.layout.content_main,customNotes);
        listView.setAdapter(customFileAdapter);
    }

    public void listFilesForFolder() {
        File folder = new File(this.getFilesDir().getPath());
        Date date;

        for (final File fileEntry : folder.listFiles()) {
            date = new Date(fileEntry.lastModified());
            files.add(fileEntry.getName().replace(".txt",""));
            customNotes.add(new CustomNotes(fileEntry.getName().replace(".txt",""),date.toString()));
        }
    }

public void date(){
    File folder = new File(this.getFilesDir().getPath());
    for (final File fileEntry : folder.listFiles()) {

        Date date = new Date(fileEntry.lastModified());
        dates.add(date.toString());
    }
}



    public String Open(String fileName) {
        String content = "";
        if (FileExists(fileName)) {
            try {
                InputStream in = openFileInput(fileName);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        return content;
    }


    public boolean FileExists(String fname) {
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }
}
