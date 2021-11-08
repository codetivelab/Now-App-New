package com.buzzware.nowapp.NetworkRequests;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.NetworkRequests.Interfaces.NetWorkRequestsCallBack;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class NetworkRequests {
    public static NetworkRequests networkRequests;
    Context context;

    public static NetworkRequests GetNetworkRequests(Context context)
    {
        if(networkRequests == null)
        {
            networkRequests= new NetworkRequests(context);
        }
        return networkRequests;
    }

    public NetworkRequests(Context context) {
        this.context = context;
    }

    public void GetVenueDetail(NetWorkRequestsCallBack netWorkRequestsCallBack, String venueName, String venueAddress, Context context)
    {
        String finalQueryURL= Constant.GetConstant().getApiBaseUrl()+"?api_key_private="+context.getString(R.string.best_time_private_key)+"&venue_name="+venueName+"&venue_address="+venueAddress;
        Log.e("finl", finalQueryURL);
        UIUpdate.GetUIUpdate(context).setProgressDialog("", context.getString(R.string.check_buisness_avalibility), context);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, finalQueryURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("xzxcc", response);
                netWorkRequestsCallBack.ResponseListener(response, false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    String responseBody = new String( volleyError.networkResponse.data, "utf-8" );
                    netWorkRequestsCallBack.ResponseListener(responseBody, false);
                } catch (UnsupportedEncodingException error){
                    netWorkRequestsCallBack.ResponseListener("", true);
                }
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}
