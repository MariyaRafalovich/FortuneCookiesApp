package com.example.mariya.fortunecookiesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SecondActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    public ListView dispList;
    public String jsonStr="";
    public ArrayList<Contact> contactList = new ArrayList<>();
    private Timer timer;
    private final Handler handler = new Handler();
    public Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        dispList = (ListView)findViewById(R.id.list);

        timer = new Timer();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            new GetContacts().execute("http://10.0.2.2/Fortune-Cookies-master-to-connect%20with%20Android/api/getData.php");
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, 0, 30000);//execute 1 minute
    }

    private boolean isConnected () {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

       boolean isMobileConn = networkInfo.isConnected();

        Log.d(TAG, "Wifi connected: " + isWifiConn);
        Log.d(TAG, "Mobile connected: " + isMobileConn);

     if (isWifiConn || isMobileConn) {
            return true;
        }else{
            return false;
        }
    }

    private class GetContacts extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //CLEAN LIST AND SHOW ONE BY ONE
            adapter = new ItemAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, contactList) {
            };
            contactList.clear();
            dispList.setAdapter(null);

            if(!isConnected())

                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            }

       @Override
        protected String doInBackground(String... arg) {

            if(isConnected()){
                // Making a request to url and getting response
                HTTPHandler sh = new HTTPHandler();
                jsonStr = sh.makeServiceCall(arg[0]);
            }else{
                String filename="contacts.json";
                StringBuffer stringBuffer = new StringBuffer();

                try {
                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(openFileInput(filename)));
                    String inputString;

                    while ((inputString = inputReader.readLine()) != null) {
                        stringBuffer.append(inputString);
                    }

                    jsonStr = stringBuffer.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ParseJson())
                return "AsyncTask finished";
            else
                return "Error";
        }

        @Override
        protected void onPostExecute(String retString) {
            super.onPostExecute(retString);

            Toast.makeText(SecondActivity.this,retString,Toast.LENGTH_LONG).show();

            //List Adapter
            dispList.setAdapter((ItemAdapter)adapter);

            if(isConnected()){
                WriteJsonInLocalStorage();
            }

                   // list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   //     @Override
                   //     public void onItemClick(AdapterView<?> adapter, View view, final int position, long arg) {

                            //Toast.makeText(getApplicationContext(),contactList.get(position).getEmail(),Toast.LENGTH_LONG).show();

                   //         Intent i = new Intent(getApplicationContext(),activity_second.class);
                   //         i.putExtra("contact",contactList.get(position));
                    //        startActivity(i);
                }


           // Toast.makeText(getApplicationContext(), "data has been refreshed", Toast.LENGTH_LONG).show();// every 30 sec it will refresh the list

        }

        public void WriteJsonInLocalStorage() {

            String filename="contacts.json";
            String data= jsonStr;

            FileOutputStream fos;

            try {
                fos = openFileOutput(filename, Context.MODE_PRIVATE);
                fos.write(data.getBytes());
                fos.close();

                Toast.makeText(getApplicationContext(), filename + " saved", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Log.d("Main_activity",e.getMessage());
            }
        }

        public Boolean ParseJson () {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = jsonObj.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    String idfcookoes = c.getString("idfcookies");
                    String des = c.getString("des");

                    contactList.add(new Contact(idfcookoes, des));
                }

            } catch (final JSONException e) {
                Log.e(TAG, "Impossible to download the json file.");
                Toast.makeText(getApplicationContext(),"Impossible to download the json.",Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }
    }

