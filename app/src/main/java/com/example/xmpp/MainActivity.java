package com.example.xmpp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class MainActivity extends AppCompatActivity  {
    ChatView chatView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
            @Override
            public boolean sendMessage(ChatMessage chatMessage){
                // perform actual message sending
                Log.d("send","sendtrue");
                return true;
            }
        });
        chatView.setTypingListener(new ChatView.TypingListener(){
            @Override
            public void userStartedTyping(){
                // will be called when the user starts typing
                Log.d("typing","typingtrue");
            }

            @Override
            public void userStoppedTyping(){
                // will be called when the user stops typing
                Log.d("typing","typingfalse");
            }
        });
        MyLoginTask task = new MyLoginTask();
        task.execute("");

    }




    private class MyLoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            // Create a connection to the jabber.org server.
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("sawah", "sawah@123")
                    .setHost("54.70.211.61")
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setServiceName("localhost")
                    .setPort(5222)
                    .setDebuggerEnabled(true) // to view what's happening in detail
                    .build();

            AbstractXMPPConnection conn1 = new XMPPTCPConnection(config);
            try {
                conn1.connect();

                if(conn1.isConnected()) {
                    Log.d("app", "conn done");
                }
                conn1.login();

                if(conn1.isAuthenticated()) {
                    Log.d("app", "Auth done");
                    ChatManager chatManager = ChatManager.getInstanceFor(conn1);
                    chatManager.addChatListener(
                            new ChatManagerListener() {
                                @Override
                                public void chatCreated(Chat chat, boolean createdLocally)
                                {
                                    chat.addMessageListener(new ChatMessageListener()
                                    {
                                        @Override
                                        public void processMessage(Chat chat, Message message) {
                                            System.out.println("Received message: "
                                                    + (message != null ? message.getBody() : "NULL"));
                                            Log.d("received",message != null ? ""+message.getBody() : "NULL");
                                             if(message !=null && !message.getBody().equals("")) {
                                                 chatView.addMessage(new ChatMessage(message.getBody(),
                                                         System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
                                             }
                                        }
                                    });

                                    Log.d("app", chat.toString());
                                }
                            });
                }
            }
            catch (Exception e) {
                Log.d("error", e.toString());
            }

            return "";
        }


        @Override
        protected void onPostExecute(String result) {
        }

    }
}

