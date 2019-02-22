package services.runnable;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.protobuf.ByteString;
import com.google.protobuf.Value;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import mail.EmailComponent;
import mail.service.gmail.MessageData;
import nlp.NLPComponent;
import nlp.service.dialogflow.DialogFlowComponent;
import nlp.service.dialogflow.IntentData;
import org.json.*;
import mail.service.gmail.GmailComponent;
import schedule.ScheduleComponent;
import schedule.ScheduleComponentImpl;
import schedule.SessionManager;
import schedule.model.Person;
import schedule.model.Session;
import schedule.model.TimeOption;
import schedule.model.TimePreferences;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class SubscriberMailboxService {

    // use the default project id
    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private static final BlockingQueue<PubsubMessage> messages = new LinkedBlockingDeque<>();
    private static JSONObject response;

    static class MessageReceiverExample implements MessageReceiver {

        @Override
        public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
            messages.offer(message);
            consumer.ack();
        }
    }

    /**
     * Convert response from ByteString to JSONObject
     * @param _response
     */
    private static void parseResponse(ByteString _response) {
        JSONObject json = new JSONObject(_response.toStringUtf8());
        response = json;
    }

    private static Object getResponse(String key) {
        return response.get(key);
    }

    private static BigInteger getHistoryId () {
        BigInteger historyId = BigInteger.valueOf((Integer)getResponse("historyId"));
        return historyId;
    }

    /** Receive messages over a subscription. */
    public static void main(String... args) throws Exception {
        String subscriptionId = "mail-pull";
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(
                PROJECT_ID, subscriptionId);
        Subscriber subscriber = null;
        EmailComponent emailComponent = new GmailComponent();
        ScheduleComponent scheduleComponent = new ScheduleComponentImpl();
        NLPComponent nlpComponent = new DialogFlowComponent();
        SessionManager sessionManager = new SessionManager();

        try {
            // create a subscriber bound to the asynchronous message receiver
            subscriber =
                    Subscriber.newBuilder(subscriptionName, new MessageReceiverExample()).build();
            subscriber.startAsync().awaitRunning();
            // Continue to listen to messages
            while (true) {
                PubsubMessage message = messages.take();
                parseResponse(message.getData());

                // 1. Get new email message
                MessageData messageData = emailComponent.getMessageData(getHistoryId());
                List<String> messages = messageData.getMessageAddedSnippets();

                // 2. Extract intent from email
                IntentData intentData = nlpComponent.detectIntentTexts(messages);
                String intent = intentData.getIntent();
                List<Map<String, Value>> parameters = intentData.getParameters();
                if (intent == null)  continue;

                // 3. Extract TimePreference from intent
                TimePreferences pref = new TimePreferences();

                if (intent != null && intent.equalsIgnoreCase("Preference")) {
                    Session session = sessionManager.initializeSession();
                    Person owner;
                    List<Person> guests = new ArrayList<Person>();
                    for (HashMap.Entry<String,String> item : messageData.getSender().entrySet()) {
                        owner = new Person(item.getKey(), item.getValue());
                        session.setOwner(owner);
                    }

                    for (HashMap.Entry<String,String> item : messageData.getRecipients().entrySet()) {
                        Person guest = new Person(item.getKey(), item.getValue());
                        guests.add(guest);
                    }
                    session.setGuests(guests);
                    pref = scheduleComponent.getTimePreferences(parameters);
                    session.setTimeOptions(pref.getTimeOptions());
                }

                // 4. Prepare and send email
                StringBuilder emailContent = new StringBuilder();
                Session currentSession = sessionManager.getCurrentSession();
                if (currentSession == null) continue;

                for (Person guest: currentSession.getGuests()) {
                    switch(intent) {
                        case "Preference":
                            emailContent = emailComponent.generateEmail(pref, guest.getName(), currentSession.getOwner().getName());
                            break;
                        case "Confirmation":
                            Map<String, Value> firstParam = parameters.get(0);
                            int choice = 1;
                            if (! firstParam.get("number").getStringValue().isEmpty()) {
                                choice = (int) (Math.round(firstParam.get("number").getNumberValue()));
                            } else if (! firstParam.get("ordinal").getStringValue().isEmpty()) {
                                choice = (int) (Math.round(firstParam.get("ordinal").getNumberValue()));
                            } else if (! firstParam.get("OrdinalExpression").getStringValue().isEmpty()) {
                                String ordinal = firstParam.get("OrdinalExpression").getStringValue();
                                choice = (ordinal.equalsIgnoreCase("sooner")) ? 1 : 2;
                            }

                            // Sometime, the number param is easily set wrongly because of the email content contain digit.
                            if (choice > 2) {
                                choice = 1;
                            }
                            System.out.println("choice = " +choice);
                            TimeOption chosen = currentSession.getTimeOptions().get(choice - 1);
                            emailContent = emailComponent.generateConfirmation(chosen, guest.getName());
                            break;
                        case "Thanks":
                            emailContent = emailComponent.generateWelcomeEmail();
                    }

                    // TBD: add unknown intent case
                    if (!guest.getEmail().equalsIgnoreCase("plismteam@gmail.com")) {
                        MimeMessage email = emailComponent.createEmail(
                            guest.getEmail(), "plismteam@gmail.com", messageData, emailContent.toString());
                        emailComponent.sendMessage("me", email);
                    }
                }

                // TBD: Send meeting request
                if (intent.equalsIgnoreCase("Thanks")) {
                    sessionManager.destroySession();
                }
            }
        } finally {
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }

}