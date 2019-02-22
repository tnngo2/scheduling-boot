package mail.service.gmail;

import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;
import mail.EmailBuilder;
import mail.EmailComponent;

import java.io.*;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import org.apache.commons.codec.binary.Base64;
import schedule.model.TimeOption;
import schedule.model.TimePreferences;


public class GmailComponent<MessageType extends Message, WatchResponseType extends WatchResponse> implements EmailComponent {
    private static Gmail service;
    private static final String HISTORY_ID_FILE = "tokens/history-id";
    private BigInteger historyIdLatest;
    private BigInteger historyIdFromLastSync;

    public GmailComponent() throws IOException, GeneralSecurityException {
        getService();
        readHistoryIdLastSyncFromFile();
    }

    private static Gmail getService() throws IOException, GeneralSecurityException {
        if (service == null) {
            setService(initializeService());
        }
        return service;
    }

    private static Gmail initializeService() throws IOException, GeneralSecurityException {
        return GmailConnector.initialize();
    }

    private static void setService(Gmail ins) {
        service = ins;
    }

    private BigInteger getHistoryIdLatest() {
        return historyIdLatest;
    }


    private BigInteger getHistoryIdFromLastSync() {
        return historyIdFromLastSync;
    }

    private void setHistoryIdFromLastSync(BigInteger id) {
        historyIdFromLastSync = id;
    }

    private void setHistoryIdLatest(BigInteger id) {
        historyIdLatest = id;
    }

    @Override
    public void storeHistoryIdFromLastSync() throws IOException {
        System.out.println("storeHistoryIdFromLastSync: " + getHistoryIdFromLastSync());

        BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_ID_FILE));
        writer.write(String.valueOf(this.getHistoryIdFromLastSync()));
        writer.close();
    }

    private void readHistoryIdLastSyncFromFile () {
        BigInteger in = readFileFirstLine(HISTORY_ID_FILE);
        System.out.println(in);
        setHistoryIdFromLastSync(readFileFirstLine(HISTORY_ID_FILE));
    }

    private BigInteger readFileFirstLine (String fileName) {
        BigInteger output = null;
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            if (line != null) {
                Integer in = Integer.parseInt(line);
                output = BigInteger.valueOf(in);
            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
        }
        return output;
    }

    public MessageData getMessageData(BigInteger idLatest) throws IOException {
        MessageData messageData = new MessageData();
        List<String> messageAddedSnippets = new ArrayList<String>();
        HashMap<String, String> recipients = new HashMap<String, String>();
        HashMap<String, String> sender = new HashMap<String, String>();

        try {
            onHistoryIdSynced(idLatest);
            List<String> ids = HistoryResource.getMessageAddedIds
                    (getService(), getHistoryIdFromLastSync());
            System.out.println("messageAddedIds: " + ids);

            List<MessageType> messages = (List<MessageType>)MessagesResource.getMessages
                    (getService(), ids);
            for (MessageType message : messages) {
                System.out.println("message: " + message.getSnippet());
                if (message.getLabelIds().contains("INBOX") && !message.getLabelIds().contains("SENT")) {
                    messageAddedSnippets.add(message.getSnippet());
                    for (MessagePartHeader header : message.getPayload().getHeaders()){
                        if (header.getName().equalsIgnoreCase("From")) {
                            System.out.println("Sender: " + header.getValue());
                            String emailList = header.getValue();
                            sender = processSenderAndReciever(sender, emailList);

                        }
                        if (header.getName().equalsIgnoreCase("To")) {
                            System.out.println("Recipient: " + header.getValue());
                            String recipientList = header.getValue();
                            recipients = processSenderAndReciever(recipients, recipientList);
                        }
                        if (header.getName().equalsIgnoreCase("Subject")) {
                            messageData.setSubject(header.getValue());
                        }
                        if (header.getName().equalsIgnoreCase("Message-ID")) {
                            messageData.setMessageID(header.getValue());
                        }
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            onMessagesSynced();
        }
        messageData.setMessageAddedSnippets(messageAddedSnippets);
        messageData.setRecipients(recipients);
        messageData.setSender(sender);
        return messageData;
    }

    private HashMap<String, String> processSenderAndReciever(HashMap<String, String> list, String emailList){
        Pattern fullPattern = Pattern.compile("((\\w+.*?)(\\<?([\\w.+]+@[\\w]+.[\\w]{2,4})\\>?))");
        Matcher fullMatcher = fullPattern.matcher(emailList);
        while (fullMatcher.find()) {
            String matched = fullMatcher.group(1);
            String email , name;
            if (matched.contains("<")){
                name = fullMatcher.group(2).trim();
                name = name.replace("\"", "");
                email = fullMatcher.group(4);
            } else {
                email = matched;
                name = email;
            }
            list.put(name, email);
        }
        return list;
    }

    private void onHistoryIdSynced(BigInteger historyId){
        setHistoryIdLatest(historyId);
    }

    private void onMessagesSynced() throws IOException {
        setHistoryIdFromLastSync(this.getHistoryIdLatest());
        storeHistoryIdFromLastSync();
    }

    public MessageType getMessage(String messageId) {
        MessageType msg = null;
        try {
           msg = (MessageType)MessagesResource.getMessage(getService(), messageId);
        } catch (Exception e){
           e.printStackTrace();
        }
        return msg;
    }

    public MimeMessage createEmail(String to,String from,MessageData messageData,String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));

        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(messageData.getSubject());
        email.setContent(bodyText, "text/html; charset=utf-8");
        email.addHeader("In-Reply-To", messageData.getMessageID());
        return email;
    }

    public Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public Message sendMessage(String userId, MimeMessage emailContent)
            throws MessagingException, IOException, GeneralSecurityException {
        Gmail service = getService();
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

    @Override
    public StringBuilder generateEmail(TimePreferences pref, String guestName, String ownerName) {
        return EmailBuilder.generateEmail(pref, guestName, ownerName);
    }

    @Override
    public StringBuilder generateConfirmation(TimeOption option, String name) {
        return EmailBuilder.generateConfirmation(option, name);
    }

    @Override
    public StringBuilder generateWelcomeEmail() {
        return EmailBuilder.generateWelcomeEmail();
    }

    public WatchResponseType registerMailboxNotification(String topicName){
        WatchResponseType response = null;
        try {
            response = (WatchResponseType)UsersResource.watch(getService(), topicName);
        } catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}