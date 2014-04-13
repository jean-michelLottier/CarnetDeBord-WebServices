/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.login;

import com.carnetdebord.webservice.email.EmailService;
import com.carnetdebord.webservice.email.IEmailService;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.carnetdebord.webservice.entities.User;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.carnetdebord.webservice.session.UserFacadeLocal;
import com.carnetdebord.webservice.utils.CarnetDeBordUtils;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public class LoginService extends CarnetDeBordUtils implements ILoginService {

    public static final Logger logger = Logger.getLogger(LoginService.class.getName());

    private IEmailService emailService;

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

        if (!user.getActivation()) {
            return codeConnection.ERROR_ACCOUNT_NOT_ACTIVATED;
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

    /**
     * <p>
     * Get information about user.</p>
     *
     * @param login
     * @return User if this one exists otherwise null
     */
    @Override
    public User getUserInformation(String login) {
        if (login == null || login.isEmpty()) {
            return null;
        }

        return userFacade.findUserByLogin(login);
    }

    @Override
    public codeConnection generatePassword(String login) throws IllegalArgumentException {
        logger.info("method generatePassword");
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Impossible to generate password without login");
        }

        User user = getUserInformation(login);
        if (user == null) {
            logger.info("user null");
            return codeConnection.ERROR_LOGIN;
        }

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; ++i) {
            int ascii = 48 + random.nextInt(47);
            String c = String.valueOf((char) ascii);
            sb.append(random.nextInt(2) == 0 ? c.toLowerCase() : c);
        }
        logger.log(Level.INFO, "old password : {0}", user.getPassword());
        String newPassword;
        try {
            newPassword = new String(encryptContent(sb.toString()), "UTF8");
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Bad encoding", e);
            newPassword = sb.toString();
        }
        logger.log(Level.INFO, "new password : {0}", newPassword);
        user.setPassword(newPassword);

        logger.info("Edit user");
        userFacade.edit(user);

        logger.info("Send email");
        emailService = new EmailService();
        emailService.sendForgotPassordEmailWithGmail(user, sb.toString());
        
        return codeConnection.SUCCESS;
    }

    @Override
    public codeConnection activateAccount(String token) {
        User user = userFacade.findUserByLogin(token);
        if (user == null) {
            return codeConnection.ERROR_LOGIN;
        }

        user.setActivation(true);
        userFacade.edit(user);

        return codeConnection.SUCCESS;
    }

    private byte[] encryptContent(String content) {
        content = Base64.encode(content.getBytes());

//        KeyGenerator kg;
//        try {
//            kg = KeyGenerator.getInstance("AES");
//        } catch (NoSuchAlgorithmException e) {
//            logger.log(Level.SEVERE, "AES algorithm not supported", e);
//            return null;
//        }
        Properties properties = new Properties();
        try {
            properties.load(CarnetDeBordUtils.class.getResourceAsStream("utils.properties"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Impossible to load file 'utils.properties'.", e);
        }
        String strKey = properties.getProperty("keyAES");
        SecretKey key = new SecretKeySpec(Base64.decode(strKey), "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return cipher.doFinal(content.getBytes("UTF8"));
        } catch (NoSuchPaddingException e) {
            logger.log(Level.SEVERE, "padding problem", e);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "algorithm problem", e);
        } catch (InvalidKeyException e) {
            logger.log(Level.SEVERE, "key invalid", e);
        } catch (BadPaddingException e) {
            logger.log(Level.SEVERE, "bad padding", e);
        } catch (IllegalBlockSizeException e) {
            logger.log(Level.SEVERE, "illegal block size", e);
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Bad encoding", e);
        }

        return null;
    }

    private byte[] decryptContent(byte[] content) {
        Properties properties = new Properties();
        try {
            properties.load(CarnetDeBordUtils.class.getResourceAsStream("utils.properties"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Impossible to load file 'utils.properties'.", e);
        }
        String strKey = properties.getProperty("keyAES");
        SecretKey key = new SecretKeySpec(Base64.decode(strKey), "AES");

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            content = cipher.doFinal(content);
            content = Base64.decode(new String(content, "UTF8"));

            return content;
        } catch (NoSuchPaddingException e) {
            logger.log(Level.SEVERE, "padding problem", e);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "algorithm problem", e);
        } catch (InvalidKeyException e) {
            logger.log(Level.SEVERE, "key invalid", e);
        } catch (BadPaddingException e) {
            logger.log(Level.SEVERE, "bad padding", e);
        } catch (IllegalBlockSizeException e) {
            logger.log(Level.SEVERE, "illegal block size", e);
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Bad encoding", e);
        }

        return null;
    }

    @Override
    public User findUserById(long id) {
        if(id < 0){
            return null;
        }
        
        return userFacade.findUserByID(id);
    }
}
