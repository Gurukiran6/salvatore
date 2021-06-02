package com.example.salvatore;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.content.pm.PackageManager;
import androidx.appcompat.app.ActionBar;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;
import java.util.Collections;
import  java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import android.widget.CheckBox;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

public class SelectContacts extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final int CONTACT_PICKER_REQUEST = 991;
    static List<String> contactList = new ArrayList<String>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    List<String> name = new ArrayList<String>();
    Set set = new TreeSet();
    List<String> phone = new ArrayList<String>();
    Set set1 = new TreeSet();
    MyAdapter ma;
    
    Button select;
    private DBHelper mydb;
    private DBHelper1 mydb1;
    List<String> name2 = new ArrayList<String>();
    List<String> phone2 = new ArrayList<String>();
    List<String> name3 = new ArrayList<String>();
    ArrayList<String> Names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_contacts);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);


        mydb = new DBHelper(this);
        mydb1 = new DBHelper1(this);
        getAllContacts(this.getContentResolver());
        ListView lv = (ListView) findViewById(R.id.lv);
        ma = new MyAdapter();
        lv.setAdapter(ma);
        lv.setOnItemClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);

        // adding
        select = (Button) findViewById(R.id.button1);
        select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    for (int i = 0; i < name.size(); i++) {
                        if (ma.mCheckStates.get(i) == true) {

                            if (mydb.existContact(phone2.get(i).toString()) == true) {
                                Toast.makeText(getApplicationContext(), "Already Exist!", Toast.LENGTH_SHORT).show();
                            } else {


                                mydb.insertContact(name2.get(i).toString(), phone2.get(i).toString());
                                Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                 openNewActivity();

                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Some field is Empty", Toast.LENGTH_LONG).show();
                }

            }
        });
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }



    public void openNewActivity(){
        Intent intent=new Intent(SelectContacts.this,SelectedContacts.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        ma.toggle(arg2);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAllContacts(ContentResolver cr) {
        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor phones = cr.query(CONTENT_URI, projection,null
                , null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if(phones != null) {
            try {
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String regex = "[^0-9+]";
                while (phones.moveToNext()) {

                    String normalizedNumber = phones.getString(indexOfNormalizedNumber);

                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                        String name1 = phones.getString(indexOfDisplayName);
                        String phoneNumber = phones.getString(indexOfDisplayNumber);

                        name.add(name1);
                        phone.add(phoneNumber.replaceAll(regex, ""));


                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Some field is Empty", Toast.LENGTH_LONG).show();
            }

            for (int i = 0; i < name.size(); i++) {
                if (mydb1.existContact(phone.get(i).toString()) == true) {

                } else {
                    mydb1.insertContact(name.get(i).toString(), phone.get(i).toString());
                }
            }
            //name.clear();
            name2 = mydb1.getAllCotacts();
            phone2 = mydb1.selectedCotacts();

            //Collections.sort(name);
            phones.close();
        }
    }


    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener  {
        public SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView tv1, tv;
        CheckBox cb;

        MyAdapter() {
            mCheckStates = new SparseBooleanArray(name2.size());
            mInflater = (LayoutInflater) SelectContacts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name2.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (convertView == null)
                vi = mInflater.inflate(R.layout.row, null);
            tv = (TextView) vi.findViewById(R.id.textView1);
            tv1 = (TextView) vi.findViewById(R.id.textView2);
            cb = (CheckBox) vi.findViewById(R.id.checkBox1);
            tv.setText(name2.get(position));

            tv1.setText("Phone No :" + phone2.get(position));
            cb.setTag(position);
            cb.setChecked(mCheckStates.get(position, false));
            cb.setOnCheckedChangeListener(this);

            return vi;
        }

        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub

            mCheckStates.put((Integer) buttonView.getTag(), isChecked);
        }




    }



}