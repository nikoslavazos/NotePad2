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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import gr.teicm.notepad.Repository.EditNotes;
import gr.teicm.notepad.Repository.ShowNote;

public class MainActivity extends AppCompatActivity {

    private List<String> files = new ArrayList<>();
    private ListView listView;

    private ArrayAdapter<String> adapter;
    String path ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        path = this.getFilesDir().getPath() +"/";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.fileList);
        listView.setLongClickable(true);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, EditNotes.class);
                        startActivity(intent);
            }
        });


        listFilesForFolder();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                files);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                View promptView = layoutInflater.inflate(R.layout.delete_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(promptView);

                alertDialogBuilder.setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                File file = new File(path+parent.getItemAtPosition(position)+".txt");
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
                String name = (String) parent.getItemAtPosition(position);
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
        files.clear();
        listFilesForFolder();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                files);
        listView.setAdapter(adapter);
    }

    public void listFilesForFolder() {
        File folder = new File(this.getFilesDir().getPath());
        for (final File fileEntry : folder.listFiles()) {
            files.add(fileEntry.getName().replace(".txt",""));
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
