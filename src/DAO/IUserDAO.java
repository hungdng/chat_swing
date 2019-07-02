/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Bean.UserBean;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author hung.tran
 */
public interface IUserDAO {
    List<UserBean> getAll() throws Exception;
    
    Optional<UserBean> getById(int id) throws Exception;
    
    Optional<UserBean> getByUsername(String username) throws Exception;
    
    Optional<UserBean> login(String username, String password) throws Exception;
    
    UserBean add(UserBean user) throws Exception;
    
    boolean update(UserBean user) throws Exception;
    
    boolean delete(UserBean user) throws Exception;
}
