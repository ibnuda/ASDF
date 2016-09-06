package com.parametris.iteng.asdf.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.TextKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parametris.iteng.asdf.ASDF;
import com.parametris.iteng.asdf.MapsActivity;
import com.parametris.iteng.asdf.R;
import com.parametris.iteng.asdf.activity.MainActivity;
import com.parametris.iteng.asdf.adapter.ConversationPagerAdapter;
import com.parametris.iteng.asdf.adapter.MessageListAdapter;
import com.parametris.iteng.asdf.comm.IRCBinder;
import com.parametris.iteng.asdf.comm.IRCService;
import com.parametris.iteng.asdf.listener.ConversationListener;
import com.parametris.iteng.asdf.listener.ServerListener;
import com.parametris.iteng.asdf.model.*;
import com.parametris.iteng.asdf.receiver.ConversationReceiver;
import com.parametris.iteng.asdf.receiver.ServerReceiver;
import com.parametris.iteng.asdf.view.ConversationTabLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ChatFragment extends Fragment implements ServerListener, ConversationListener, ServiceConnection {

    public static final String TAG = ChatFragment.class.getName();

    public static final int REQUEST_CODE_JOIN = 1;
    public static final int REQUEST_CODE_USERS = 2;
    public static final int REQUEST_CODE_USER = 3;
    public static final int REQUEST_CODE_NICK_COMPLETION = 4;

    private int serverId;
    private Server server;
    private IRCBinder ircBinder;
    private ConversationReceiver conversationReceiver;
    private ServerReceiver serverReceiver;

    private MainActivity mainActivity;

    private EditText inputEditText;
    private ViewPager viewPager;
    private ConversationPagerAdapter pagerAdapter;
    private ConversationTabLayout tabLayout;

    private ScrollBack scrollBack;
    private String joinChannelBuffer;
    private Snackbar snackbar;

    private final View.OnKeyListener inputKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            EditText input = (EditText) v;
            if (KeyEvent.ACTION_DOWN != event.getAction()) {
                return false;
            }
            String message = null;
            Boolean beneran = false;
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    message = scrollBack.goBack();
                    beneran = true;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    message = scrollBack.goForward();
                    beneran = true;
                    break;
                case KeyEvent.KEYCODE_ENTER:
                    sendCurrentMessage();
                    beneran = true;
                    break;
                case KeyEvent.KEYCODE_SEARCH:
                    doNickCompletion(input);
                    beneran = true;
                    break;
            }
            if (null != message) {
                input.setText(message);
            }
            return beneran;
        }
    };

    public ChatFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // TODO: 9/5/2016 Fix this shit up.
        this.mainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        serverId = getArguments().getInt("serverId");
        server = ASDF.getInstance().getServerById(serverId);
        scrollBack = new ScrollBack();
    }


    private void doNickCompletion(EditText input) {
        String text = input.getText().toString();
        if (1 > text.length()) {
            return;
        }
        String[] tokens = text.split("[\\s,.-]+");
        if (1 > tokens.length) {
            return;
        }

        String word = tokens[tokens.length - 1].toLowerCase();
        tokens[tokens.length - 1] = null;

        int begin = input.getSelectionStart();
        int end = input.getSelectionEnd();
        int cursor = Math.min(begin, end);
        int selectionEnd = Math.max(begin, end);

        boolean inSelection = (cursor != selectionEnd);

        if (inSelection) {
            word = text.substring(cursor, selectionEnd);
        } else {
            while (true) {
                cursor -= 1;
                if (1 > cursor || text.charAt(cursor) == ' ') {
                    break;
                }
            }

            if (cursor < 0) {
                cursor = 0;
            }

            if (text.charAt(cursor) == ' ') {
                cursor += 1;
            }

            selectionEnd = text.indexOf(' ', cursor);

            if (selectionEnd == -1) {
                selectionEnd = text.length();
            }

            word = text.substring(cursor, selectionEnd);
        }

        Conversation conversation = pagerAdapter.getItem(viewPager.getCurrentItem());
        String[] users = null;
        if (Conversation.TYPE_CHANNEL == conversation.getType()) {
            users = ircBinder.getIrcService().getConnection(server.getId()).getUsersAsStringArray(conversation.getName());
        }

        if (null != users) {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < users.length; i++) {
                String nick = removeStatusChar(users[i].toLowerCase());
                if (nick.startsWith(word.toLowerCase())) {
                    result.add(i);
                }
            }

            if (1 == result.size()) {
                input.setSelection(cursor, selectionEnd);
                insertNickCompletion(input, users[result.get(0)]);
            } else if (0 < result.size()) {
                // TODO: 9/5/2016 Fix MapsActivity, somehow.
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                String[] extra = new String[result.size()];
                System.arraycopy(extra, 0, users, 0, extra.length);

                input.setSelection(cursor, selectionEnd);
                intent.putExtra(Extra.USERS, extra);
                startActivityForResult(intent, REQUEST_CODE_NICK_COMPLETION);
            }
        }
    }

    private void insertNickCompletion(final EditText input, String user) {
        int start = input.getSelectionStart();
        int end = input.getSelectionEnd();
        user = removeStatusChar(user);
        if (0 == start) {
            user += ":";
        }
        user += " ";
        input.getText().replace(start, end, user, 0, user.length());
        input.setSelection(start, user.length());
        input.clearComposingText();
        input.post(new Runnable() {
            @Override
            public void run() {
                openSoftKeyboard(input);
            }
        });
        input.requestFocus();
    }

    private void openSoftKeyboard(View view) {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    private String removeStatusChar(String nick) {
        if (nick.startsWith("@") || nick.startsWith("+") || nick.startsWith("%")) {
            nick = nick.substring(1);
        }
        return nick;
    }

    private void sendCurrentMessage() {
        sendMessage(inputEditText.getText().toString());
        TextKeyListener.clear(inputEditText.getText());
    }

    private void sendMessage(String pesan) {
        if (pesan.equals("")) {
            return;
        }

        if (!server.isConnected()) {
            Message message = new Message(getString(R.string.message_not_connected));
            message.setColor(Message.COLOR_RED);
            server.getConversation(server.getSelected()).addMessage(message);
            onConversationMessage(server.getSelected());
        }
        scrollBack.addMessage(pesan);

        Conversation conversation = pagerAdapter.getItem(viewPager.getCurrentItem());
        if (null != conversation) {
            if (!pesan.trim().startsWith("/")) {
                if (Conversation.TYPE_SERVER != conversation.getType()) {
                    String nickname = ircBinder.getIrcService().getConnection(serverId).getNick();
                    conversation.addMessage(new Message(pesan, nickname));
                    ircBinder.getIrcService().getConnection(serverId).sendMessage(conversation.getName(), pesan);
                } else {
                    Message message = new Message(getString(R.string.chat_only_from_channel));
                    message.setColor(Message.COLOR_YELLOW);
                    conversation.addMessage(message);
                }
                onConversationMessage(conversation.getName());
            } else {
                // TODO: 9/5/2016 COMMAND PARSER.
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_something, container, false);
        Settings settings = new Settings(getActivity());
        inputEditText = (EditText) view.findViewById(R.id.input);
        inputEditText.setOnKeyListener(inputKeyListener);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        pagerAdapter = new ConversationPagerAdapter(getActivity(), server);
        viewPager.setAdapter(pagerAdapter);

        // TODO: 9/5/2016 Add tab layout.


        Toolbar.LayoutParams params = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (Status.PRE_CONNECTING == server.getStatus()) {
            server.clearConversations();
            pagerAdapter.clearConversation();
            server.getConversation(ServerInfo.DEFAULT_NAME).setHistorySize(settings.getHistorySize());
        }

        Collection<Conversation> conversations = server.getConversations();

        for (Conversation conversation : conversations) {
            if (Conversation.STATUS_SELECTED == conversation.getStatus()) {
                onNewConversation(conversation.getName());
            } else {
                createNewConversation(conversation.getName());
            }
        }

        int setInputTypeFlags = 0;
        setInputTypeFlags |= InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
        setInputTypeFlags |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
        setInputTypeFlags |= InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE;

        inputEditText.setInputType(inputEditText.getInputType() | setInputTypeFlags);
        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 < inputEditText.length()) {
                    sendCurrentMessage();
                }
            }
        });

        sendButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doNickCompletion(inputEditText);
                return true;
            }
        });
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    private void createNewConversation(String name) {
        pagerAdapter.addConversation(server.getConversation(name));
        tabLayout.update();
    }

    @Override
    public void onConversationMessage(String target) {

    }

    @Override
    public void onNewConversation(String target) {

    }

    @Override
    public void onRemoveConversation(String target) {

    }

    @Override
    public void onTopicChanged(String target) {

    }

    @Override
    public void onStatusUpdate() {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.ircBinder = (IRCBinder) service;

        if (Status.PRE_CONNECTING == server.getStatus() && getArguments().containsKey("connect")) {
            server.setStatus(Status.CONNECTING);
            ircBinder.connect(server);
        } else {
            onStatusUpdate();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.ircBinder = null;
    }

    @Override
    public void onResume() {
        conversationReceiver = new ConversationReceiver(server.getId(), this);
        getActivity().registerReceiver(conversationReceiver, new IntentFilter(Broadcast.CONVERSATION_MESSAGE));
        getActivity().registerReceiver(conversationReceiver, new IntentFilter(Broadcast.CONVERSATION_NEW));
        getActivity().registerReceiver(conversationReceiver, new IntentFilter(Broadcast.CONVERSATION_REMOVE));
        getActivity().registerReceiver(conversationReceiver, new IntentFilter(Broadcast.CONVERSATION_TOPIC));

        serverReceiver = new ServerReceiver(this);
        getActivity().registerReceiver(serverReceiver, new IntentFilter(Broadcast.SERVER_UPDATE));

        super.onResume();

        Intent intent = new Intent(getActivity(), IRCService.class);
        intent.setAction(IRCService.ACTION_FOREGROUND);
        getActivity().startService(intent);
        getActivity().bindService(intent, this, 0);

        inputEditText.setEnabled(server.isConnected());

        Collection<Conversation> conversations = server.getConversations();
        MessageListAdapter messageListAdapter;

        for (Conversation conversation : conversations) {
            String name = conversation.getName();
            messageListAdapter = pagerAdapter.getItemAdapter(name);

            if (null != messageListAdapter) {
                messageListAdapter.addBulkMessage(conversation.getBuffer());
                conversation.cleanBuffer();
            } else {
                if (-1 == pagerAdapter.getPositionByName(name)) {
                    onNewConversation(name);
                }
            }

            if (Conversation.STATUS_SELECTED == conversation.getStatus() && 0 < conversation.getNewMentions()) {
                Intent ackIntent = new Intent(getActivity(), IRCService.class);
                ackIntent.setAction(IRCService.ACTION_ACK_NEW_MENTIONS);
                ackIntent.putExtra(IRCService.EXTRA_ACK_SERVERID, serverId);
                ackIntent.putExtra(IRCService.EXTRA_ACK_CONVTITLE, name);
                getActivity().startService(intent);
            }
        }

        int numViews = pagerAdapter.getCount();
        if (numViews > conversations.size()) {
            for (int i = 0; i < numViews; i++) {
                if (conversations.contains(pagerAdapter.getItem(i))) {
                    pagerAdapter.removeConversation(i--);
                    --numViews;
                }
            }
        }

        if (null != joinChannelBuffer) {
            new Thread() {
                @Override
                public void run() {
                    ircBinder.getIrcService().getConnection(serverId).joinChannel(joinChannelBuffer);
                }
            }.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        server.setForeground(false);
        if (null != ircBinder && null != ircBinder.getIrcService()) {
            ircBinder.getIrcService().checkServiceStatus();
        }
        getActivity().unbindService(this);
        getActivity().unregisterReceiver(conversationReceiver);
        getActivity().unregisterReceiver(serverReceiver);
    }
}
