/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author hung.tran
 */
public class ChatMessage implements Serializable {
    
    protected static final long serialVersionUID = 1112122200L;
    
    public static final int ONLINE = 0, MESSAGE = 1, LOGOUT = 2, WHOISIN = 3;
    private int type;
    private String username;
    private String message;
    private ArrayList<String> listUser = new ArrayList<>();

    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }  
    
    public ChatMessage(int type, String message, String username, ArrayList<String> listUser) {
        this.type = type;
        this.message = message;
        this.username = username;
        this.listUser = listUser;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    } 

    public ArrayList<String> getListUser() {
        return listUser;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
