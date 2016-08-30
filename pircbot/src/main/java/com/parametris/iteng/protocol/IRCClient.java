package com.parametris.iteng.protocol;

import android.net.SSLCertificateSocketFactory;
import android.support.annotation.Nullable;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

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
    
    private static final String version = "notice me senpai.";
    private static final String finger = "finger...";

    public IRCClient() {}

    public String getVersion() {
        return version;
    }

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

        OutputThread.sendRawLine(this, bufferedWriter, "NICK " + nick);
        OutputThread.sendRawLine(this, bufferedWriter, "USER " + this.getLogin() + " 8 * :" + this.getVersion());

        this.inputThread = new InputThread(this, this.socket, bufferedReader, bufferedWriter);
        this.setNick(nick);

        String line = null;
        line = bufferedReader.readLine();

        if (null == line) {
            throw new IOException("Tidak dapat menyambung ke server.");
        }

        this.handleLine(line);
        this.socket.setSoTimeout(5 * 60 * 1000);
        this.inputThread.start();

        if (null == this.outputThread) {
            this.outputThread = new OutputThread(this, this.outputQueue);
            this.outputThread.start();
        }

        this.onConnect();
    }

    protected void onConnect() {
    }

    protected void onRegister() {
        this.registered = true;
    }

    protected void onDisconnect() {
        this.registered = false;
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

                        if (443 == code && !this.registered) {
                            if (this.autoNickChange) {
                                String oldNick = this.nick;
                                List<String> aliases = getAliases();
                                this.autoNickTries++;
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
            target = target.substring(1);
        }

        if (command.equals("PRIVMSG") && line.indexOf(":\u0001") > 0 && line.endsWith("\u0001")) {
            String request = line.substring(line.indexOf(":\u0001") + 2, line.length() - 1);
            if (request.equals("VERSION")) {
                this.onVersion(sourceNick, sourceLogin, sourceHostname, target);
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
            if (sourceNick.equals(this.getNick())) {
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
        } else if (command.equals("KICK")) {
            String recipient = stringTokenizer.nextToken();
            if (recipient.equals(this.getNick())) {
                this.removeChannel(target);
            }
            this.removeUser(target, recipient);
            this.onKick(target, sourceNick, sourceLogin, sourceHostname, recipient, line.substring(line.indexOf(" :") + 2));
        } else if (command.equals("MODE")) {
            String mode = line.substring(line.indexOf(target, 2) + target.length() + 1);
            if (mode.startsWith(":")) {
                mode = mode.substring(1);
            }
            this.processMode(target, sourceNick, sourceLogin, sourceHostname, mode);
        } else if (command.equals("TOPIC")) {
            this.onTopic(target, line.substring(line.indexOf(" :") + 2), sourceNick, System.currentTimeMillis(), true);
        } else if (command.equals("INVITE")) {
            this.onInvite(target, sourceNick, sourceLogin, sourceHostname, line.substring(line.indexOf(" :") + 2));
        } else {
            this.onUnknown(line);
        }
    }

    protected void onInvite(String target, String sourceNick, String sourceLogin, String sourceHostname, String substring) {
        
    }

    protected void onTopic(String target, String substring, String sourceNick, long l, boolean b) {
    }

    private void processMode(String target, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
        if (this.channelPrefix.indexOf(target.charAt(0)) >= 0) {
            String channel = target;
            StringTokenizer stringTokenizer = new StringTokenizer(mode);
            String[] params = new String[stringTokenizer.countTokens()];

            int t = 0;
            while (stringTokenizer.hasMoreTokens()) {
                params[t] = stringTokenizer.nextToken();
                t++;
            }

            char pn = ' ';
            int p = 1;

            for (int i = 0; i < params[0].length(); i++) {
                char atPos = params[0].charAt(i);

                if (atPos == '+' || atPos == '-') {
                    pn = atPos;
                } else if (atPos == 'o') {
                    if (pn == '+') {
                        this.updateUser(channel, OP_ADD, params[p]);
                        onOp(channel, sourceNick, sourceLogin, sourceHostname, params[p]);
                    } else {
                        this.updateUser(channel, OP_REMOVE, params[p]);
                        onDeop(channel, sourceNick, sourceLogin, sourceHostname, params[p]);
                    }
                    p++;
                } else if (atPos == 'v') {
                    if (pn == '+') {
                        this.updateUser(channel, VOICE_ADD, params[p]);
                        onVoice(channel, sourceNick, sourceLogin, sourceHostname, params[p]);
                    } else {
                        this.updateUser(channel, VOICE_REMOVE, params[p]);
                        onDeVoice(channel, sourceNick, sourceLogin, sourceHostname, params[p]);
                    }
                    p++;
                } else if (atPos == 'k') {
                    if (pn == '+') {
                        onSetChannelKey(channel, sourceNick, sourceLogin, sourceHostname, params[p]);
                    } else {
                        onRemoveChannelKey(channel, sourceNick, sourceLogin, sourceHostname, params[p]);
                    }
                    p++;
                } else if (atPos == 'l') {
                    if (pn == '+') {
                        onSetChannelLimit(channel, sourceNick, sourceLogin, sourceHostname, Integer.parseInt(params[p]));
                        p++;
                    } else {
                        onRemoveChannelLimit(channel, sourceNick, sourceLogin, sourceHostname);
                    }
                } else if (atPos == 'b') {
                    if (pn == '+') {
                        onSetChannelBan(channel, sourceNick, sourceLogin, sourceHostname,params[p]);
                    } else {
                        onRemoveChannelBan(channel, sourceNick, sourceLogin, sourceHostname, params[p]);
                    }
                    p++;
                } else if (atPos == 't') {
                    if (pn == '+') {
                        onSetTopicProtection(channel, sourceNick, sourceLogin, sourceHostname);
                    } else {
                        onRemoveTopicProtection(channel, sourceNick, sourceLogin, sourceHostname);
                    }
                } else if (atPos == 'n') {
                    if (pn == '+') {
                        onSetNoExternalMessages(channel, sourceNick, sourceLogin, sourceHostname);
                    } else {
                        onRemoveNoExternalMessages(channel, sourceNick, sourceLogin, sourceHostname);
                    }
                } else if (atPos == 'i') {
                    if (pn == '+') {
                        onSetInviteOnly(channel, sourceNick, sourceLogin, sourceHostname);
                    } else {
                        onRemoveInviteOnly(channel, sourceNick, sourceLogin, sourceHostname);
                    }
                } else if (atPos == 'm') {
                    if (pn == '+') {
                        onSetModerated(channel, sourceNick, sourceLogin, sourceHostname);
                    } else {
                        onRemoveModerated(channel, sourceNick, sourceLogin, sourceHostname);
                    }
                } else if (atPos == 'p') {
                    if (pn == '+') {
                        onSetPrivate(channel, sourceNick, sourceLogin, sourceHostname);
                    } else {
                        onRemovePrivate(channel, sourceNick, sourceLogin, sourceHostname);
                    }
                } else if (atPos == 's') {
                    if (pn == '+') {
                        onSetSecret(channel, sourceNick, sourceLogin, sourceHostname);
                    } else {
                        onRemoveSecret(channel, sourceNick, sourceLogin, sourceHostname);
                    }
                }
            }

            this.onMode(channel, sourceNick, sourceLogin, sourceHostname, mode);
        } else {
            String nick = target;
            this.onUserMode(nick, sourceNick, sourceLogin, sourceHostname, mode);
        }
    }

    protected void onUserMode(String nick, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
    }

    protected void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode) {
    }

    protected void onRemoveSecret(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onSetSecret(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onRemovePrivate(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onSetPrivate(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onRemoveModerated(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onSetModerated(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onRemoveInviteOnly(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onSetInviteOnly(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onRemoveNoExternalMessages(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onSetNoExternalMessages(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onRemoveTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onSetTopicProtection(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onRemoveChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
    }

    protected void onSetChannelBan(String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
    }

    protected void onRemoveChannelLimit(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    protected void onSetChannelLimit(String channel, String sourceNick, String sourceLogin, String sourceHostname, int i) {
    }

    protected void onRemoveChannelKey(String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
    }

    protected void onSetChannelKey(String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
    }

    protected void onDeVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
    }

    protected void onVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
    }

    protected void onDeop(String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
    }

    protected void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String param) {
    }

    private void updateUser(String channel, int opAdd, String param) {
        channel = channel.toLowerCase();
        synchronized (this.channels) {
            Hashtable<User, User> userUserHashtable = this.channels.get(channel);
            User user = null;
            if (null != userUserHashtable) {
                Enumeration<User> enumeration = userUserHashtable.elements();
                while (enumeration.hasMoreElements()) {
                    User userObj = enumeration.nextElement();
                    if (userObj.getNick().equalsIgnoreCase(param)) {
                        switch (opAdd) {
                            case OP_ADD:
                                if (userObj.hasVoice()) {
                                    user = new User("@+", param);
                                } else {
                                    user = new User("@", param);
                                }
                                break;
                            case OP_REMOVE:
                                if (userObj.hasVoice()) {
                                    user = new User("+", param);
                                } else {
                                    user = new User("", param);
                                }
                                break;
                            case VOICE_ADD:
                                if (userObj.isOp()) {
                                    user = new User("@+", param);
                                } else {
                                    user = new User("+", param);
                                }
                                break;
                            case VOICE_REMOVE:
                                if (userObj.isOp()) {
                                    user = new User("@", param);
                                } else {
                                    user = new User("", param);
                                }
                                break;
                        }
                    }
                }
            }
            if (null != null) {
                userUserHashtable.put(user, user);
            } else {
                user = new User("", param);
                userUserHashtable.put(user, user);
            }
        }
    }

    protected void onKick(String target, String sourceNick, String sourceLogin, String sourceHostname, String recipient, String substring) {
    }

    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String substring) {
    }

    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String substring) {
    }

    private final void renameUser(String sourceNick, String newNick) {
        synchronized (this.channels) {
            Enumeration<String> enumeration = this.channels.keys();
            while (enumeration.hasMoreElements()) {
                String channel = enumeration.nextElement();
                User user = this.removeUser(sourceNick, newNick);
                if (null != user) {
                    user = new User(user.getPrefix(), newNick);
                    this.addUser(channel, user);
                }
            }
        }
    }

    protected void onPart(String target, String sourceNick, String sourceLogin, String sourceHostname) {
    }

    private void removeChannel(String target) {

    }

    private final void removeUser(String  nick) {
        synchronized (this.channels) {
            Enumeration<String> enumeration = this.channels.keys();
            while (enumeration.hasMoreElements()) {
                String channel = enumeration.nextElement();
                this.removeUser(channel, nick);
            }
        };
    }

    @Nullable
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

    protected void onJoin(String channel, String sourceNick, String sourceLogin, String sourceHostname) {
        
    }

    private final void addUser(String channel, User user) {
        channel = channel.toLowerCase();
        synchronized (this.channels) {
            Hashtable<User, User> userUserHashtable = this.channels.get(channel);
            if (null == userUserHashtable) {
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

    private void onFinger(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        this.sendRawLine("NOTICE " + sourceNick + "\u0001PING" + this.finger + "\u0001");
    }

    private void onTime(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        this.sendRawLine("NOTICE " + sourceNick + "\u0001PING" + new Date().toString() + "\u0001");
    }

    protected void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String substring) {
        this.sendRawLine("NOTICE " + sourceNick + "\u0001PING" + substring + "\u0001");
    }

    protected void onAction(String sourceNick, String sourceLogin, String sourceHostname, String target, String substring) {
        
    }

    protected void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        this.sendRawLine("NOTICE " + sourceNick + " :\u0001VERSION" + this.version + "\u0001");
    }

    protected void onUnknown(String line) {

    }

    public final synchronized void sendRawLineViaQueue(String line) {
        if (null == line) {
            throw new NullPointerException("Sorry, you can't send null message.");
        }
        if (isConnected()) {
            outputQueue.add(line);
        }
    }

    protected void onNickChange(String oldNick, String login, String s, String nick) {
        
    }

    public final List<String> getAliases() {
        return Collections.unmodifiableList(this.aliases);
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
                first = response.indexOf(' ');
                second = response.indexOf(' ', first + 1);
                colon = response.indexOf(':');
                channel = response.substring(first + 1, second);
                topic = response.substring(code + 1);
                this.topics.put(channel, topic);
                this.onTopic(channel, topic);
                break;
            case RPL_TOPICINFO:
                StringTokenizer stringTokenizer = new StringTokenizer(response);
                stringTokenizer.nextToken();
                channel = stringTokenizer.nextToken();
                String setBy = stringTokenizer.nextToken();
                Long date = 0L;
                try {
                    date = Long.parseLong(stringTokenizer.nextToken()) * 1000;
                } catch (NumberFormatException e) {
                }
                topic = this.topics.get(channel);
                this.topics.remove(channel);
                this.onTopic(channel, topic, setBy, date, false);
                break;
            case RPL_NAMREPLY:
                int channelEndIndex = response.indexOf(" :");
                channel = response.substring(response.lastIndexOf(' ', channelEndIndex - 1) + 1, channelEndIndex);
                StringTokenizer tokenizer = new StringTokenizer(response.substring(response.indexOf(" :") + 2));
                while (tokenizer.hasMoreTokens()) {
                    String nick = tokenizer.nextToken();
                    String prefix = "";
                    if (nick.startsWith("@")) {
                        prefix = "@";
                    }
                    else if (nick.startsWith("+")) {
                        prefix = "+";
                    }
                    else if (nick.startsWith(".")) {
                        prefix = ".";
                    }
                    else if (nick.startsWith("%")) {
                        prefix = "%";
                    }
                    nick = nick.substring(prefix.length());
                    this.addUser(channel, new User(prefix, nick));
                }
                break;
            case RPL_ENDOFNAMES:
                channel = response.substring(response.indexOf(' ') + 1, response.indexOf(" :"));
                User[] users = this.getUsers(channel);
                this.onUserList(channel, users);
                break;
        }
        this.onServerResponse(code, response);
    }

    private void onUserList(String channel, User[] users) {
    }

    protected void onTopic(String channel, String topic) {
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

    public String getLogin() {
        return this.login;
    }

    public String getNick() {
        return this.nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public final User[] getUsers(String channel) {
        channel = channel.toLowerCase();
        User[] userArray = new User[0];
        synchronized (this.channels) {
            Hashtable<User, User> userUserHashtable = this.channels.get(channel);
            if (null != userUserHashtable) {
                userArray = new User[userUserHashtable.size()];
                Enumeration<User> enumeration = userUserHashtable.elements();
                for (int i = 0; i < userArray.length; i++) {
                    User user = enumeration.nextElement();
                    userArray[i] = user;
                }
            }
        }
        return userArray;
    }
}
