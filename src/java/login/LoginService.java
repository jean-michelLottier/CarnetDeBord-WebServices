/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import entities.User;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import session.UserFacadeLocal;
import utils.CarnetDeBordUtils;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public class LoginService extends CarnetDeBordUtils implements ILoginService {

    public static final Logger logger = Logger.getLogger(LoginService.class.getName());

    @EJB
    private UserFacadeLocal userFacade;

    public LoginService() {
        try {
            this.userFacade = (UserFacadeLocal) new InitialContext().lookup("java:module/UserFacade");
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Impossible to init UserFacade object.", ex);
        }
    }

    public UserFacadeLocal getUserFacade() {
        if (userFacade == null) {
            try {
                userFacade = (UserFacadeLocal) new InitialContext().lookup("java:module/UserFacade");
            } catch (NamingException ex) {
                logger.log(Level.SEVERE, "Impossible to init UserFacade object.", ex);
            }
        }
        return userFacade;
    }

    public void setUserFacade(UserFacadeLocal userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public codeConnection isLoginPasswordCorrect(String login, String password) {
        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            return codeConnection.ERROR_EMPTY_FIELD;
        }

        User user = userFacade.findUserByLogin(login);

        if (user == null) {
            return codeConnection.ERROR_LOGIN;
        }

        if (!user.getPassword().equals(password)) {
            return codeConnection.ERROR_PASSWORD;
        }

        return codeConnection.SUCCESS;
    }

    @Override
    public codeConnection createNewUser(User user) {
        if (user == null) {
            return codeConnection.ERROR_REGISTER;
        }

        if (userFacade.findUserByLogin(user.getLogin()) != null) {
            return codeConnection.ERROR_ACCOUNT_ALREADY_EXIST;
        }

        userFacade.create(user);

        return codeConnection.SUCCESS;
    }

}
