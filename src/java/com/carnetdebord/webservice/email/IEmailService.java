/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.email;

import com.carnetdebord.webservice.entities.Email;
import com.carnetdebord.webservice.entities.User;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public interface IEmailService {

    public Email sendConfirmationEmailWithGmail(String token, String... to);
    
    public void sendConfirmationEmailWithGmail(String token, User user);
}
