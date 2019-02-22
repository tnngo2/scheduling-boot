package nlp.service.dialogflow;

import com.google.protobuf.Value;

import java.util.List;
import java.util.Map;

public class IntentData {
    private String intent;
    private List<Map<String, Value>> parameters;

    public IntentData(){

    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public List<Map<String, Value>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map<String, Value>> parameters) {
        this.parameters = parameters;
    }
}
