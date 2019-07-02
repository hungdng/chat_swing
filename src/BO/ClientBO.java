/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import Utils.ChatMessage;
import View.ClientView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author hung.tran
 */
public class ClientBO {

    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;	
    private Socket socket;

    // if I use a GUI or not
    private ClientView clientView;

    // the server, the port and the username
    private String server, username;
    private int port;

    public ClientBO(ClientView clientView, String server, String username, int port) {
        this.clientView = clientView;
        this.server = server;
        this.username = username;
        this.port = port;
    }

    public ClientBO(String server, String username, int port) {
        this(null, server, username, port);
    }

    /*
     * To start the dialog
     */
    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, port);
        } // if it failed not much I can so
        catch (Exception ec) {
            display("Error connectiong to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server 
        new ListenFromServer().start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage objects
        try {
            sOutput.writeObject(username);
        } catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }

    private void display(String msg) {
       clientView.append(msg + "\n");
    }

    private void display(ChatMessage msg) {
        clientView.append(msg.getMessage() + "\n");
    }

    /*
     * To send a message to the server
     */
    public void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
//            e.printStackTrsace();
            display("Exception writing to server: " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if (sInput != null) {
                sInput.close();
            }
            if (sOutput != null) {
                sOutput.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
        }

        // inform the GUI
        if (clientView != null) {
            clientView.connectionFailed();
        }

    }


    class ListenFromServer extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    ChatMessage msg = (ChatMessage) sInput.readObject();

                    if (msg.getType() == ChatMessage.LOGOUT || msg.getType() == ChatMessage.ONLINE) {
                        if (!username.equalsIgnoreCase(msg.getUsername())) {
                            clientView.append(msg.getMessage());
                        }
                        clientView.loadData(msg.getListUser());
                    } else {
                        clientView.append(msg.getMessage());
                    }
                } catch (IOException e) {
                    display("Server has close the connection.");
                    if (clientView != null) {
                        clientView.connectionFailed();
                    }
                    break;
                } catch (ClassNotFoundException e2) {
                }
            }
        }
    }

}
