package com.parametris.iteng.asdf.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public abstract class ViewPagerAdapter extends PagerAdapter {
    public abstract View getView(int position, ViewPager viewPager);
}
