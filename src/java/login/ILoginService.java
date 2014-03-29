/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import entities.User;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public interface ILoginService {

    public enum codeConnection {

        ERROR_LOGIN, ERROR_PASSWORD, ERROR_EMPTY_FIELD, SUCCESS, ERROR_REGISTER, ERROR_ACCOUNT_ALREADY_EXIST;
    };

    public codeConnection isLoginPasswordCorrect(String login, String password);

    public codeConnection createNewUser(User user);

}
