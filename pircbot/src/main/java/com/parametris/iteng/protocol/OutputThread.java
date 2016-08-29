package com.parametris.iteng.protocol;

import java.io.BufferedWriter;
import java.io.IOException;

public class OutputThread extends Thread {
    private IRCClient ircClient = null;
    private Queue queue = null;

    public OutputThread(IRCClient ircClient, Queue queue) {
        this.ircClient = ircClient;
        this.queue = queue;
        this.setName(this.getClass() + "-Thread");
    }

    public void run() {
        try {
            boolean running = true;
            while (running) {
                Thread.sleep(ircClient.getMessageDelay());
                String line = (String) queue.next();
                if (null != line) {
                    ircClient.sendRawLine(line);
                } else {
                    running = false;
                }
            }
        } catch (InterruptedException e) {
        }
    }

    static void sendRawLine(IRCClient ircClient, BufferedWriter bufferedWriter, String line) {
        if (line.length() > ircClient.getMaxLineLength() - 2) {
            line = line.substring(0, ircClient.getMaxLineLength() - 2);
        }
        synchronized (bufferedWriter) {
            try {
                bufferedWriter.write(line + "\r\n");
                bufferedWriter.flush();
            } catch (IOException e) {
            }
        }
    }
}
