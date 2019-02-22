package mail.service.gmail;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.History;
import com.google.api.services.gmail.model.HistoryMessageAdded;
import com.google.api.services.gmail.model.ListHistoryResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class HistoryResource {
    private static final String userId = "me";

    protected static List<History> list(Gmail service, BigInteger historyId) throws IOException {
        List<History> histories = new ArrayList<>();
        ListHistoryResponse response = service.users().history().list(userId)
                .setStartHistoryId(historyId).execute();
        while (response.getHistory() != null) {
            histories.addAll(response.getHistory());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().history().list(userId).setPageToken(pageToken)
                        .setStartHistoryId(historyId).execute();
            } else {
                break;
            }
        }

        return histories;
    }

    protected static List<String> getMessageIds (List<History> histories) {
        List<String> messageIds = new ArrayList<String>();
        for (History history : histories) {
            List<HistoryMessageAdded> messagesAdded = history.getMessagesAdded();
            if (messagesAdded != null ) {
                for (HistoryMessageAdded message : messagesAdded) {
                    messageIds.add(message.getMessage().getId());
                }
            }
        }
        return messageIds;
    }


    protected static List<String> getMessageAddedIds(List<History> histories) {
        List<String> messageIds = new ArrayList<String>();
        for (History history : histories) {
            List<HistoryMessageAdded> messagesAdded = history.getMessagesAdded();
            if (messagesAdded != null ) {
                for (HistoryMessageAdded message : messagesAdded) {
                    messageIds.add(message.getMessage().getId());
                }
            }
        }
        return messageIds;
    }

    protected static List<String> getMessageIds (Gmail service, BigInteger historyId) throws IOException {
        List<History> historyList = list(service, historyId);
        return getMessageIds(historyList);
    }

    protected static List<String> getMessageAddedIds(Gmail service, BigInteger historyId) throws IOException {
        List<History> historyList = list(service, historyId);
        return getMessageAddedIds(historyList);
    }
}