/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.UserBean;
import Utils.ConnectDB;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author hung.tran
 */
public class UserDAO {

    public static boolean addUser(UserBean user) throws ClassNotFoundException, SQLException {
        int empID = 0;
        StringBuilder query = new StringBuilder();
        query.append(" INSERT INTO User");
        query.append(" ([username], [password], [fullname], [ip], [isconnected])");
        query.append(" VALUES(?,?,?,?,?,)");
        PreparedStatement pStmt = ConnectDB.conn.prepareStatement(query.toString());
        pStmt.setString(1, user.getUsername());
        pStmt.setString(2, user.getPassword());
        pStmt.setNString(3, user.getFullName());
        pStmt.setString(4, user.getIp());
        pStmt.setBoolean(5, true);
        pStmt.execute();
        return true;
    }
    
    public static boolean updateIPUser(UserBean user) throws ClassNotFoundException, SQLException {
        StringBuilder query = new StringBuilder();
         query.append(" UPDATE [User] SET");
        query.append(" [ip] = ?");
        query.append(" WHERE [id] = ?");        
        PreparedStatement pStmt = ConnectDB.conn.prepareStatement(query.toString());
        pStmt.setString(1, user.getIp());
        pStmt.setInt(2, user.getId());
        pStmt.executeUpdate();
        return true;
    }

}
