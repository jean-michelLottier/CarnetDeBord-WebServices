/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.session;

import com.carnetdebord.webservice.entities.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Stateless
public class UserFacade extends AbstractFacade<User> implements UserFacadeLocal {

    @PersistenceContext(unitName = "CarnetDeBordPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }

    @Override
    public User findUserByLogin(String login) {
        Query query = em.createNamedQuery("User.findByLogin")
                .setParameter("login", login);
        User user;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            user = null;
        }

        return user;
    }

    @Override
    public List<User> findUserByName(String name) {
        Query query = em.createNamedQuery("User.findByName")
                .setParameter("name", name);
        List<User> users = (List<User>) query.getResultList();

        return users;
    }

    @Override
    public User findUserByID(long id) {
        if (id < 0) {
            return null;
        }

        Query query = em.createNamedQuery("User.findById")
                .setParameter("id", id);

        User user;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            user = null;
        }

        return user;
    }

}
