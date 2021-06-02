package com.example.salvatore;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;





public class SelectedContacts extends AppCompatActivity {

    DBHelper mydb;
    private ListView obj;

    int id_To_Update = 0;
    @Override
    protected void onCreate (Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);
        obj=(ListView) findViewById(R.id.list_item);
        mydb = new DBHelper(this);

        ArrayList array_list1 = mydb.getAllCotacts();

        try{


            ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1, array_list1);

            obj.setAdapter(arrayAdapter);

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Some field is Empty1", Toast.LENGTH_LONG).show();
        }
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(SelectedContacts.this, Main.class));
                this.finish();
                return true;
            case R.id.Delete_Contact:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteContact)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(getApplicationContext(),DeleteContacts.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }
}