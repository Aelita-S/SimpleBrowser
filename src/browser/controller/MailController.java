package browser.controller;

import browser.util.SMTPSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;

/**
 * 邮件界面控制器
 */
public class MailController {
    public Button sendButton;
    public TextField fromMailbox;
    public PasswordField fromPassword;
    public TextField toMailbox;
    public TextField mailTitle;
    public TextArea mailContent;

    public void sendMail(ActionEvent actionEvent) {
        try {
            SMTPSender.sendMail(fromMailbox.getText(), fromPassword.getText(), toMailbox.getText(),
                    mailTitle.getText(), mailContent.getText());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
