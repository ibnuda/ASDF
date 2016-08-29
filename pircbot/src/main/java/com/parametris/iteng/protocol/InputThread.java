package com.parametris.iteng.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

public class InputThread extends Thread {
    private IRCClient ircClient = null;
    private Socket socket = null;
    private BufferedWriter bufferedWriter = null;
    private BufferedReader bufferedReader = null;
    private boolean connected = true;
    private boolean disposed = false;

    public static final int MAX_LENGTH = 512;

    public InputThread(IRCClient ircClient, Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        this.ircClient = ircClient;
        this.socket = socket;
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = bufferedWriter;
        this.setName(this.getClass() + "-Thread");
    }

    void sendRawLine(String line) {
        OutputThread.sendRawLine(this.ircClient, this.bufferedWriter, line);
    }

    public boolean isConnected() {
        return connected;
    }

    public void run() {
        try {
            boolean running = true;
            while (running) {
                try {
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null) {
                        try {
                            ircClient.handleLine(line);
                        } catch (Throwable e) {
                            StringWriter stringWriter = new StringWriter();
                            PrintWriter printWriter = new PrintWriter(stringWriter);
                            e.printStackTrace(printWriter);
                            printWriter.flush();
                        }
                        if (line == null) {
                            running = false;
                        }
                    }
                } catch (InterruptedIOException e) {
                    this.sendRawLine("PING " + (System.currentTimeMillis() / 1000));
                }
            }
        } catch (Exception e) {

        }

        try {
            socket.close();
        } catch (IOException e) {
        }
        if (!disposed) {
            connected = false;
            ircClient.onDisconnected();
        }
    }

    public void dispose() {
        try {
            this.disposed = true;
            this.socket.close();
        } catch (IOException e) {
        }
    }
}
