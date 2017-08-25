package br.com.pdasolucoes.connectfood.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import br.com.pdasolucoes.connectfood.dao.ProdutosDao;
import br.com.pdasolucoes.connectfood.model.Produtos;

/**
 * Created by PDA on 24/02/2017.
 */

public class SendMailRomaneio extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Session session;
    private String email;
    private String subject;
    private String message;
    private String table;

    public SendMailRomaneio(Context context, String email, String subject, String table, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.table = table;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");


        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(ConfigEmail.EMAIL, ConfigEmail.PASSWORD);
                    }
                });

        try {

            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress(ConfigEmail.EMAIL,"Connecting Food"));

            Address[] toUser = InternetAddress.parse(email);
            //Adding receiver
            mm.addRecipients(Message.RecipientType.TO, toUser);
            //Adding subject
            mm.setSubject(subject);

            String cod = "Código de confirmação: ";

            mm.setContent(table + "</TABLE>" + "<br>" + cod + message + "<br>" + "<br><h3>Agradecemos a colaboração!</h3>", "text/html; charset=UTF-8");
            Transport.send(mm);


        } catch (MessagingException e) {
            e.printStackTrace();
            //Toast.makeText(context, "email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
