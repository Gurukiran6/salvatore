package com.example.salvatore;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

public class DeleteContacts extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final int CONTACT_PICKER_REQUEST = 991;
    static List<String> contactList = new ArrayList<String>();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    List<String> name = new ArrayList<String>();
    Set set = new TreeSet();
    List<String> phone = new ArrayList<String>();
    Set set1 = new TreeSet();
    MyAdapter ma;
    Button delete;
    int id ;
    private DBHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);


        mydb = new DBHelper(this);

        getAllContacts(this.getContentResolver());
        ListView lv = (ListView) findViewById(R.id.lv);
        ma = new MyAdapter();
        lv.setAdapter(ma);
        lv.setOnItemClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);

        // adding
        delete = (Button) findViewById(R.id.button1);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    for (int i = 0; i < name.size(); i++) {
                        if (ma.mCheckStates.get(i) == true) {


                                mydb.deleteContact(phone.get(i).toString());
                                Toast.makeText(getApplicationContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Some field is empty", Toast.LENGTH_SHORT).show();

                        }
                    }

                    openNewActivity();

                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Some field is Empty1", Toast.LENGTH_LONG).show();
                }

            }
        });
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void openNewActivity(){
        Intent intent=new Intent(DeleteContacts.this,Main.class);
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
        name=mydb.getAllCotacts();
        phone=mydb.selectedCotacts();

    }
    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener  {
        public SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView tv;
        CheckBox cb;

        MyAdapter() {
            mCheckStates = new SparseBooleanArray(name.size());
            mInflater = (LayoutInflater) DeleteContacts.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name.size();
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
                vi = mInflater.inflate(R.layout.delete_contacts, null);
            tv = (TextView) vi.findViewById(R.id.textView1);

            cb = (CheckBox) vi.findViewById(R.id.checkBox1);
            tv.setText(name.get(position));


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