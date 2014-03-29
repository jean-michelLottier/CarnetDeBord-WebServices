/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package session;

import entities.Historical;
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
    
}
