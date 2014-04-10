/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.session;

import com.carnetdebord.webservice.entities.Ticket;
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
public class TicketFacade extends AbstractFacade<Ticket> implements TicketFacadeLocal {

    @PersistenceContext(unitName = "CarnetDeBordPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TicketFacade() {
        super(Ticket.class);
    }

    @Override
    public Ticket findTicketByID(long ticketID) {
        if (ticketID < 0) {
            return null;
        }

        Query query = em.createNamedQuery("Ticket.findById")
                .setParameter("id", ticketID);

        Ticket ticket;
        try {
            ticket = (Ticket) query.getSingleResult();
        } catch (NoResultException e) {
            ticket = null;
        }

        return ticket;
    }

    @Override
    public Ticket findTicketByID(long userID, long ticketID) {
        if (userID < 0 || ticketID < 0) {
            return null;
        }

        Query query = em.createQuery("SELECT t "
                + "FROM Ticket t "
                + "WHERE t.id = :ticketID AND t.userFK.id = :userID")
                .setParameter("ticketID", ticketID)
                .setParameter("userID", userID);

        Ticket ticket;
        try {
            ticket = (Ticket) query.getSingleResult();
        } catch (NoResultException e) {
            ticket = null;
        }

        return ticket;
    }

}
