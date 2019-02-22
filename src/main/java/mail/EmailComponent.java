package mail;

import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.api.services.gmail.Gmail;
import mail.service.gmail.MessageData;
import schedule.model.TimeOption;
import schedule.model.TimePreferences;

public interface EmailComponent<Message, WatchResponse> {
    public void storeHistoryIdFromLastSync() throws IOException;
    public MessageData getMessageData(BigInteger historyId) throws IOException;
    public Message getMessage(String messageId);
    public WatchResponse registerMailboxNotification(String topicName);
    public MimeMessage createEmail(String to, String from, MessageData messageData, String bodyText) throws MessagingException;
    public Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException;
    public Message sendMessage(String userId,MimeMessage emailContent) throws MessagingException, IOException, GeneralSecurityException;

    public StringBuilder generateEmail(TimePreferences pref, String guestName, String ownerName);
    public StringBuilder generateConfirmation(TimeOption option, String guestName);
    public StringBuilder generateWelcomeEmail();
}