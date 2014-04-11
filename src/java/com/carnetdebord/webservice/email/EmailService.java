/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.carnetdebord.webservice.email;

import com.carnetdebord.webservice.entities.Email;
import com.carnetdebord.webservice.entities.User;
import com.carnetdebord.webservice.session.EmailFacadeLocal;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author Jean-Michel Lottier <jean-michel.lottier@cpe.fr>
 */
public class EmailService implements IEmailService {
    
    public static final Logger logger = Logger.getLogger(EmailService.class.getName());
    
    private static final String EMAIL_SUBJECT_CONFIRMATION_INSCRIPTION = "Confirmation d'inscription";
    private static final String EMAIL_CONTENT_CONFIRMATION_INSCRIPTION = "<h1 style=\"color:blue;text-align:center;\">Horizon - Carnet de bord</h1>"
            + "<br/><p>Bienvenue sur <strong>Carnet de bord</strong> nouvel horizon!"
            + "<br/>Vous pouvez vous connecter à votre compte une fois que vous l'aurez activé. Pour cela, veuillez activer celui-ci en cliquant sur le lien suivant : <a href=\"http://localhost:8080/CarnetDeBord/webresources/login/token/tokenid/\" style=\"color:blue;text-decoration:none;\">activer mon compte carnet de bord</a>."
            + "<br/><br/>Cordialement,</p></p>"
            + "<br/><br/><p>L'équipe <strong>Horizon - Carnet de bord</strong></p>";
    
    @EJB
    private EmailFacadeLocal emailFacade;
    
    public EmailService() {
        try {
            this.emailFacade = (EmailFacadeLocal) new InitialContext().lookup("java:module/EmailFacade");
        } catch (NamingException e) {
            logger.log(Level.SEVERE, "Impossible to init EmailFacade object.", e);
        }
    }
    
    @Override
    public Email sendConfirmationEmailWithGmail(String token, String... to) {
        if (to == null || to.length == 0 || token == null || token.isEmpty()) {
            return null;
        }

//        if (from == null || from.isEmpty()) {
//            Properties prop = new Properties();
//            try {
//                prop.load(CarnetDeBordUtils.class.getResourceAsStream("utils.properties"));
//            } catch (IOException e) {
//                logger.log(Level.SEVERE, "Impossible to load file 'utils.properties'.", e);
//            }
//            from = prop.getProperty("carnetDeBordEmail");
//            password = prop.getProperty("carnetDeBordEmailPassword");
//            password = new String(Base64.decode(password), Charset.defaultCharset());
//        }
//        final String from1 = from;
//        final String password1 = password;
//        logger.log(Level.INFO, "from : {0}, password : {1}", new Object[]{from, password});
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.socketFactory.port", "465");
//        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.port", "465");
//
//        Session session = Session.getDefaultInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(from1, password1);
//            }
//        });
        InitialContext ctx = null;
        Session session = null;
        try {
            ctx = new InitialContext();
            session = (Session) ctx.lookup("mail/carnetdebord");
        } catch (NamingException ex) {
            Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Message message = new MimeMessage(session);
        StringBuilder sbAddresses = new StringBuilder();
        try {
//            message.setFrom(new InternetAddress(from));
            Address[] addresses = new Address[to.length];
            int i = 0;
            for (String address : to) {
                sbAddresses.append(address).append(",");
                InternetAddress internetAddress = new InternetAddress(address);
                addresses[i] = internetAddress;
                i++;
            }
            message.setRecipients(Message.RecipientType.TO, addresses);
            message.setSubject(EMAIL_SUBJECT_CONFIRMATION_INSCRIPTION);
            message.setContent(EMAIL_CONTENT_CONFIRMATION_INSCRIPTION.replace("tokenid", token), "text/html");
            
            Transport.send(message);
        } catch (MessagingException ex) {
            logger.log(Level.WARNING, "mail not sent", ex);
        }
        
        return new Email("horizon.carnetdebord@gmail.com", StringEscapeUtils.escapeXml(sbAddresses.toString().replaceAll("(,$)", "")), StringEscapeUtils.escapeXml(EMAIL_SUBJECT_CONFIRMATION_INSCRIPTION), StringEscapeUtils.escapeXml(EMAIL_CONTENT_CONFIRMATION_INSCRIPTION.replace("tokenid", token)));
    }
    
    @Override
    public void sendConfirmationEmailWithGmail(String token, User user) {
        Email email = sendConfirmationEmailWithGmail(token, user.getLogin());
        
        if (email != null) {
            email.setUserFK(user);
            email.setPostedDate(new Date());
            emailFacade.create(email);
        }
    }
    
}
