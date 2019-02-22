package mail.service.gmail;

import java.util.HashMap;
import java.util.List;

public class MessageData {
    private List<String> messageAddedSnippets;
    private HashMap<String, String> recipients;
    private HashMap<String, String> sender;
    private String subject;
    private String messageID;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public MessageData(){
    }
    public List<String> getMessageAddedSnippets() {
        return messageAddedSnippets;
    }

    public void setMessageAddedSnippets(List<String> messageAddedSnippets) {
        this.messageAddedSnippets = messageAddedSnippets;
    }

    public HashMap<String, String> getRecipients() {
        return recipients;
    }

    public void setRecipients(HashMap<String, String> recipients) {
        this.recipients = recipients;
    }

    public HashMap<String, String> getSender() {
        return sender;
    }

    public void setSender(HashMap<String, String> sender) {
        this.sender = sender;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
