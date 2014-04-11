/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.login;

import com.carnetdebord.webservice.entities.User;
import com.carnetdebord.webservice.utils.CarnetDeBordUtils;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public interface ILoginService {

    /**
     * <p>
     * Determinate if the couple login/password is correct.</p>
     *
     * @param login
     * @param password
     * @return A specify code is returned according to the query result
     */
    public CarnetDeBordUtils.codeConnection isLoginPasswordCorrect(String login, String password);

    /**
     * <p>
     * Create a new user into data table.</p>
     *
     * @param user
     * @return
     */
    public CarnetDeBordUtils.codeConnection createNewUser(User user);

    public User getUserInformation(String login);

    /**
     * <p>
     * Method to generate a password encrypted and with size : 8.</p>
     *
     * @param login
     * @return
     * @throws IllegalArgumentException
     */
    public CarnetDeBordUtils.codeConnection generatePassword(String login) throws IllegalArgumentException;

    /**
     * <p>
     * Method to activate account.</p>
     *
     * @param token = login
     * @return
     */
    public CarnetDeBordUtils.codeConnection activateAccount(String token);
}
