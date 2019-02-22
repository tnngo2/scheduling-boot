package mail.service.gmail;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

public class MessagesResource {
    private static final String userId = "me";

    protected static Message getMessage(Gmail service, String messageId)
            throws IOException {
        Message message = null;
        try {
            message = service.users().messages().get(userId, messageId).execute();
        } catch (IOException e) {
            System.out.println("Cannot getMessage messageId:" + messageId);
            e.printStackTrace();
        }
        return message;
    }

    protected static List<Message> getMessages(Gmail service, List<String> ids)
            throws IOException {
        List<Message> messages = new ArrayList<Message>();
        Message msg;
        for (String id : ids) {
            msg = getMessage(service, id);
            if (msg != null) {
                messages.add(msg);
            }

        }
        return messages;
    }

//
//    public static ByteArrayOutputStream getByteArray(String data) {
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
//
//        try {
//            out.write(data.getBytes());
//            byteArrayOutputStream.flush();
//            byteArrayOutputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return byteArrayOutputStream;
//
//    }

}
