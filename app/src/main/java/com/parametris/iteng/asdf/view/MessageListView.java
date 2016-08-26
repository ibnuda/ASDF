package com.parametris.iteng.asdf.view;

import android.content.Context;
import android.widget.ListView;
import com.parametris.iteng.asdf.adapter.MessageListAdapter;
import com.parametris.iteng.asdf.listener.MessageClickListener;
import com.parametris.iteng.asdf.models.Message;

/**
 * Created on 26/08/2016.
 */
public class MessageListView extends ListView {
    public MessageListView(Context context) {
        super(context);

        setOnItemClickListener(MessageClickListener.getInstance());
        setDivider(null);

        float density = context.getResources().getDisplayMetrics().density;
        int padding = (int) (5 * density);
        setPadding(padding, padding, padding, padding);
        setTranscriptMode(TRANSCRIPT_MODE_NORMAL);
    }

    @Override
    public MessageListAdapter getAdapter() {
        return (MessageListAdapter) super.getAdapter();
    }
}
