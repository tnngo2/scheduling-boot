package services.runnable;

import com.google.api.services.gmail.model.*;
import mail.EmailComponent;
import mail.service.gmail.GmailComponent;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class RegisterMailboxNotificationService {
    public static final String topicName = "projects/meeting-bot-1535683525121/topics/mail-agent";
    public static void main(String... args) throws IOException, GeneralSecurityException {
        EmailComponent emailComponent = new GmailComponent();

        WatchResponse response = (WatchResponse)emailComponent.registerMailboxNotification(topicName);
        System.out.println("HistoryResource Id:" + response.getHistoryId());
    }
}