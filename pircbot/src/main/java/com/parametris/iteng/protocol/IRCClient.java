package com.parametris.iteng.protocol;

import android.net.SSLCertificateSocketFactory;
import android.util.Base64;

import com.parametris.iteng.protocol.ssl.NaiveTrustManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
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

    private String finger = "Hmmm";

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
            OutputThread.sendRawLine(this, bufferedWriter, "AUTHENTICATE PLAIN");

            String authString = saslUsername + '\0' + saslUsername + '\0' + saslPassword;
            String authStringEncoded = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

            while (authStringEncoded.length() >= 400) {
                String toSend = authStringEncoded.substring(0, 400);
                authString = authStringEncoded.substring(400);

                OutputThread.sendRawLine(this, bufferedWriter, "AUTHENTICATE " + toSend);
            }

            if (authStringEncoded.length() > 0) {
                OutputThread.sendRawLine(this, bufferedWriter, "AUTHENTICATE " + authStringEncoded);
            } else {
                OutputThread.sendRawLine(this, bufferedWriter, "AUTHENTICATE +");
            }
        }
        OutputThread.sendRawLine(this, bufferedWriter, "NICK " + nick);
        OutputThread.sendRawLine(this, bufferedWriter, "USER " + this.getLogin() + " 8 * : ");

        this.inputThread = new InputThread(this, this.socket, bufferedReader, bufferedWriter);

        this.setNick(nick);

        String line = null;
        line = bufferedReader.readLine();

        if (null == line) {
            throw new IOException("WUT WUT WUT");
        }

        this.handleLine(line);
        this.socket.setSoTimeout(5 * 60 * 1000);

        this.inputThread.start();
        if (this.outputThread == null) {
            this.outputThread = new OutputThread(this, this.outputQueue);
            this.outputThread.start();
        }

        this.onConnect();
    }

    private void onConnect() {

    }

    private String getLogin() {
        return this.login;
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

    public final synchronized void sendRawLine(String line) {
        if (isConnected()) {
            this.inputThread.sendRawLine(line);
        }
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

                        if (code == 443 && !this.registered) {
                            if (autoNickChange) {
                                String oldNick = this.nick;
                                List<String> aliases = getAliases();
                                if (this.autoNickTries - 1 <= aliases.size()) {
                                    this.nick = aliases.get(this.autoNickTries - 2);
                                } else {
                                    this.nick = getName() + (this.autoNickTries - aliases.size());
                                }

                                this.onNickChange(oldNick, getLogin(), "", this.nick);
                                this.sendRawLineViaQueue("NICK " + this.nick);
                            } else {
                                this.socket.close();
                                this.inputThread = null;
                                throw new NickAlreadyInUseException(line);
                            }
                        }
                        return;
                    } else {
                        sourceNick = senderInfo;
                        target = token;
                        if (sourceNick.contains("!") && !sourceNick.contains("@")) {
                            String[] chunks = sourceNick.split("!");
                            sourceNick = chunks[0];
                        }
                        if (command.equalsIgnoreCase("nick")) {
                            target = stringTokenizer.nextToken();
                        }
                    }
                } else {
                    this.onUnknown(line);
                    return;
                }
            }
        }
        command = command.toUpperCase();
        if (sourceNick.startsWith(":")) {
            sourceNick = sourceNick.substring(1);
        }
        if (null == target) {
            target = stringTokenizer.nextToken();
        }
        if (target.startsWith(":")) {
            target.substring(1);
        }
        if (command.equals("PRIVMSG") && line.indexOf(":\u0001") > 0 && line.endsWith("\u0001")) {
            String request = line.substring(line.indexOf(":\u0001") + 2, line.length() - 1);
            if (request.equals("VERSION")) {

            } else if (request.startsWith("ACTION ")) {
                this.onAction(sourceNick, sourceLogin, sourceHostname, target, request.substring(7));
            } else if (request.startsWith("PING ")) {
                this.onPing(sourceNick, sourceLogin, sourceHostname, target, request.substring(5));
            } else if (request.equals("TIME")) {
                this.onTime(sourceNick, sourceLogin, sourceHostname, target);
            } else if (request.equals("FINGER")) {
                this.onFinger(sourceNick, sourceLogin, sourceHostname, target);
            } else {
                this.onUnknown(line);
            }
        } else if (command.equals("PRIVMSG") && this.channelPrefix.indexOf(target.charAt(0)) >= 0) {
            this.onMessage(target, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
        } else if (command.equals("PRIVMSG")) {
            this.onPrivateMessage(sourceNick, sourceLogin, sourceHostname, target, line.substring(line.indexOf(" :") + 2));
        } else if (command.equals("JOIN")) {
            String channel = target;
            this.addUser(channel, new User("", sourceNick));
            this.onJoin(channel, sourceNick, sourceLogin, sourceHostname);
        } else if (command.equals("PART")) {
            this.removeUser(target, sourceNick);
            if (sourceNick.equals(this.getName())) {
                this.removeChannel(target);
            }
            this.onPart(target, sourceNick, sourceLogin, sourceHostname);
        } else if (command.equals("NICK")) {
            String newNick = target;
            this.renameUser(sourceNick, newNick);
            if (sourceNick.equals(this.getNick())) {
                this.setNick(newNick);
            }
            this.onNickChange(sourceNick, sourceLogin, sourceHostname, newNick);
        } else if (command.equals("NOTICE")) {
            this.onNotice(sourceNick, sourceLogin, sourceHostname, target, line.substring(line.indexOf(" :") + 2));
        } else if (command.equals("QUIT")) {
            this.onQuit(sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
            if (sourceNick.equals(this.getNick())) {
                this.removeAllChannels();
            } else {
                this.removeUser(sourceNick);
            }
        }
    }

    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String substring) {
    }

    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String substring) {
    }

    protected final void renameUser(String sourceNick, String newNick) {
        synchronized (this.channels) {
            Enumeration<String> enumeration = this.channels.keys();
            while (enumeration.hasMoreElements()) {
                String channel = enumeration.nextElement();
                User user = this.removeUser(channel, sourceNick);
                if (null != user) {
                    user = new User(user.getPrefix(), newNick);
                    this.addUser(channel, user);
                }
            }
        }
    }

    protected void onPart(String target, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    private final void removeChannel(String channel) {
        channel = channel.toLowerCase();
        synchronized (this.channels) {
            this.channels.remove(channel);
        }
    }

    private final User removeUser(String target, String sourceNick) {
        target = target.toLowerCase();
        User user = new User("", sourceNick);
        synchronized (this.channels) {
            Hashtable<User, User> userUserHashtable = this.channels.get(target);
            if (null != userUserHashtable) {
                return userUserHashtable.remove(user);
            }
        }
        return null;
    }

    private final void removeUser(String sourceNick) {
        synchronized (this.channels) {
            Enumeration<String> enumeration = this.channels.keys();
            while (enumeration.hasMoreElements()) {
                String channel = enumeration.nextElement();
                this.removeUser(channel, sourceNick);
            }
        }
    }

    protected void onJoin(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    private void addUser(String channel, User user) {
        channel = channel.toLowerCase();
        synchronized (this.channels) {
            Hashtable<User, User> userUserHashtable = this.channels.get(channel);
            if (userUserHashtable == null) {
                userUserHashtable = new Hashtable<>();
                this.channels.put(channel, userUserHashtable);
            }
            userUserHashtable.put(user, user);
        }
    }

    protected void onPrivateMessage(String sourceNick, String sourceLogin, String sourceHostname, String target, String substring) {
    }

    protected void onMessage(String target, String sourceNick, String sourceLogin, String sourceHostname, String substring) {
        
    }

    protected void onFinger(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        this.sendRawLine("NOTICE " + sourceNick + " :\u0001FINGER " + this.finger + "\u0001");
    }

    protected void onTime(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        this.sendRawLine("NOTICE " + sourceNick + " :\u0001TIME " + new Date().toString() + "\u0001");
    }

    protected void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String substring) {
        this.sendRawLine("NOTICE " + sourceNick + " :\u0001PING " + substring + "\u0001");
    }

    protected void onAction(String sourceNick, String sourceLogin, String sourceHostname, String target, String substring) {
    }

    private void onUnknown(String line) {

    }

    private final synchronized void sendRawLineViaQueue(String line) {
        if (null == line) {
            throw new NullPointerException("Gagal kirim ke server.");
        }
        if (isConnected()) {
            this.outputQueue.add(line);
        }
    }

    private void onNickChange(String oldNick, String login, String s, String nick) {

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

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getSaslUsername() {
        return this.saslUsername;
    }

    public void setSaslUsername(String saslUsername) {
        this.saslUsername = saslUsername;
    }

    public String getSaslPassword() {
        return this.saslPassword;
    }

    public String getNick() {
        return this.nick;
    }

    public void setSaslPassword(String saslPassword) {
        this.saslPassword = saslPassword;
    }

    public void setSaslCredentials(String saslUsername, String saslPassword) {
        setSaslPassword(saslPassword);
        setSaslUsername(saslUsername);
    }

    public void setAutoNickChange(boolean autoNickChange) {
        this.autoNickChange = autoNickChange;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public final void joinChannel(String channel) {
        this.sendRawLine("JOIN " + channel);
    }

    public final void joinChannel(String channel, String key) {
        this.joinChannel(channel + ' ' + key);
    }

    public final void partChannel(String channel) {
        this.sendRawLine("PART " + channel);
    }

    public void quitServer() {
        this.quitServer("");
    }

    public void quitServer(String reason) {
        this.sendRawLine("QUIT :" + reason);
    }
}
