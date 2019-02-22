package services.runnable;

import nlp.NLPComponent;
import nlp.service.dialogflow.DialogFlowComponent;

import java.util.ArrayList;
import java.util.List;

public class NLPService {
    public static void main(String... args) throws Exception {
        NLPComponent nlpComponent = new DialogFlowComponent();
        List<String> sentences = new ArrayList<String>();
        sentences.add("I am free before 11:00 pm Tuesday or Friday");
        nlpComponent.detectIntentTexts(sentences);
    }
}
