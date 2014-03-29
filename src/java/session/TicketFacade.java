/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session;

import entities.Ticket;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
}
