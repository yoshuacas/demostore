package com.innoteam.atomodappinstalldemo;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RestClient extends AsyncTask<String, Void, Boolean> {

    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> headers;

    private String url;
    private int responseCode;
    private String message;
    private String response;
    private OnTaskComplete onTaskComplete;

    private static final String TAG = "ASYNCTASK";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Boolean result) {
        //Sending execution control to callback method in MainActivity
        onTaskComplete.setMyTaskComplete(message, 1);
    }

    @Override
    protected Boolean doInBackground(String... method) {
        try {
            Log.d(TAG,"Inicio del doInBackground " + method [0]);
            switch (method[0]) {
                case "GET": {
                    // add parameters
                    String combinedParams = "";
                    if (!params.isEmpty()) {
                        combinedParams += "?";
                        for (NameValuePair p : params) {
                            String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                            if (combinedParams.length() > 1) {
                                combinedParams += "&" + paramString;
                            } else {
                                combinedParams += paramString;
                            }
                        }
                    }

                    HttpGet request = new HttpGet(url + combinedParams);

                    // add headers
                    for (NameValuePair h : headers) {
                        request.addHeader(h.getName(), h.getValue());
                    }

                    executeRequest(request, url);
                    break;
                }
                case "POST": {
                    Log.d(TAG,"Inicio del POST");
                    HttpPost request = new HttpPost(url);

                    // add headers
                    for (NameValuePair h : headers) {
                        request.addHeader(h.getName(), h.getValue());
                    }

                    if (!params.isEmpty()) {
                        JSONObject data = new JSONObject();
                        try {
                            for (NameValuePair p : params) {
                                data.put(p.getName(),p.getValue());
                            }
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(data);

                            StringEntity se = new StringEntity(data.toString());
                            se.setContentType("application/json");
                            request.setEntity(se);

                        } catch (JSONException e) {
                            Log.e(TAG,"Error creando el JSON data");
                            e.printStackTrace();
                        }

                    }
                    Log.d(TAG,"Antes de Execute Request");
                    executeRequest(request, url);

                    if (response != null) {
                        Log.d("ASYNC", response);

                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void executeRequest(HttpUriRequest request, String url) throws Exception {
        Log.d(TAG,"Inicio del executeRequest");
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
            Log.d(TAG,"Envio de SOlicitud al Server");
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();

                response = convertStreamToString(instream);

                // Closing the input stream will trigger connection release
                instream.close();
            }

        } catch (Exception e) {
            client.getConnectionManager().shutdown();
            throw e;
        }
    }

    private static String convertStreamToString(InputStream is) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return message;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public RestClient(String url) {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public void AddParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }


    public void AddHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public interface OnTaskComplete {
        public void setMyTaskComplete(String message, int number);
    }

    public void setMyTaskCompleteListener(OnTaskComplete onTaskComplete) {
        this.onTaskComplete = onTaskComplete;
    }


}