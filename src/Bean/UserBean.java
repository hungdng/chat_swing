/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bean;

import java.util.Date;

/**
 *
 * @author hung.tran
 */
public class UserBean {
    private int id;
    private String username;
    private String password;
    private String ip;
    private String fullName;
    private boolean isConnected;
    private Date lastLogin;

    public UserBean() {
    }

    public UserBean(int id, String username, String password, String ip, String fullName, boolean isConnected, Date lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.fullName = fullName;
        this.isConnected = isConnected;
        this.lastLogin = lastLogin;
    }

    public UserBean(String username, String password, String ip, String fullName, boolean isConnected) {
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.fullName = fullName;
        this.isConnected = isConnected;
        this.lastLogin = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", password=" + password + ", ip=" + ip + ", fullName=" + fullName + ", isConnected=" + isConnected + ", lastLogin=" + lastLogin + '}';
    }
    
    
}
