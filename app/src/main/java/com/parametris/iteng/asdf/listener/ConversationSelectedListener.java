package com.parametris.iteng.asdf.listener;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.util.StringBuilderPrinter;
import android.widget.TextView;

import com.parametris.iteng.asdf.R;
import com.parametris.iteng.asdf.adapter.ConversationPagerAdapter;
import com.parametris.iteng.asdf.comm.IRCService;
import com.parametris.iteng.asdf.models.Conversation;
import com.parametris.iteng.asdf.models.Server;

public class ConversationSelectedListener implements ViewPager.OnPageChangeListener {
    // TODO: 8/26/2016 complete this shit. 
    private final Context context;
    private final Server server;
    private final TextView textView;
    private final ConversationPagerAdapter conversationPagerAdapter;

    public ConversationSelectedListener(
            Context context,
            Server server,
            TextView textView,
            ConversationPagerAdapter conversationPagerAdapter) {
        this.context = context;
        this.server = server;
        this.textView = textView;
        this.conversationPagerAdapter = conversationPagerAdapter;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Conversation conversation = conversationPagerAdapter.getItem(position);
        if (null != conversation && conversation.getType() == Conversation.TYPE_SERVER) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(server.getTitle() + " - " + conversation.getName());
            this.textView.setText(stringBuilder.toString());
        } else {
            this.textView.setText(server.getTitle());
        }

        if (null != conversation) {
            Conversation prevConversation = server.getConversation(server.getSelected());
            if (null != prevConversation) {
                prevConversation.setStatus(Conversation.STATUS_DEFAULT);
            }
            if (conversation.getNewMentions() > 0) {
                Intent intent = new Intent(context, IRCService.class);
                intent.setAction(IRCService.ACTION_ACK_NEW_MENTIONS);
                intent.putExtra(IRCService.EXTRA_ACK_SERVERID, server.getId());
                intent.putExtra(IRCService.EXTRA_ACK_CONVTITLE, conversation.getName());
                context.startService(intent);
            }

            conversation.setStatus(Conversation.STATUS_SELECTED);
            server.setSelected(conversation.getName());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
