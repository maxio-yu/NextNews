package com.example.nextnews.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class NewsListAdapter extends FragmentPagerAdapter {
	
	private ArrayList<Fragment> fragments;

	public NewsListAdapter(FragmentManager fm) {
		super(fm);
	}

	public NewsListAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int index) {
		return fragments.get(index);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
