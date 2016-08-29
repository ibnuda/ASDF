package com.parametris.iteng.protocol;

import android.net.SSLCertificateSocketFactory;

import com.parametris.iteng.protocol.ssl.NaiveTrustManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public abstract class IRCClient {
    public static final int RPL_LIST = 322;
    public static final int RPL_TOPIC = 332;
    public static final int RPL_TOPICINFO = 333;
    public static final int RPL_NAMREPLY = 353;
    public static final int RPL_ENDOFNAMES = 366;

    public static final int OP_ADD = 1;
    public static final int OP_REMOVE = 2;
    public static final int VOICE_ADD = 3;
    public static final int VOICE_REMOVE = 4;

    private InputThread inputThread = null;
    private OutputThread outputThread = null;
    private String charset = null;
    private InetAddress inetAddress = null;
    private Socket socket = null;

    private String server = null;
    private int port = -1;
    private String password = null;

    private final Queue outputQueue = new Queue();
    private long messageDelay = 1000;

    private String saslUsername;
    private String saslPassword;

    private Hashtable<String, Hashtable<User, User>> channels = new Hashtable<>();
    private final Hashtable<String, String> topics = new Hashtable<>();

    private int[] dccPort = null;
    private InetAddress dccInetAddress = null;

    private boolean autoNickChange = false;
    private int autoNickTries = 1;
    private boolean useSSL = false;
    private boolean registered = false;

    private String name = "Yuhu";
    private final List<String> aliases = new ArrayList<>();
    private String nick = name;
    private String login = "Yuhu";
    private final String channelPrefix = "#&+!";

    public IRCClient() {}

    public final synchronized void connect(String hostname) throws IOException, IRCException, NickAlreadyInUseException {
        this.connect(hostname, 6667, null);
    }

    private final synchronized void connect(String hostname, int port) throws IOException, IRCException, NickAlreadyInUseException {
        this.connect(hostname, port, null);
    }

    private void connect(String hostname, int port, String password) throws IOException, IRCException, NickAlreadyInUseException {
        this.registered = false;
        this.server = hostname;
        this.port = port;
        this.password = password;

        if (isConnected()) {
            throw new IOException("Sudah tersambung. Lepas dulu sambungannya.");
        }
        autoNickTries = 1;
        this.removeAllChannels();

        if (useSSL) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new X509TrustManager[]{new NaiveTrustManager()}, null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(hostname, port);
                setSNIHost(sslSocketFactory, sslSocket, hostname);
                sslSocket.startHandshake();
                this.socket = sslSocket;
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        } else {
            this.socket = new Socket(hostname, port);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(this.socket.getInputStream());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.socket.getOutputStream());

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        if (null != password && !password.equals("")) {
            OutputThread.sendRawLine(this, bufferedWriter, "PASS " + password);
        }
        String nick = this.getName();

        if (null != this.saslUsername) {
            OutputThread.sendRawLine(this, bufferedWriter, "CAP LS");
            OutputThread.sendRawLine(this, bufferedWriter, "CAP REQ :sasl");
            OutputThread.sendRawLine(this, bufferedWriter, "CAP END");
        }
    }

    private void setSNIHost(SSLSocketFactory sslSocketFactory, SSLSocket sslSocket, String hostname) {
        if (sslSocketFactory instanceof android.net.SSLCertificateSocketFactory) {
            ((SSLCertificateSocketFactory) sslSocketFactory).setHostname(sslSocket, hostname);
        } else {
            try {
                sslSocket.getClass().getMethod("setHostname", String.class).invoke(sslSocket, hostname);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {

            }
        }
    }

    private final void removeAllChannels() {
        synchronized (channels) {
            channels = new Hashtable<>();
        }
    }

    private boolean isConnected() {
        return null != this.inputThread && this.inputThread.isConnected();
    }

    public long getMessageDelay() {
        return 0;
    }

    public void sendRawLine(String line) {

    }

    public int getMaxLineLength() {
        return 0;
    }

    public void handleLine(String line) throws NickAlreadyInUseException, IOException {
        if (line.startsWith("PING ")) {
            this.onServerPing(line.substring(5));
            return;
        }

        String sourceNick = "";
        String sourceLogin = "";
        String sourceHostname = "";

        StringTokenizer stringTokenizer = new StringTokenizer(line);
        String senderInfo = stringTokenizer.nextToken();
        String command = stringTokenizer.nextToken();
        String target = null;

        int exclamation = senderInfo.indexOf('!');
        int at = senderInfo.indexOf('@');

        if (senderInfo.startsWith(":")) {
            if (exclamation > 0 && at > 0 && exclamation < at) {
                sourceNick = senderInfo.substring(1, exclamation);
                sourceLogin = senderInfo.substring(exclamation + 1, at);
                sourceHostname = senderInfo.substring(at + 1);
            } else {
                if (stringTokenizer.hasMoreTokens()) {
                    String token = command;
                    int code = -1;
                    try {
                        code = Integer.parseInt(token);
                    } catch (Exception e) {

                    }
                    if (code != -1) {
                        String error = token;
                        String response = line.substring(line.indexOf(error, senderInfo.length()) + 4, line.length());

                        this.processServerResponse(code, response);
                    }
                }
            }
        }
    }

    private final void processServerResponse(int code, String response) {
        switch (code) {
            case RPL_LIST:
                int first = response.indexOf(' ');
                int second = response.indexOf(' ', first + 1);
                int third = response.indexOf(' ', second + 1);
                int colon = response.indexOf(':');
                String channel = response.substring(first + 1, second);
                int userCount = 0;
                try {
                    userCount = Integer.parseInt(response.substring(second + 1, third));
                } catch (Exception e) {
                }
                String topic = response.substring(colon + 1);
                this.onChannelInfo(channel, userCount, topic);
                break;
            case RPL_TOPIC:
                break;
            case RPL_TOPICINFO:
                break;
            case RPL_NAMREPLY:
                break;
            case RPL_ENDOFNAMES:
                break;
        }
        this.onServerResponse(code, response);
    }

    private void onChannelInfo(String channel, int userCount, String topic) {
        
    }

    private void onServerResponse(int code, String response) {

    }

    private void onServerPing(String response) {
        this.sendRawLine("PONG " + response);
    }

    public void onDisconnected() {
        registered = false;
    }

    public String getName() {
        return name;
    }
}
