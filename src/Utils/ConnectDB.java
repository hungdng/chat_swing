/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author hung.tran
 */
public class ConnectDB {

    public static Connection conn;
    public static final String DRIVER_CONNECT = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    public static final String CONNECT_SUCCESS = "Conect success";

    public static void Connect(String serverName, String databaseName, String userName, String password) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        Class.forName(DRIVER_CONNECT);
        String url = String.format("jdbc:sqlserver://%s:1433;databaseName=%s;user=%s; password=%s", serverName, databaseName, userName, password);
        conn = DriverManager.getConnection(url);
        System.out.println(CONNECT_SUCCESS);
    }

    public static void closeConnection() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
    
    public static void closePreparedStatement(PreparedStatement preparedStatement) throws SQLException {
        if (preparedStatement != null) {
            preparedStatement.close();
        }
    }

//    public static ResultSet queryTable(String query, String table, String condition) throws Exception {
//        StringBuilder sql = new StringBuilder(String.format("select %s from %s", query, table));
//        if (condition != null) {
//            sql.append(String.format(" where %s", condition));
//        }
//        PreparedStatement stmt = conn.prepareStatement(sql.toString());
//        return stmt.executeQuery();
//    }
}
