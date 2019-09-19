package com.example.prave.iot;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
public TextView temp,humid,lastupdate;
public Button refresh;
public CardView crd;
int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //feed1  replace with your username feedname and AIO key
        final String link_temp="https://io.adafruit.com/api/v2/vishal11/feeds/medtemp/data?x-aio-key=45fb50108ccd48c68786f9e7e201b561";
        //feed2 replace with your username feedname and AIO key
        final String link_moist="https://io.adafruit.com/api/v2/vishal11/feeds/medhum/data?x-aio-key=45fb50108ccd48c68786f9e7e201b561";
        temp=(TextView)findViewById(R.id.temp) ;
        humid=(TextView)findViewById(R.id.humid);
        refresh=(Button)findViewById(R.id.button4);
        lastupdate=(TextView)findViewById(R.id.textView15);
        crd=(CardView)findViewById(R.id.cardView);
        HttpGetRequest hp=new HttpGetRequest();
        hp.execute(link_temp);
        HttpGetRequestMoist m=new HttpGetRequestMoist();
        m.execute(link_moist);
        //Values gets updated on clicking the refresh button
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HttpGetRequestMoist m1=new HttpGetRequestMoist();
                m1.execute(link_moist);
                HttpGetRequest t1=new HttpGetRequest();
                t1.execute(link_temp);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date d = new Date();
                lastupdate.setText(formatter.format(d));
              //  crd.setCardBackgroundColor(Color.RED);
            }
        });
    }
    //Async Task Method to GET
    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                Log.d("stuff", "doInBackground: "+result);
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONArray jsonArray = new JSONArray(result);

                JSONObject explrObject = jsonArray.getJSONObject(0);
               // Log.d("values", "onPostExecute: "+explrObject.get("value"));
                String value=explrObject.getString("value");

                temp.setText(value+" C");
                //replace with whatever condition you want
                //Change the data type accordingly
                if(Float.parseFloat(value)>29.5){
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            MainActivity.this).create();
                    alertDialog.setTitle("Alert Dialog");
                    //set alert message
                    alertDialog.setMessage("This medicine is about to exceed  optimal temperature please take control action");
                   // alertDialog.setIcon(R.drawable.tick);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK" ,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            flag=1;
                            Toast.makeText(getApplicationContext(), "Please try to maintain optimal temperature", Toast.LENGTH_SHORT).show();
                            crd.setCardBackgroundColor(Color.YELLOW);
                        }
                    });
                    alertDialog.show();

                }
              /*  if(Float.parseFloat(value)>30&&flag==1){
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            MainActivity.this).create();
                    alertDialog.setTitle("WARNING");
                    alertDialog.setMessage("This medicine is not maintanied at  optimal temperature please take control action");
                    // alertDialog.setIcon(R.drawable.tick);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK" ,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            Toast.makeText(getApplicationContext(), "Please try to maintain optimal temperature", Toast.LENGTH_SHORT).show();
                            crd.setCardBackgroundColor(Color.RED);
                        }
                    });
                    alertDialog.show();

                } */


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //Request for 2nd feed
    public class HttpGetRequestMoist extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
//            String feed=params[1];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                Log.d("stuff", "doInBackground: "+result);

            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONArray jsonArray = new JSONArray(result);

                JSONObject explrObject = jsonArray.getJSONObject(0);
                String value=explrObject.getString("value");
                humid.setText(value+" %");
                //replace with your conditions
                if(Float.parseFloat(value)>96){
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            MainActivity.this).create();
                    alertDialog.setTitle("Alert Dialog");
                    alertDialog.setMessage("This medicine is about to exceed  optimal humidity please check before use");
                    // alertDialog.setIcon(R.drawable.tick);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK" ,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            Toast.makeText(getApplicationContext(), "Please make sure that you check the medicine before use", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.show();
                    crd.setCardBackgroundColor(Color.YELLOW);
                }
            /*    if(Float.parseFloat(value)>98){
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            MainActivity.this).create();
                    alertDialog.setTitle("Alert Dialog");
                    alertDialog.setMessage("This medicine is about to exceed  optimal humidity please check before use");
                    // alertDialog.setIcon(R.drawable.tick);
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK" ,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            Toast.makeText(getApplicationContext(), "Please make sure that you check the medicine before use", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog.show();
                    crd.setCardBackgroundColor(Color.RED);
                } */



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

