package nlp.service.dialogflow;

import com.google.cloud.dialogflow.v2.*;
import com.google.protobuf.Value;
import nlp.NLPComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DialogFlowComponent implements NLPComponent {
    private String USER_AGENT = "Mozilla/5.0";
    private String SESSION_ID = "123456789123456789123456789123456789";
    private String PROJECT_ID = "meeting-bot-1535683525121";
    private String LANG_CODE = "en";

    @Override
    public IntentData detectIntentTexts(List<String> texts) throws IOException {
        IntentData intentData = new IntentData();
        String intent;
        List<Map<String, Value>> parameters = new ArrayList<Map<String, Value>>();
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(PROJECT_ID, SESSION_ID);
            System.out.println("Session Path: " + session.toString());

            // Detect intents for each text input
            for (String text : texts) {
                // Set the text (hello) and language code (en-US) for the query
                TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(LANG_CODE);

                // Build the query with the TextInput
                QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

                // Performs the detect intent request
                DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

                // Display the query result
                QueryResult queryResult = response.getQueryResult();

                System.out.println("====================");
                System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
                System.out.format("Detected Intent: %s (confidence: %f)\n",
                        queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
                intentData.setIntent(queryResult.getIntent().getDisplayName());
                System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
                System.out.format("Fulfillment Text: '%s'\n", queryResult.getParameters());
                parameters.add(queryResult.getParameters().getFieldsMap());
                intentData.setParameters(parameters);
            }
        }
        return intentData;
    }
}