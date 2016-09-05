package com.parametris.iteng.asdf.menu;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.parametris.iteng.asdf.R;
import com.parametris.iteng.asdf.adapter.ServerAdapter;
import com.parametris.iteng.asdf.model.Server;

public class ServerPopupMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {
    private final ServerAdapter.ClickListener listener;
    private Server server;

    public ServerPopupMenu(Context context, View anchor, ServerAdapter.ClickListener listener) {
        super(context, anchor);
        this.listener = listener;

        getMenuInflater().inflate(R.menu.context_server, getMenu());
        anchor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect:
                listener.onConnectToServer(server);
                break;
            case R.id.disconnect:
                listener.onDisconnectFromServer(server);
                break;
            case R.id.edit:
                listener.onEditServer(server);
                break;
            case R.id.delete:
                listener.onDeleteServer(server);
                break;
        }
        return true;
    }

    public void updateServer(Server server) {
        this.server = server;
    }
}
