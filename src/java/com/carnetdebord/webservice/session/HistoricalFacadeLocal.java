/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.session;

import com.carnetdebord.webservice.entities.Historical;
import com.carnetdebord.webservice.entities.Ticket;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
@Local
public interface HistoricalFacadeLocal {

    void create(Historical historical);

    void edit(Historical historical);

    void remove(Historical historical);

    Historical find(Object id);

    List<Historical> findAll();

    List<Historical> findRange(int[] range);

    int count();

    public List<Ticket> findUserTicketsConsulted(long userID, long limit);

    public List<Historical> findHistoricalsByTicketID(long ticketID);

    public Historical findHistoricalByTicketIDAndUserID(long userID, long ticketID);
}
