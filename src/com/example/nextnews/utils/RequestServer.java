package com.example.nextnews.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class RequestServer implements Runnable {
	String httpUrl = null;
	String method = null;
	String owner = null;
	Map<String, String> params = null;

	private static Context context;// what is context used for ??? whatever
									// context is OK? S Dong!

	public RequestServer(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	public void setOptions(String owner, Map<String, String> params,
			String method) {
		this.owner = owner;
		this.params = params;
		this.method = method;
	}

	/*
	 * TODO make sure all connect with POST method exchange data with JSON object.
	 */
	private void interaction(String owner, Map<String, String> params,
			String method) {

		HttpClient client = new DefaultHttpClient(httpConfig());
		HttpResponse response = null;
		int httpStatus;

		if (method == "GET") {
			if (params != null) {
				Iterator<Entry<String, String>> iter = params.entrySet()
						.iterator();
				if (iter.hasNext()) {
					httpUrl = httpUrl + "?";
				}
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
							.next();
					httpUrl = httpUrl + entry.getKey();
					httpUrl = httpUrl + "=" + entry.getValue() +"&";
				}
			}
			HttpGet httpRequest = new HttpGet(httpUrl);
			try {
				response = client.execute(httpRequest);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("TTTTTTTT","exception network");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("TTTTTTTT","TimeOut ?");
				Toast.makeText(context, "网络似乎不是很畅通(>_<)", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} 
		} else if (method == "POST") {

			Log.d("TAT", "Post started");
			HttpPost httpRequest = new HttpPost(httpUrl);
			httpRequest.addHeader(HTTP.CONTENT_TYPE, "application/json");
			JSONObject data = new JSONObject();
			StringEntity se;

			try {
				if (params == null) {
					Log.d("TAT", "params should not be null when POST");
				} else {
					Iterator<Entry<String, String>> iter = params.entrySet()
							.iterator();
					while (iter.hasNext()) {
						Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
								.next();
						if(entry.getKey().equals("JSONOBJ")) {
							JSONObject tmp = new JSONObject(entry.getValue());
							Iterator<?> it = tmp.keys();
				            while(it.hasNext()){
				                String key = (String) it.next().toString();  
				                JSONArray value = tmp.getJSONArray(key);
				                data.put(key, value);				                  
				            }
						} else {
							data.put(entry.getKey(), entry.getValue());
						}
						Log.d("TAT", "Json is like " + data.toString());
					}
					se = new StringEntity(data.toString());
					httpRequest.setEntity(se);
				}
				Log.d("TAT",
						"about to excute http request, is this in a new thread?");
				response = client.execute(httpRequest);
				Log.d("TAT", "http execute finished");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		httpStatus = response.getStatusLine().getStatusCode();

		if (httpStatus == HttpStatus.SC_OK) {
			String returnedMessage = null;
			try {
				returnedMessage = EntityUtils.toString(response.getEntity());

				// TODO we need to check message here, maybe email or passwd is
				// incorrect
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Intent intent = new Intent(owner);
			Log.d("TAT", "we put below msg into intent: " + returnedMessage);
			intent.putExtra("returned_msg", returnedMessage);
			Log.d("TAT", "about to send local broadcast message");
			LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			Log.d("TAT", "local broadcast message sent out");
		} else {
			Log.w("TZ_http", "error at connect to server, status is "
					+ httpStatus);
			Toast.makeText(context, "������������������������(>_<)", Toast.LENGTH_SHORT).show();
		}
	}

	private HttpParams httpConfig() {
		HttpParams mhParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(mhParams, 6 * 1000);
		HttpConnectionParams.setSoTimeout(mhParams, 10 * 1000);
		HttpConnectionParams.setSocketBufferSize(mhParams, 2 * 8192);
		return mhParams;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Log.d("TAT", "thread started!!");
		interaction(owner, params, method);
	}

}
