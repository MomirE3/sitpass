package svt.projekat.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import svt.projekat.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("jovanovicviktor10@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setText(text + "\n\nPlease do not reply to this email. This is an informational message.");
        emailSender.send(message);
        System.out.println("Uspesno poslao. ");
    }
}
