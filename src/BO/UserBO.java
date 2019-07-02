/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BO;

import Bean.UserBean;
import DAO.UserDAO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hung.tran
 */
public class UserBO {
    private UserDAO userDAO = new UserDAO();
    
    public boolean add(UserBean user) throws Exception{
        if (userDAO.getByUsername(user.getUsername()).isPresent()) {
            return false;
        }
        UserBean objectInserted = userDAO.add(user);
        return true;
    }
}
