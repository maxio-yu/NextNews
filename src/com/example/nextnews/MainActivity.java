package com.example.nextnews;

import java.util.ArrayList;

import com.example.nextnews.R;
import com.example.nextnews.adapter.NewsListAdapter;
import com.example.nextnews.fragment.NewsHeadlings;
import com.example.nextnews.utils.DBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	/** 屏幕宽度 */
	private int screenWidth = 0;
	/** Item宽度 */
	private int itemWidth = 0;
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

	HorizontalScrollView columnScroll = null;
	LinearLayout columnNames = null;
	private ViewPager mViewPager;
	private RadioGroup radioGroup;
	private Button refreshBtn;
	private Button showReadStatusBtn;
	private OnCheckedChangeListener emotion_buttons_listener;
	private OnClickListener refreshBtnListener;
	private OnClickListener showReadStatusListener;

	private String emotionSelected = "全部";
	private int tabPosition = 0;
	
	DBAdapter db;

	/** 新闻分类列表 */
	private ArrayList<String> channelList = new ArrayList<String>();
	/** 当前选中的栏目 */
	private int columnSelectIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		screenWidth = getWindowsWidth(this);
		itemWidth = screenWidth / 7;// 一个Item宽度为屏幕的1/7
		radioGroup = (RadioGroup) findViewById(R.id.emotion_buttons);
		emotion_buttons_listener = new EmotionBtnListener();
		radioGroup.setOnCheckedChangeListener(emotion_buttons_listener);
		refreshBtn = (Button)findViewById(R.id.refresh_button);
		refreshBtnListener = new RefreshBtnListener();
		refreshBtn.setOnClickListener(refreshBtnListener);
		showReadStatusBtn = (Button)findViewById(R.id.show_read_status);
		showReadStatusListener = new ShowReadStatusListener();
		showReadStatusBtn.setOnClickListener(showReadStatusListener);
		
		db = new DBAdapter(this);
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* 初始化 View */
	public void initView() {
		columnScroll = (HorizontalScrollView) findViewById(R.id.column_scroll);
		columnNames = (LinearLayout) findViewById(R.id.column_names);
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		setChangelView();
	}

	/**
	 * 当栏目项发生变化时候调用
	 * */
	private void setChangelView() {
		initColumnData();
		initTabColumn();
		initFragment();
	}

	/** 获取Column栏目 数据 */
	private void initColumnData() {
		channelList.add("社会");
		channelList.add("科技");
		channelList.add("体育");
		channelList.add("娱乐");
		channelList.add("其它");
	}

	/**
	 * 初始化Column栏目项
	 * */
	private void initTabColumn() {
		columnNames.removeAllViews();
		int count = channelList.size();
		for (int i = 0; i < count; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					itemWidth, LayoutParams.WRAP_CONTENT);
			params.leftMargin = 5;
			params.rightMargin = 5;
			TextView columnTextView = new TextView(this);
			// columnTextView.setTextAppearance(this,
			// R.style.top_category_scroll_view_item_text);
			columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setPadding(5, 5, 5, 5);
			columnTextView.setId(i);
			columnTextView.setText(channelList.get(i).toString());
			columnTextView.setTextSize(15);
			columnTextView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
			columnTextView.setTextColor(getResources().getColorStateList(
					R.color.top_category_scroll_text_color_day));
			if (columnSelectIndex == i) {
				columnTextView.setSelected(true);
			}
			columnTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int i = 0; i < columnNames.getChildCount(); i++) {
						View localView = columnNames.getChildAt(i);
						if (localView != v)
							localView.setSelected(false);
						else {
							localView.setSelected(true);
							tabPosition = i;
							mViewPager.setCurrentItem(i);
						}
					}
					Toast.makeText(getApplicationContext(),
							channelList.get(v.getId()).toString(),
							Toast.LENGTH_SHORT).show();
				}
			});
			columnNames.addView(columnTextView, i, params);
		}
	}

	/**
	 * 初始化Fragment
	 * */
	private void initFragment() {
		fragments.clear();// 清空
		int count = channelList.size();
		for (int i = 0; i < count; i++) {
			Bundle data = new Bundle();
			switch (channelList.get(i).toString()) {
			case "社会":
				data.putString("text", "society");
				break;
			case "科技":
				data.putString("text", "technology");
				break;
			case "体育":
				data.putString("text", "sports");
				break;
			case "娱乐":
				data.putString("text", "entertainment");
				break;
			case "其它":
				data.putString("text", "uncategory");
				break;
			}
			NewsHeadlings newsfragment = new NewsHeadlings();
			newsfragment.setArguments(data);
			fragments.add(newsfragment);
		}

		NewsListAdapter mAdapetr = new NewsListAdapter(
				getSupportFragmentManager(), fragments);

		// mViewPager.setOffscreenPageLimit(0);

		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
		tabPosition = 0;
		mViewPager.setCurrentItem(0);

	}

	/**
	 * ViewPager切换监听方法
	 * */
	public OnPageChangeListener pageListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			tabPosition = position;
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};

	/**
	 * 选择的Column里面的Tab
	 * */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < columnNames.getChildCount(); i++) {
			View checkView = columnNames.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - screenWidth / 2;
			// rg_nav_content.getParent()).smoothScrollTo(i2, 0);
			columnScroll.smoothScrollTo(i2, 0);
			// mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
			// mItemWidth , 0);
		}
		// 判断是否选中
		for (int j = 0; j < columnNames.getChildCount(); j++) {
			View checkView = columnNames.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}

	private class EmotionBtnListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup arg0, int arg1) {
			// TODO Auto-generated method stub
			// 获取变更后的选中项的ID
			int radioButtonId = arg0.getCheckedRadioButtonId();
			// 根据ID获取RadioButton的实例
			RadioButton rb = (RadioButton) findViewById(radioButtonId);
			// 更新文本内容，以符合选中项
			emotionSelected = rb.getText().toString();
			NewsHeadlings fragment = (NewsHeadlings) getSupportFragmentManager().  
		            findFragmentByTag("android:switcher:"+R.id.mViewPager+":" + tabPosition); 
			fragment.displayData();
		}

	}
	
	private class RefreshBtnListener implements OnClickListener {
	

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			NewsHeadlings fragment = (NewsHeadlings) getSupportFragmentManager().  
		            findFragmentByTag("android:switcher:"+R.id.mViewPager+":" + tabPosition); 
			fragment.getDataFromNet();
		}
		
	}
	
	private class ShowReadStatusListener implements OnClickListener {
		

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			db.open();
			Cursor cursor = db.getReadStatus();
			if (cursor.getCount() == 1) {
				int societyIndex = cursor.getColumnIndex("society");
				int sportsIndex = cursor.getColumnIndex("sports");
				int entertainmentIndex = cursor.getColumnIndex("entertainment");
				int technologyIndex = cursor.getColumnIndex("technology");
				int uncategoryIndex = cursor.getColumnIndex("uncategory");
				int societyCount = cursor.getInt(societyIndex);
				int sportsCount = cursor.getInt(sportsIndex);
				int entertainmentCount = cursor.getInt(entertainmentIndex);
				int technologyCount = cursor.getInt(technologyIndex);
				int uncategoryCount = cursor.getInt(uncategoryIndex);
				
				Intent intent = new Intent(MainActivity.this, ReadStatusPage.class);
				intent.putExtra("society", societyCount);
				intent.putExtra("sports", sportsCount);
				intent.putExtra("entertainment", entertainmentCount);
				intent.putExtra("technology", technologyCount);
				intent.putExtra("uncategory", uncategoryCount);
				startActivity(intent);				
			} else {
				Log.d("EEEEEEEEEEEE", "no data!!!!!!!");
			}
			
		}
		
	}

	public String getEmotion() {
		return emotionSelected;
	}

	public final static int getWindowsWidth(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
}
