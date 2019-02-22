package schedule;

import com.google.protobuf.Value;
import schedule.model.TimePreferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ScheduleComponent {
    public TimePreferences getTimePreferences(List<Map<String, Value>> preferences);
}
