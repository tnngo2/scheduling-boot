package mail.service.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.WatchRequest;
import com.google.api.services.gmail.model.WatchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsersResource {
    private static final String userId = "me";
    protected static WatchResponse watch(Gmail service, String topicName) throws IOException {
        WatchRequest req = new WatchRequest();
        req.setTopicName(topicName);
        List<String> labelIds= new ArrayList<String>();
        labelIds.add("INBOX");
        req.setLabelIds(labelIds);


        WatchResponse response = service.users().watch(userId, req).execute();
        return response;
    }
}