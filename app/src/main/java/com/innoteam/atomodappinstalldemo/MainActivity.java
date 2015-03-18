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

    private static RestClientStrong mytask;

    private TelephonyManager telephonyManager;
    private String reqId = "";
    private String token = null;
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
            //mainTextView.setText(str);

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

                    login(getApplicationContext().getString(R.string.strongURLLogin));







            }

        });

    }

    public void login(String url){

        mytask = new RestClientStrong(url);

        mytask.setMyTaskCompleteListener(new RestClientStrong.OnTaskComplete() {
            @Override
            public void setMyTaskComplete(String message, int number) {

                Log.d("LOGIN", "Login Recibido del servidor");

                try {
                    JSONObject jsonObject = new JSONObject(mytask.getResponse());
                    token  = jsonObject.getString("id");

                    pushApp (getApplicationContext().getString(R.string.strongURLPush));

                    Log.d("LOGIN", "JSON OBJECT" + jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        mytask.AddHeader("Content-Type","application/json");
        mytask.AddParam("email", getApplicationContext().getString(R.string.username));
        mytask.AddParam("password", getApplicationContext().getString(R.string.password));
        mytask.execute("POST");

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

    public void pushApp(String url){

        RestClientStrong taskPush = new RestClientStrong(url);

        taskPush.setMyTaskCompleteListener(new RestClientStrong.OnTaskComplete() {
            @Override
            public void setMyTaskComplete(String message, int number) {

                Log.d("PUSH", "ODPUSH recibido");

                try {
                    JSONObject jsonObject = new JSONObject(mytask.getResponse());
                    reqId = jsonObject.getString("id");
                    notifyDevice();
                    Log.d("ODPUSH", "JSON OBJECT" + jsonObject.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        final TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        taskPush.AddUrlParam("access_token", token); //Add authentication
        taskPush.AddParam("atomAppId", getApplicationContext().getString(R.string.atomAppId));
        taskPush.AddParam("imsi", tm.getSubscriberId());
        taskPush.AddParam("storeName", "Tienda");
        taskPush.AddParam("storeTransactionId", "Tien234234234da");
        taskPush.AddParam("status", "CREATE");
        taskPush.AddParam("created", "2015-03-13 13:21:24.038Z");
        taskPush.AddParam("lastUpdated", "2015-03-13 13:21:24.038Z");
        taskPush.AddHeader("Content-Type","application/json");
        taskPush.execute("POST");
}


    public void notifyDevice(){
        Intent intent = new Intent(ATOM);
        intent.putExtra("requestId",reqId);

        sendBroadcast(intent);
    }




}
