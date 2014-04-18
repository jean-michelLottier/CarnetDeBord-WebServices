/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.session;

import com.carnetdebord.webservice.entities.Historical;
import com.carnetdebord.webservice.entities.Ticket;
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
public class HistoricalFacade extends AbstractFacade<Historical> implements HistoricalFacadeLocal {

    @PersistenceContext(unitName = "CarnetDeBordPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistoricalFacade() {
        super(Historical.class);
    }

    @Override
    public List<Ticket> findUserTicketsConsulted(long userID, long limit) {
        if (userID <= 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder("SELECT h.ticketFK"
                + " FROM Historical h"
                + " WHERE h.userFK.id = :userid ORDER BY h.lastVisitedDate DESC");

        Query query = em.createQuery(sb.toString())
                .setParameter("userid", userID);
        if (limit > 0) {
            query.setMaxResults((int) limit);
        }

        List<Ticket> tickets;
        try {
            tickets = (List<Ticket>) query.getResultList();
        } catch (NoResultException e) {
            tickets = null;
        }

        return tickets;
    }

    @Override
    public Historical findHistoricalByTicketIDAndUserID(long userID, long ticketID) {
        if (userID < 0 || ticketID < 0) {
            return null;
        }

        Query query = em.createQuery("SELECT h"
                + " FROM Historical h"
                + " WHERE h.userFK.id=:userID AND h.ticketFK.id=:ticketID")
                .setParameter("userID", userID)
                .setParameter("ticketID", ticketID);

        Historical historical = new Historical();
        try {
            historical = (Historical) query.getSingleResult();
        } catch (NoResultException e) {
            historical = null;
        }
        
        return historical;
    }

}
