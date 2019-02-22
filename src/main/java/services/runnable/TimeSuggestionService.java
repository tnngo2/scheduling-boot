package services.runnable;

import com.google.cloud.dialogflow.v2.Intent;
import com.google.protobuf.Value;
import nlp.NLPComponent;
import nlp.service.dialogflow.DialogFlowComponent;
import nlp.service.dialogflow.IntentData;
import schedule.ScheduleComponent;
import schedule.ScheduleComponentImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimeSuggestionService {
    public static void main(String... args) throws Exception {
        NLPComponent nlpComponent = new DialogFlowComponent();
        ScheduleComponent scheduleComponent = new ScheduleComponentImpl();

        showMessagesSuggested(nlpComponent, scheduleComponent,
                "I am free before 11:00 pm Tuesday or Friday");

        showMessagesSuggested(nlpComponent, scheduleComponent,
                "I prefer the meeting time at 10:00pm Monday or Tuesday.");

        showMessagesSuggested(nlpComponent, scheduleComponent,
                "I prefer the timing at 11:00 am next Monday to Wednesday.");
    }

    private static void showMessagesSuggested(NLPComponent nlpComponent, ScheduleComponent scheduleComponent, String sentence) throws Exception {
        List<String> sentences = new ArrayList<String>();
        sentences.add(sentence);
        IntentData intentData = nlpComponent.detectIntentTexts(sentences);
        List<Map<String, Value>> preferences = intentData.getParameters();
//        StringBuilder message = scheduleComponent.getMessageTimeSuggested(preferences, "");
//        System.out.println(message);
    }
}
