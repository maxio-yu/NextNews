package com.example.nextnews.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.nextnews.MainActivity;
import com.example.nextnews.R;
import com.example.nextnews.WebBrowser;
import com.example.nextnews.adapter.LvAdapter;
import com.example.nextnews.utils.DBAdapter;
import com.example.nextnews.utils.RequestServer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NewsHeadlings extends Fragment {

	String category;
	ListView newsList;
	DBAdapter db;
	SimpleAdapter newsListAdapter;
	List<Map<String, String>> list;
	boolean dbReady = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle args = getArguments();
		category = args != null ? args.getString("text") : "";
		db = new DBAdapter(this.getActivity());
		list = new ArrayList<Map<String, String>>();
		
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(
				message, new IntentFilter(category));
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_fragment, container, false);
		newsList = (ListView) view.findViewById(R.id.news_list);
		newsListAdapter = new SimpleAdapter(this.getActivity(), list,
				R.layout.news_items, new String[] { "title", "pub_date", "emotion_level" },
				new int[] { R.id.news_title, R.id.news_pub_date, R.id.news_emotion_level });
		if (newsList != null) {
			newsList.setAdapter(newsListAdapter);
			newsList.setOnItemClickListener(new ClickOnNews());
		}
		getData();
		 
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		displayData();
	}
	
	private void getData() {
		if (existDatebase() <= 0) {
			getDataFromNet();
		}
	}
	
	private int existDatebase() {
		db.open();
		Cursor newsItemsCursor = db.getNewsByCategory(category, "全部");
		db.close();
		Log.d("QQQQQQQQQ", ""+ newsItemsCursor.getCount());
		return newsItemsCursor.getCount();		
	}

	public void getDataFromNet() {
		dbReady = false;
		list.removeAll(list);
		newsListAdapter.notifyDataSetChanged();
		// TODO Auto-generated method stub
		if (isOpenNetwork()) {
			String httpUrl = "http://api.minghe.me/news";
			RequestServer rs = new RequestServer(httpUrl);
			HashMap<String, String> params = new HashMap<String, String>();

			params.put("number", "15");
			params.put("category", category);

			rs.setOptions(category, params, "GET");

			Thread thread = new Thread(rs);
			thread.start();
		} else {
			Toast.makeText(this.getActivity().getApplicationContext(),
					"请打开网络连接!", Toast.LENGTH_SHORT).show();
		}
	}

	public void displayData() {
		
		list.removeAll(list);

			db.open();
			Cursor newsItemsCursor = db.getNewsByCategory(category, ((MainActivity)(this.getActivity())).getEmotion());
			Log.d("RRRRRRRRRRRR",((MainActivity)(this.getActivity())).getEmotion() );
			if (newsItemsCursor.getCount() == 0) {
				return;
			}

			do {
				Map<String, String> listItem = new HashMap<String, String>();
				int titleIndex = newsItemsCursor.getColumnIndex("title");
				int dateIndex = newsItemsCursor.getColumnIndex("pub_date");
				int emotionIndex = newsItemsCursor.getColumnIndex("emotion_level");
				int linkIndex = newsItemsCursor.getColumnIndex("guid");
				listItem.put("title", newsItemsCursor.getString(titleIndex));
				listItem.put("pub_date", newsItemsCursor.getString(dateIndex));
				listItem.put("emotion_level", newsItemsCursor.getString(emotionIndex));
				listItem.put("link", newsItemsCursor.getString(linkIndex));
				list.add(listItem);
			} while (newsItemsCursor.moveToNext());
			db.close();

			newsListAdapter.notifyDataSetChanged();

			

	}

	/**
	 * 对网络连接状态进行判断
	 * 
	 * @return true, 可用； false， 不可用
	 */
	private boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) this
				.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}

	// TODO: we need to abstract this method so we don't need re-write this ugly
	// code
	private BroadcastReceiver message = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String returnedMsg = intent.getStringExtra("returned_msg");
			if (returnedMsg == null) {
				Toast.makeText(
						NewsHeadlings.this.getActivity()
								.getApplicationContext(), "网络不太好哦，请重试!",
						Toast.LENGTH_SHORT).show();
			} else {
				JSONArray jsonArray;

				try {
					jsonArray = new JSONArray(returnedMsg);
					db.open();
					db.deleteNewsByCategory(category);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
						int id = jsonObject.getInt("id");
						String title = jsonObject.getString("title");
						String description = jsonObject
								.getString("description");
						String guid = jsonObject.getString("guid");
						String link = jsonObject.getString("link");
						String category = jsonObject.getString("category");
						int emotion_level = jsonObject.getInt("emotion_level");
						String created_at = jsonObject.getString("created_at");
						String updated_at = jsonObject.getString("updated_at");
						String picture = jsonObject.getString("picture");
						String pub_date = jsonObject.getString("pub_date");
						db.insertNews(id, title, description, guid, link,
								category, emotion_level, created_at,
								updated_at, picture, pub_date);
					}
					db.close();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dbReady = true;
				displayData();
			}
		}
	};

	private class ClickOnNews implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Map<String, Object> map = (Map<String, Object>) NewsHeadlings.this.newsListAdapter
					.getItem(position);
			String link = map.get("link").toString();
			Intent intent = new Intent(NewsHeadlings.this.getActivity(), WebBrowser.class);
			//TODO we need to handle null message or it may caused aborted
			intent.putExtra("link", link);
			db.open();
			db.updateReadStatus(category);
			db.close();
			startActivity(intent);	
		}
	}
}
