/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.Serializable;

/**
 *
 * @author hung.tran
 */
public class ChatMessage implements Serializable {
    
    protected static final long serialVersionUID = 1112122200L;
    
    public static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
    private int type;
    private String message;

    public ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }        

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

}
