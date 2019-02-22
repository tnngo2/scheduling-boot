package schedule;

import com.google.protobuf.Value;
import schedule.model.TimePreferences;

import java.util.List;
import java.util.Map;

public class ScheduleComponentImpl implements  ScheduleComponent{
    @Override
    public TimePreferences getTimePreferences(List<Map<String, Value>> preferences) {
        return TimeSuggestion.getTimePreferences(preferences);
    }
}
