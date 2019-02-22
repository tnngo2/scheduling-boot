package nlp;

import com.google.protobuf.Value;
import nlp.service.dialogflow.IntentData;

import java.util.List;
import java.util.Map;

public interface NLPComponent {
    public IntentData detectIntentTexts(List<String> sentences) throws Exception;
}
