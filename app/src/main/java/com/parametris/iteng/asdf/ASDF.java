package com.parametris.iteng.asdf;

import android.content.Context;
import android.util.SparseArray;

import com.parametris.iteng.asdf.model.Server;

import java.util.ArrayList;
import java.util.List;

public class ASDF {
    private static ASDF instance;
    private SparseArray<Server> servers;
    private boolean serverLoaded = false;

    private ASDF() {
        servers = new SparseArray<>();
    }

    public void loadServers(Context context) {
        if (!serverLoaded) {

        }
    }

    public static ASDF getInstance() {
        if (null == instance) {
            instance = new ASDF();
        }
        return instance;
    }

    public Server getServerById(int serverId) {
        return servers.get(serverId);
    }

    public void removeServerById(int serverId) {
        servers.remove(serverId);
    }

    public void addServer(Server server) {
        servers.put(server.getId(), server);
    }

    public void updateServer(Server server) {
        servers.put(server.getId(), server);
    }

    public List<Server> getServers() {
        List<Server> servers = new ArrayList<>();
        for (int i = 0; i < this.servers.size(); i++) {
            servers.add(this.servers.valueAt(i));
        }
        return servers;
    }
}
