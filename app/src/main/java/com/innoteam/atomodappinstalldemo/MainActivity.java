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
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {
    final Context context = this;

    private static RestClient mytask;
    private static RestClient2 mytask2;
    private TelephonyManager telephonyManager;
    private String reqId = "";
    TextView mainTextView;
    Button button;

    final public static String ATOM = "com.innoteam.atom.ONDEMANDINSTALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
        Context mContext = getApplicationContext();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

         mainTextView = (TextView) findViewById(R.id.networktext);


        registerTM();


    }


    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onCallForwardingIndicatorChanged(boolean cfi) {}
        public void onCallStateChanged(int state, String incomingNumber) {}
        public void onCellLocationChanged(CellLocation location) {}
        public void onDataActivity(int direction) {}
        public void onDataConnectionStateChanged(int state) {}
        public void onMessageWaitingIndicatorChanged(boolean mwi) {}
        public void onServiceStateChanged(ServiceState serviceState) {}
        public void onSignalStrengthsChanged(SignalStrength asu) {

            Calendar c = Calendar.getInstance();
            int seconds = c.get(Calendar.SECOND);

            Time   time = new Time ();
            time.setToNow();

            String str="Hora:"+time.hour+":"+time.minute+":"+time.second+"\n";

            str+="GSM Signal strenght: " + asu.getGsmSignalStrength()+ "\n";
            str+="GSM bit error rate (0-7, 99): " + asu.getGsmBitErrorRate()+ "\n";
            str+="signal to noise ratio: " + asu.getEvdoSnr()+ "\n";

            str+="CDMA RSSI value in dBm: " + asu.getCdmaDbm() + "\n";
            str+="CDMA Ec/Io value in dB*10: " + asu.getCdmaEcio()+ "\n";
            str+="EVDO RSSI value in dBm: " + asu.getEvdoDbm()+ "\n";
            str+="EVDO Ec/Io value in dB*10: " + asu.getEvdoEcio()+ "\n";
            str+="Other info: " + asu.toString();
            mainTextView.setText(str);

        }
    };


        public void registerTM (){
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR |
                            PhoneStateListener.LISTEN_CALL_STATE |
                            PhoneStateListener.LISTEN_CELL_LOCATION |
                            PhoneStateListener.LISTEN_DATA_ACTIVITY |
                            PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                            PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR |
                            PhoneStateListener.LISTEN_SERVICE_STATE |
                            PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

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
                                //MainActivity.this.finish();
                            }
                        });


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                try {
                    addOnAppUser("https://api.parse.com/1/functions/odAppInstall");
                    Thread.sleep(5000);
                    notifyDevice();
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
                    JSONObject result  = jsonObject.getJSONObject("result");
                    reqId=result.getString("objectId");

                    Log.d("MYTASK", "JSON OBJECT" + jsonObject.toString()+" - -" + reqId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        String version = Build.VERSION.RELEASE;

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String s1 = getString(R.string.X_Parse_Application_Id);
        String s2 = getString(R.string.X_Parse_REST_API_Key);
        mytask.AddParam("imsi", tm.getSubscriberId());
        mytask.AddParam("userId", "7FO6tM161x");
        mytask.AddParam("mcc", "732");
        mytask.AddParam("mnc", "103");
        mytask.AddParam("appName", "Deezer");
        mytask.AddParam("version", "23");
        mytask.AddParam("appId", "LDPrNXHyBC");
        mytask.AddHeader("X-Parse-Application-Id",s1);
        mytask.AddHeader("X-Parse-REST-API-Key",s2);
        mytask.AddHeader("Content-Type","application/json");
        mytask.execute("POST");
}


    public void notifyDevice(){
        Intent intent = new Intent(ATOM);
        intent.putExtra("requestId",reqId);

        sendBroadcast(intent);
    }




}
