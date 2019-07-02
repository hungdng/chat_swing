/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.UserBean;
import Utils.ConnectDB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hung.tran
 */
public class UserDAO implements IUserDAO {

    private UserBean createUser(ResultSet resultSet) throws SQLException {
        return new UserBean(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("ip"),
                resultSet.getString("fullname"),
                resultSet.getBoolean("isconnected")
        );
    }

    private void checkConnect() {
        if (ConnectDB.conn == null) {
            try {
                String userName = "sa";
                String password = "123456";
                String databaseName = "ChatDB";
                String serverName = "localhost";
                ConnectDB.Connect(serverName, databaseName, userName, password);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public UserDAO() {
        checkConnect();
    }

    @Override
    public List<UserBean> getAll() throws Exception {
        ResultSet rs = ConnectDB.getTable("*", "[User]");
        List<UserBean> ds = new ArrayList<>();
        while (rs.next()) {
            ds.add(createUser(rs));
        }
        rs.close();
        return ds;
    }

    @Override
    public Optional<UserBean> getById(int id) throws Exception {
        try (
                PreparedStatement statement = ConnectDB.conn.prepareStatement("SELECT * FROM [User] WHERE  id = ?")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createUser(resultSet));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public UserBean add(UserBean user) throws Exception {
        try {
            java.sql.Date lastLogin = new java.sql.Date(user.getLastLogin().getTime());
            String sql = "INSERT INTO [User] (username, password, fullname, ip, [isconnected], [lastlogin]) VALUES (?,?,?,?,?,?)";
            PreparedStatement statement = ConnectDB.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setNString(3, user.getFullName());
            statement.setString(4, user.getIp());
            statement.setBoolean(5, user.isIsConnected());
            statement.setDate(6, lastLogin);

            statement.execute();

            ResultSet rs = statement.getGeneratedKeys();
            int idValue = 0;
            if (rs.next()) {
                idValue = rs.getInt(1);
            }
            user.setId(idValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean update(UserBean user) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean delete(UserBean user) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<UserBean> getByUsername(String username) throws Exception {

        try {
            String sql = "SELECT * FROM [User] WHERE username = ?";
            PreparedStatement statement = ConnectDB.conn.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createUser(resultSet));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

}
