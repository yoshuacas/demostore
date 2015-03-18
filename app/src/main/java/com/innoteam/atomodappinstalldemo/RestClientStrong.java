package com.innoteam.atomodappinstalldemo;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;


public class RestClientStrong extends AsyncTask<String, Void, Boolean> {

    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> urlParams;
    private ArrayList<NameValuePair> headers;

    private String url;
    private int responseCode;
    private String message;
    private String response;
    private OnTaskComplete onTaskComplete;

    private static final String TAG = "ATOM_ASYNCTASK";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Boolean result) {
        //Sending execution control to callback method in MainActivity

        if (message != null && response != null)
            onTaskComplete.setMyTaskComplete(message, 1);
        else
            onTaskComplete.setMyTaskComplete("",1);
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

                    //executeRequest(request, url);
                    break;
                }
                case "POST": {
                    Log.d(TAG,"Inicio del POST");

                    String combinedParams = "";
                    if (!urlParams.isEmpty()){
                        if (!urlParams.isEmpty()) {
                            combinedParams += "?";
                            for (NameValuePair p : urlParams) {
                                String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                                if (combinedParams.length() > 1) {
                                    combinedParams += "&" + paramString;
                                } else {
                                    combinedParams += paramString;
                                }
                            }
                        }

                    }

                    HttpPost request = new HttpPost(url+combinedParams);

                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams,5000);
                    HttpConnectionParams.setStaleCheckingEnabled(httpParams,true);
                    HttpConnectionParams.setSoTimeout(httpParams,5000);

                    request.setParams(httpParams);

                    // add headers
                    for (NameValuePair h : headers) {
                        request.addHeader(h.getName(), h.getValue());
                    }


                    if (!params.isEmpty()) {
                        JSONObject data = new JSONObject();
                        try {
                            for (NameValuePair p : params) {
                                data.put(p.getName(),p.getValue());
                                Log.d(TAG,p.getName()+":"+p.getValue());
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

                    executeRequest(request, url+combinedParams);

                    if (response != null) {
                        Log.d(TAG, response);

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

    private void executeRequest(HttpPost request, String url) throws Exception {
        Log.d(TAG,"Inicio del executeRequest");

        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            Log.d(TAG,"Envio de SOlicitud al Server");

            System.out.println("Executing request " + request.getRequestLine());

            // Create a custom response handler
            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

                public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
                    HttpEntity entity = response.getEntity();
                    int responseCode = response.getStatusLine().getStatusCode();
                    message = response.getStatusLine().getReasonPhrase();
                    return entity != null ? EntityUtils.toString(entity) : null;
                }

            };
            response = httpclient.execute(request, responseHandler);
            System.out.println("----------------------------------------");
            System.out.println(response);
        } finally {
            httpclient.close();
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

    public RestClientStrong(String url) {
        this.url = url;
        params = new ArrayList<NameValuePair>();
        urlParams = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    public void AddParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void AddUrlParam(String name, String value) {
        urlParams.add(new BasicNameValuePair(name, value));
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






