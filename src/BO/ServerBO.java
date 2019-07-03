/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import Utils.ChatMessage;
import Utils.ChatService;
import View.ServerView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hung.tran
 */
public class ServerBO {
    // a unique ID for each connection

    private static int uniqueId;
    private ArrayList<ClientThread> listClient;
    private ServerView serverView;
    private SimpleDateFormat sdf;
    private int port;
    // the boolean that will be turned of to stop the server
    private boolean keepGoing;
    
    private ChatService chatService;

    public ServerBO(ServerView serverView, int port) {
        this.serverView = serverView;
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        listClient = new ArrayList<>();
        chatService = new ChatService();
    }

    public void start() {
        keepGoing = true;
        /* create socket server and wait for connection requests */
        try {
            ServerSocket serverSocket = new ServerSocket(port);           

            // infinite loop to wait for connections
            while (keepGoing) {
                display("Server đang hoạt động ở cổng " + port + ".");

                Socket socket = serverSocket.accept();
                // if I was asked to stop
                if (!keepGoing) {
                    break;
                }
                ClientThread t = new ClientThread(socket);
                listClient.add(t);
                t.start();
            }
            // I was asked to stop
            try {
                serverSocket.close();
                for (int i = 0; i < listClient.size(); ++i) {
                    ClientThread tc = listClient.get(i);
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ioE) {
                    }
                }
            } catch (Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        }
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    public void stop() {
        keepGoing = false;
        try {
            Socket socket = new Socket("localhost", port);
        } catch (Exception e) {
            // nothing I can really do
        }
    }

    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        serverView.appendEvent(time + "\n");
    }

    /*
     *  to broadcast a message to all Clients
     */
    private synchronized void broadcast(ChatMessage message) {
        String messageLf = message.getMessage();
        if (message.getType() != ChatMessage.ONLINE || message.getType() != ChatMessage.LOGOUT) {
            String time = sdf.format(new Date());
            messageLf = time + " " + message.getMessage() + "\n";
            message.setMessage(messageLf);
        }

        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for (int i = listClient.size(); --i >= 0;) {
            ClientThread ct = listClient.get(i);
            // try to write to the Client if it fails remove it from the list
            if (!ct.writeMsg(message)) {
                listClient.remove(i);
                display("Disconnected Client " + ct.username + " removed from list.\n");
            }
        }
    }

    private synchronized void remove(int id) {
        // scan the array list until we found the Id
        for (int i = 0; i < listClient.size(); ++i) {
            ClientThread ct = listClient.get(i);
            // found it
            if (ct.id == id) {
                listClient.remove(i);
                return;
            }
        }
    }

    /**
     * One instance of this thread will run for each client
     */
    class ClientThread extends Thread {
        // the socket where to listen/talk

        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;
        String date;
        ArrayList<String> listUser = new ArrayList<>();

        ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            /* Creating both Data Stream */
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
                display(username + " đã đăng nhập.");
            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            date = new Date().toString() + "\n";
        }

        // what will run forever
        @Override
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while (keepGoing) {
                // read a String (which is an object)
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }
                String message = cm.getMessage();
                switch (cm.getType()) {

                    case ChatMessage.MESSAGE:
                        broadcast(new ChatMessage(ChatMessage.MESSAGE, username + ": " + message));
                        break;
                    case ChatMessage.LOGOUT:
                        remove(id);
                        synchronized (this) {
                            display(username + " đã đăng xuất.");
                            String messageSend = ">> [Hệ thống]: " + username + " đã đăng xuất.";                            
                            listUser.clear();
                            for (ClientThread clientThread : listClient) {
                                listUser.add(clientThread.username);
                            }
                            broadcast(new ChatMessage(ChatMessage.LOGOUT, messageSend, username, listUser));
                            keepGoing = false;
                        }                       
                        break;
                    case ChatMessage.ONLINE:
                        message = ">> [Hệ thống]: " + username + " đã đăng nhập.";
                        listUser.clear();
                        listClient.forEach((item) -> {
                            listUser.add(item.username);
                        });
                        broadcast(new ChatMessage(ChatMessage.ONLINE, message, username, listUser));
                        break;
                }
            }
            // remove myself from the arrayList containing the list of the connected Clients
            remove(id);
            close();
        }

        // try to close everything
        private void close() {
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
                if (sInput != null) {
                    sInput.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(ChatMessage msg) {
            // if Client is still connected send the message to it
            if (!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }
    }

}
