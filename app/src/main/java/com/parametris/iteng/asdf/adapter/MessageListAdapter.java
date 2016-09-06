package com.parametris.iteng.asdf.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parametris.iteng.asdf.model.Conversation;
import com.parametris.iteng.asdf.model.Message;

import java.util.LinkedList;

public class MessageListAdapter extends BaseAdapter {

    private final LinkedList<Message> messages;
    private final Context context;
    private int historySize;

    public MessageListAdapter(Conversation conversation, Context context) {
        LinkedList<Message> messages = new LinkedList<>();
        if (conversation.getType() != Conversation.TYPE_SERVER) {
            Message header = new Message(conversation.getName());
            header.setColor(Message.COLOR_BLUE);
            messages.add(header);
        }

        messages.addAll(conversation.getHistory());
        conversation.cleanBuffer();

        this.messages = messages;
        this.context = context;
        historySize = conversation.getHistorySize();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).renderTextView(context, (TextView) convertView);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (null == observer) {
            return;
        }
        super.unregisterDataSetObserver(observer);
    }

    public void addMessage(Message message) {
        messages.add(message);
        if (messages.size() > historySize) {
            messages.remove(0);
        }
        notifyDataSetChanged();
    }

    public void addBulkMessage(LinkedList<Message> messages) {
        this.messages.addAll(messages);
        while (messages.size() > historySize) {
            messages.remove(0);
        }
        notifyDataSetChanged();
    }
}
