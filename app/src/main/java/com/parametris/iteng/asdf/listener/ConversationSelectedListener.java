package com.parametris.iteng.asdf.listener;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.parametris.iteng.asdf.models.Conversation;
import com.parametris.iteng.asdf.models.Server;

public class ConversationSelectedListener implements ViewPager.OnPageChangeListener {
    // TODO: 8/26/2016 complete this shit. 
    private final Context context;
    private final Server server;
    private final TextView textView;
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
