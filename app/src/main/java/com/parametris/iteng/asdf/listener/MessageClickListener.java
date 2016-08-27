package com.parametris.iteng.asdf.listener;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.parametris.iteng.asdf.adapter.MessageListAdapter;
import com.parametris.iteng.asdf.fragment.ChatFragment;

public class MessageClickListener implements AdapterView.OnItemClickListener {
    private static MessageClickListener instance;

    private MessageClickListener() {

    }

    public static synchronized MessageClickListener getInstance() {
        if (null == instance) {
            instance = new MessageClickListener();
        }
        return instance;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageListAdapter adapter = (MessageListAdapter) parent.getAdapter();
        // TODO: 8/26/2016 consider using fragment thingy.
        Intent intent = new Intent(parent.getContext(), ChatFragment.class);
        parent.getContext().startActivity(intent);
    }
}
