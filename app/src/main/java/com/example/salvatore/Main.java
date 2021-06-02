package com.example.salvatore;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class Main extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "MESSAGE";
    DBHelper mydb;
    LocationManager locationManager;
    String latitude, longitude;
    private ListView obj;
    ImageButton send;
   Button select_button;
   Button selected_button;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

        obj = (ListView) findViewById(R.id.list_item);
        mydb = new DBHelper(this);
        ArrayList array_list = mydb.getAllCotacts();
        List<String> name = new ArrayList<String>();


        int permission_All = 1;
        String[] Permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS, };
        if(!hasPermissions(Main.this, Permissions)){
            ActivityCompat.requestPermissions(Main.this, Permissions, permission_All);
        }

        send = (ImageButton) findViewById(R.id.alert);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //Check gps is enable or not

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //Write Function To enable gps

                    OnGPS();
                } else {
                    //GPS is already On then

                    getLocation();

                }

            }
        });
        select_button=(Button)findViewById(R.id.select);
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this,SelectContacts.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Maximum 5 contacts are allowed", Toast.LENGTH_LONG).show();

        }
        });


        selected_button=(Button)findViewById(R.id.selected);
        selected_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Main.this,SelectedContacts.class);
                startActivity(intent);

            }
        });
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("786ACDDD4B8258D997D8ABF5B11024C5"))
                        .build());

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus status) {

            }
        });
         mAdView = new AdView(this);

        mAdView.setAdSize(AdSize.BANNER);

        mAdView.setAdUnitId("ca-app-pub-4455231867863882/1536567787");

        mAdView = findViewById(R.id.adView);
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        selected_button=(Button)findViewById(R.id.selected);

    }
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public static boolean hasPermissions(Context context, String... permissions){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!=PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }
    private void getLocation() {

        //Check Permissions again

        if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Main.this, new String[]{
                    Manifest.permission.SEND_SMS
            }, 100);
        } else {

            Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);


            if (LocationGps != null) {
                double lat = LocationGps.getLatitude();
                double longi = LocationGps.getLongitude();
                if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main.this,

                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 100);
                    ActivityCompat.requestPermissions(Main.this, new String[]{
                            Manifest.permission.SEND_SMS
                    }, 100);
                }
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                ArrayList array_list1 = mydb.selectedCotacts();
                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0; i < array_list1.size(); i++) {
                    String number = array_list1.get(i).toString();
                    String message = "hello Dude" + "\n" + "Location:" + "\n" + "https://maps.google.com/?q=" + latitude + "," + longitude;
                    smsManager.sendTextMessage(number, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();

                }


            } else if (LocationNetwork != null) {
                double lat = LocationNetwork.getLatitude();
                double longi = LocationNetwork.getLongitude();
                if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main.this,

                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 100);
                    ActivityCompat.requestPermissions(Main.this, new String[]{
                            Manifest.permission.SEND_SMS
                    }, 100);
                }
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                ArrayList array_list1 = mydb.selectedCotacts();
                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0; i < array_list1.size(); i++) {
                    String number = array_list1.get(i).toString();
                    String message = "hello Dude" + "\n" + "Location:" + "\n" + "https://maps.google.com/?q=" + latitude + "," + longitude;
                    smsManager.sendTextMessage(number, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();

                }

            } else if (LocationPassive != null) {
                double lat = LocationPassive.getLatitude();
                double longi = LocationPassive.getLongitude();
                if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main.this,

                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Main.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 100);
                    ActivityCompat.requestPermissions(Main.this, new String[]{
                            Manifest.permission.SEND_SMS
                    }, 100);
                }
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);

                ArrayList array_list1 = mydb.selectedCotacts();
                SmsManager smsManager = SmsManager.getDefault();
                for (int i = 0; i < array_list1.size(); i++) {
                    String number = array_list1.get(i).toString();
                    String message = "hello Dude" + "\n" + "Location:" + "\n" + "https://maps.google.com/?q=" + latitude + "," + longitude;
                    smsManager.sendTextMessage(number, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(getApplicationContext(), "Please turn on your mobile data!", Toast.LENGTH_LONG).show();
            }
        }

    }

    //Thats All Run Your App





    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }
}

