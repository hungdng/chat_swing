/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import Utils.ChatMessage;
import View.ServerView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author hung.tran
 */
public class ServerBO {
    // a unique ID for each connection

    private static int uniqueId;
    // an ArrayList to keep the list of the Client
    private ArrayList<ClientThread> listClient;
    // if I am in a GUI
    private ServerView serverView;
    // to display time
    private SimpleDateFormat sdf;
    // the port number to listen for connection
    private int port;
    // the boolean that will be turned of to stop the server
    private boolean keepGoing;

    public ServerBO(ServerView serverView, int port) {
        this.serverView = serverView;
        this.port = port;
        sdf = new SimpleDateFormat("HH:mm:ss");
        // ArrayList for the Client list
        listClient = new ArrayList<ClientThread>();
    }

    public void start() {
        keepGoing = true;
        /* create socket server and wait for connection requests */
        try {
            // the socket used by the server
            ServerSocket serverSocket = new ServerSocket(port);

            // infinite loop to wait for connections
            while (keepGoing) {
                // format message saying we are waiting
                display("Server waiting for Clients on port " + port + ".");

                Socket socket = serverSocket.accept();  	// accept connection
                // if I was asked to stop
                if (!keepGoing) {
                    break;
                }
                ClientThread t = new ClientThread(socket);  // make a thread of it
                listClient.add(t);									// save it in the ArrayList
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
                        // not much I can do
                    }
                }
            } catch (Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        } // something went bad
        catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    /*
     * For the GUI to stop the server
     */
    public void stop() {
        keepGoing = false;
        // connect to myself as Client to exit statement 
        // Socket socket = serverSocket.accept();
        try {
            Socket socket = new Socket("localhost", port);
        } catch (Exception e) {
            // nothing I can really do
        }
    }

    /*
     * Display an event (not a message) to the console or the GUI
     */
    private void display(String msg) {
        String time = sdf.format(new Date()) + " " + msg;
        if (serverView == null) {
            System.out.println(time);
        } else {
            serverView.appendEvent(time + "\n");
        }
    }

    /*
     *  to broadcast a message to all Clients
     */
    private synchronized void broadcast(int type, String message) {
        if (type == ChatMessage.WHOISIN || type == ChatMessage.LOGOUT) {
            String[] listStr = message.split("[:]");
            // display message on console or GUI
//            serverView.appendRoom(message);
            // we loop in reverse order in case we would have to remove a Client
            // because it has disconnected
            for (int i = listClient.size(); --i >= 0;) {
                ClientThread ct = listClient.get(i);
                // try to write to the Client if it fails remove it from the list
                if (!ct.username.equals(listStr[0])) {
                    if (!ct.writeMsg(message)) {
                        listClient.remove(i);
                        display("Disconnected Client " + ct.username + " removed from list.");
                    }
                }
            }
        } else {
            // add HH:mm:ss and \n to the message
            String time = sdf.format(new Date());
            String messageLf = time + " " + message + "\n";
            // display message on console or GUI
            if (serverView == null) {
                System.out.print(messageLf);
            } else {
                serverView.appendRoom(messageLf);     // append in the room window
            }
        // we loop in reverse order in case we would have to remove a Client
            // because it has disconnected
            for (int i = listClient.size(); --i >= 0;) {
                ClientThread ct = listClient.get(i);
                // try to write to the Client if it fails remove it from the list
                if (!ct.writeMsg(messageLf)) {
                    listClient.remove(i);
                    display("Disconnected Client " + ct.username + " removed from list.");
                }
            }
        }

    }

    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id) {
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
        // my unique id (easier for deconnection)
        int id;
        // the Username of the Client
        String username;
        // the only type of message a will receive
        ChatMessage cm;
        // the date I connect
        String date;

        // Constructore
        ClientThread(Socket socket) {
            // a unique id
            id = ++uniqueId;
            this.socket = socket;
            /* Creating both Data Stream */
            System.out.println("Thread trying to create Object Input/Output Streams");
            try {
                // create output first
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                // read the username
                username = (String) sInput.readObject();
                display(username + " just connected.");
            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            } // have to catch ClassNotFoundException
            // but I read a String, I am sure it will work
            catch (ClassNotFoundException e) {
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
                // the messaage part of the ChatMessage
                String message = cm.getMessage();

                // Switch on the type of message receive
                switch (cm.getType()) {

                    case ChatMessage.MESSAGE:
                        broadcast(ChatMessage.MESSAGE, username + ": " + message);
                        break;
                    case ChatMessage.LOGOUT:
                        display(username + " disconnected with a LOGOUT message.");
                        broadcast(ChatMessage.LOGOUT,username + ": disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case ChatMessage.WHOISIN:
                        System.out.println("-=--" + id + " - " + username);
                        message = username + ": đã đăng nhập";
                        broadcast(ChatMessage.WHOISIN,message);
//                        writeMsg("List of the users connected at " + sdf.format(new Date()) + "\n");
//                        // scan al the users connected
//                        for (int i = 0; i < listClient.size(); ++i) {
//                            ClientThread ct = listClient.get(i);
//                            writeMsg((i + 1) + ") " + ct.username + " since " + ct.date);
//                        }
                        break;
                }
            }
            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
            close();
        }

        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch (IOException e) {
            }
            try {
                if (sInput != null) {
                    sInput.close();
                }
            } catch (IOException e) {
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
            }
        }

        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(String msg) {
            // if Client is still connected send the message to it
            if (!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            } // if an error occurs, do not abort just inform the user
            catch (IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }
    }

}
