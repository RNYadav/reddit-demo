package com.reddit.demo.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.reddit.demo.utils.Config.TAG;

/**
 * Created by Ravi Yadav on 31/10/17
 * ====================================
 * This is the Network Handler CLass to prevent excess memory leak in our applications
 * This class will also help us keep the network class very handy to maintain and modify
 **/

public class NetworkHandler extends AsyncTask<Void, Void, JSONObject> {
    private static final int MY_SOCKET_TIMEOUT_MS = 15000;
    private String url;
    private JSONObject result;
    @SuppressLint("StaticFieldLeak")
    private Context context;

    // This is the reference to the associated listener
    private TaskListener taskListener;

    // Interface for the listener
    public interface TaskListener {
        // On Success
        void onFinished(JSONObject result);
        // On Failure
        void onFailure(VolleyError volleyError);
    }

    // Constructor to call Get Methods
    public NetworkHandler(Context context, String url, TaskListener listener){
        this.context = context;
        this.taskListener = listener;
        // Check if url is null
        // if the url is null return 404 error
        if(url==null) {
            listener.onFailure(new VolleyError());
            return;
        }
        //checking if the url is complete or just an url part
        if(url.contains(".com/"))
            this.url = url;
        else{
            this.url = new Uri.Builder().scheme(Config.SITE_PROTOCOL)
                    .authority(Config.SITE_DOMAIN)
                    .path("/"+url)
                    .appendQueryParameter("limit", Config.DATA_LIMIT(context))
                    .appendQueryParameter("geo_filter", Config.GEO_LIMIT(context))
                    .build().toString();
        }
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        Cache cache = RequestController.getInstance(context).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if(entry != null){
            try {
                // Get JSON from the data
                if(!entry.isExpired())
                    result = new JSONObject(new String(entry.data, "UTF-8"));
                else {
                    //if(NetworkChangeReceiver.isConnected()) {
                    cache.invalidate(url, true);
                    getDataFromNetwork();
                    //}else
                    // result = new JSONObject(new String(entry.data, "UTF-8"));
                }
            } catch (Exception e) {
                Log.e("CACHE EXCEPTION", "doInBackground: ",e );
                cache.invalidate(url,true);
                getDataFromNetwork();
            }
        } else {
            //Make network call
            getDataFromNetwork();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        if(result!=null)
            taskListener.onFinished(result);
        super.onPostExecute(jsonObject);
    }

    private void getDataFromNetwork() {
        Log.e(TAG, "getDataFromNetwork: " + url);

        if (url.contains("/comments/")){
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {
                    // If ALL GOES WELL, we call the callback function on it.
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("jsonArray", jsonArray);
                        Log.e(TAG, "onResponse: " + jsonObject);
                        taskListener.onFinished(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        taskListener.onFailure(new VolleyError());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    taskListener.onFailure(volleyError);
                }
            });

            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestController.getInstance(context).addToRequestQueue(jsonArrayRequest);
        }
        else {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    // If ALL GOES WELL, we call the callback function on it.
                    Log.e(TAG, "onResponse: " + jsonObject);
                    taskListener.onFinished(jsonObject);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    taskListener.onFailure(volleyError);
                }
            });

        /*
          Setting the update policy to update the timeout duration
          by setting the MY_SOCKET_TIMEOUT_MS to 5000ms i.e. 5sec.
          the socket will wait for the response to wait for the max of 5 sec before sending the timeout error
         */
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestController.getInstance(context).addToRequestQueue(jsonObjectRequest);
        }
    }

}
