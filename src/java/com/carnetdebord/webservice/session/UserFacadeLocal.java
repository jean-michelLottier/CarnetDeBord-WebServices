/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.session;

import com.carnetdebord.webservice.entities.User;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Local
public interface UserFacadeLocal {

    public void create(User user);

    public void edit(User user);

    public void remove(User user);

    public User find(Object id);

    public List<User> findAll();

    public List<User> findRange(int[] range);

    public int count();

    public User findUserByLogin(String login);

    public List<User> findUserByName(String name);
}
