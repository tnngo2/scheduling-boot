package schedule;

import com.google.protobuf.Value;
import schedule.model.TimeOption;
import schedule.model.TimePreferences;

import java.util.*;

import static datetime.DateTime.formatDate;
import static datetime.DateTime.formatTime;

public class TimeSuggestion {

    protected static TimePreferences getTimePreferences(List<Map<String, Value>> preferences) {
        Map<String, Value> firstParam = preferences.get(0);
        TimePreferences timePref = new TimePreferences();

        String dateTimeValue = null;
        Map<String, Value> dateTimeMap = null;

        if (firstParam.get("date-time") != null) {
            dateTimeValue = firstParam.get("date-time").getStringValue();
            dateTimeMap = firstParam.get("date-time").getStructValue().getFieldsMap();

            if (isEmpty(dateTimeValue) && !isEmpty(dateTimeMap)) {
                if (!isEmpty(dateTimeMap.get("date_time"))) {
                    dateTimeValue = dateTimeMap.get("date_time").getStringValue();
                    dateTimeMap = null;
                }
            }
        }

        List<Value> dates = firstParam.get("date").getListValue().getValuesList();

        String timeValue = firstParam.get("time").getStringValue();
        List<Value> times = firstParam.get("time").getListValue().getValuesList();
        if (isEmpty(timeValue) && !isEmpty(times)) {
            timeValue = times.get(0).getStringValue();
        }

        Map<String, Value> datePeriodMap = firstParam.get("date-period").getStructValue().getFieldsMap();
        if (isEmpty(datePeriodMap) && !isEmpty(dateTimeMap)) {
            if (!isEmpty(dateTimeMap.get("startDate")) ||
                !isEmpty(dateTimeMap.get("endDate"))) {
                datePeriodMap = dateTimeMap;
                dateTimeMap = null;
            }
        }

        Map<String, Value> timePeriodMap = firstParam.get("time-period").getStructValue().getFieldsMap();
        if (isEmpty(timePeriodMap) && !isEmpty(dateTimeMap)) {
            if (!isEmpty(dateTimeMap.get("startTime")) ||
                !isEmpty(dateTimeMap.get("endTime"))) {
                timePeriodMap = dateTimeMap;
                dateTimeMap = null;
            }
        }

        timePref.setDateTimeValue(dateTimeValue);
        timePref.setDateTimeMap(dateTimeMap);
        timePref.setDatePeriodMap(datePeriodMap);
        timePref.setDates(dates);
        timePref.setTimePeriodMap(timePeriodMap);
        timePref.setTimeValue(timeValue);

        if (! isEmpty(timePref.getDateTimeValue())) {
//            getEmailMessageForDateTimeValue(content, pref.getDateTimeValue());
        } else if (! isEmpty(timePref.getDates())) {
            timePref.setTimeOptions(getDateOptions(timePref));
//            getEmailMessageForDates(content, pref.getTimeValue(), pref.getTimePeriodMap(), pref.getDates());
        } else if (! isEmpty(timePref.getDatePeriodMap())) {
//            getEmailMessageForDatePeriod(content, pref.getTimeValue(), pref.getTimePeriodMap(), pref.getDatePeriodMap());
        } else if (! isEmpty(timePref.getDateTimeMap())) {
//            getEmailMessageForDateTimeMap(content, pref.getDateTimeMap());
        }
        return timePref;
    }

    private static List<TimeOption> getDateOptions(TimePreferences pref){
        List<TimeOption> options = new ArrayList<>();
        StringBuilder opt;
        TimeOption option;
        List<String> timeSlots = getTimeSlots(pref.getTimeValue(), pref.getTimePeriodMap());
        List<String> dateSlots = getDateSlots(pref.getDates(), pref.getDatePeriodMap());

        for (int i = 0; i < 2; i ++) {
            String timePresent = (timeSlots.size() == 1) ? timeSlots.get(0) : timeSlots.get(i);
            String datePresent = (dateSlots.size() == 1) ? dateSlots.get(0) : dateSlots.get(i);

            option = new TimeOption();
            opt = new StringBuilder();
            opt.append(datePresent);
            opt.append(" at " + timePresent);
            option.setTime(opt.toString());
            options.add(option);
        }
        return options;
    }

    private static List<String> getDateSlots(List<Value> dates ,Map<String, Value> datePeriodMap) {
        List<String> slots = new ArrayList<String>();
        if (!isEmpty(dates)) {
            for (int i = 0; i < dates.size() && i < 2; i ++) {
               slots.add(formatDate(dates.get(i).getStringValue()));
            }
        } else if (!isEmpty(datePeriodMap)) {
            if (datePeriodMap.size() == 2 ) {
                slots = Arrays.asList("","");
            }

            for (Map.Entry<String, Value> date : datePeriodMap.entrySet()) {
                int index = (date.getKey().equalsIgnoreCase("startDate")) ?
                    0 : 1;
                slots.set(index, formatTime(date.getValue().getStringValue()));
            }
        }
        return slots;
    }

    private static List<String> getTimeSlots(String time, Map<String, Value> timePeriodMap) {
        List<String> slots = new ArrayList<String>();
        if (!isEmpty(time)) {
            slots.add(formatTime(time));
        } else if (!isEmpty(timePeriodMap)) {
            if (timePeriodMap.size() == 2 ) {
                slots = Arrays.asList("","");
            }

            for (Map.Entry<String, Value> _time : timePeriodMap.entrySet()) {
                int index = (_time.getKey().equalsIgnoreCase("startTime")) ?
                                0 : 1;
                slots.set(index, formatTime(_time.getValue().getStringValue()));
            }
        }
        return slots;
    }

    private static String getDatePresent(String date) {
        String str = "";
        if (!isEmpty(date)) {
            str = formatDate(date);
        }
        return str;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    private static boolean isEmpty(Collection values) {
        return values == null || values.isEmpty();
    }

    private static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    private static boolean isEmpty(Value value) {
        return value == null;
    }
}