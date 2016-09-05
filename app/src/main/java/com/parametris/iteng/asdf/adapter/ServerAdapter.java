package com.parametris.iteng.asdf.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parametris.iteng.asdf.ASDF;
import com.parametris.iteng.asdf.R;
import com.parametris.iteng.asdf.menu.ServerPopupMenu;
import com.parametris.iteng.asdf.model.Server;

import java.util.List;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ViewHolder> {
    private List<Server> servers;
    private ClickListener listener;

    public interface ClickListener {
        void onServerSelected(Server server);
        void onConnectToServer(Server server);
        void onDisconnectFromServer(Server server);
        void onEditServer(Server server);
        void onDeleteServer(Server server);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView titleTextView;
        public final TextView hostTextView;
        public final ImageView connectionImageView;
        public final View menuView;
        public final ServerPopupMenu serverPopupMenu;

        public ViewHolder(View view, ClickListener listener) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.title);
            hostTextView = (TextView) view.findViewById(R.id.host);
            connectionImageView = (ImageView) view.findViewById(R.id.connection);
            menuView = view.findViewById(R.id.menu);

            serverPopupMenu = new ServerPopupMenu(view.getContext(), view.findViewById(R.id.menu), listener);
        }
    }

    public ServerAdapter(ClickListener clickListener) {
        this.listener = clickListener;
        loadServers();
    }

    private void loadServers() {
        this.servers = ASDF.getInstance().getServers();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_server, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Server server = servers.get(position);
        int colorResource = server.isDisconnected() ? R.color.disconnected : R.color.connected;
        int color = holder.itemView.getContext().getResources().getColor(colorResource);
        holder.titleTextView.setText(server.getTitle());
        holder.titleTextView.setTextColor(color);

        holder.hostTextView.setText(server.getIdentity().getNickname() + "@" + server.getHost() + ":" + server.getPort());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onServerSelected(server);
            }
        });
        holder.serverPopupMenu.updateServer(server);
    }

    @Override
    public int getItemCount() {
        return this.servers.size();
    }

}
