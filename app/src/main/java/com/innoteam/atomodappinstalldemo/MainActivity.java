package com.innoteam.atomodappinstalldemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {
    final Context context = this;

    private static RestClient mytask;
    private static RestClient2 mytask2;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void addListenerOnButton() {

        button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("Tu App esta en camino :)");


                // set dialog message
                alertDialogBuilder
                        .setMessage("Te avisaremos cuando tu descarga este lista!")
                        .setCancelable(false)
                        .setIcon(R.drawable.ns_icon)
                        .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                MainActivity.this.finish();
                            }
                        });


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                try {
                    addOnAppUser("https://api.parse.com/1/functions/odAppInstallDev");
                    Thread.sleep(1000);
                    //notifyDevice("https://android.googleapis.com/gcm/send");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }





            }

        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addOnAppUser(String url){

        mytask = new RestClient(url);

        mytask.setMyTaskCompleteListener(new RestClient.OnTaskComplete() {
            @Override
            public void setMyTaskComplete(String message, int number) {

                Log.d("MYTASK", "APPList recibido del servidor");

                try {
                    JSONObject jsonObject = new JSONObject(mytask.getResponse());
                    Log.d("MYTASK", "JSON OBJECT" + jsonObject.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        String version = Build.VERSION.RELEASE;

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        mytask.AddParam("imsi", tm.getSubscriberId());
        mytask.AddParam("userId", "7FO6tM161x");
        mytask.AddParam("mcc", "732");
        mytask.AddParam("mnc", "103");
        mytask.AddParam("appName", "Deezer");
        mytask.AddParam("version", "23");
        mytask.AddParam("appId", "LDPrNXHyBC");
        mytask.AddHeader("X-Parse-Application-Id","Tma29vMR9R7y0kjmVLwAQtIOQpedkIqF7vKiW1kG");
        mytask.AddHeader("X-Parse-REST-API-Key","39mqoaMtyTpYchP8n5kkjzrNypLdVYxD2SxiWWEi");
        mytask.AddHeader("Content-Type","application/json");
        mytask.execute("POST");
}


    public void notifyDevice(String url){

        mytask2 = new RestClient2(url);

        mytask2.setMyTaskCompleteListener(new RestClient2.OnTaskComplete() {
            @Override
            public void setMyTaskComplete(String message, int number) {

                Log.d("MYTASK", "APPList recibido del servidor");

                try {
                    JSONObject jsonObject = new JSONObject(mytask2.getResponse());
                    Log.d("MYTASK", "JSON OBJECT" + jsonObject.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        String version = Build.VERSION.RELEASE;

        mytask2.AddHeader("Authorization","key=AIzaSyAOgBIqM3hVkJXas61RTUKZlIxi-l3t-Ak");
        mytask2.AddHeader("Content-Type","application/json");
        mytask2.execute("POST");
    }

}
