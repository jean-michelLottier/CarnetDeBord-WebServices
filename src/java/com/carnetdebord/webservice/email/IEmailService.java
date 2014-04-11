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

    /**
     * * <p>
     * Send email to notify user that his account has been created
     * successfuly.</p>
     *
     * @param token
     * @param to
     * @return
     */
    public Email sendConfirmationEmailWithGmail(String token, String... to);

    /**
     * {@link #sendConfirmationEmailWithGmail(java.lang.String, java.lang.String...)
     * }
     *
     * @param token
     * @param user
     */
    public void sendConfirmationEmailWithGmail(String token, User user);

    /**
     * <p>
     * Send email at the user to notify this password has been renewed.</p>
     *
     * @param user
     * @param password
     */
    public void sendForgotPassordEmailWithGmail(User user, String password);
}
