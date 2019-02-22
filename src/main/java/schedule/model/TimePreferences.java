package schedule.model;

import com.google.protobuf.Value;

import java.util.List;
import java.util.Map;

public class TimePreferences {
  Map<String, Value> dateTimeMap;
  String dateTimeValue;
  List<Value> dates;

  Map<String, Value> datePeriodMap;
  Map<String, Value> timePeriodMap;
  String timeValue;

  List<TimeOption> timeOptions;

  public List<TimeOption> getTimeOptions() {
    return timeOptions;
  }

  public void setTimeOptions(List<TimeOption> timeOptions) {
    this.timeOptions = timeOptions;
  }

  public Map<String, Value> getDateTimeMap() {
    return dateTimeMap;
  }

  public void setDateTimeMap(Map<String, Value> dateTimeMap) {
    this.dateTimeMap = dateTimeMap;
  }

  public String getDateTimeValue() {
    return dateTimeValue;
  }

  public void setDateTimeValue(String dateTimeValue) {
    this.dateTimeValue = dateTimeValue;
  }

  public List<Value> getDates() {
    return dates;
  }

  public void setDates(List<Value> dates) {
    this.dates = dates;
  }

  public Map<String, Value> getDatePeriodMap() {
    return datePeriodMap;
  }

  public void setDatePeriodMap(Map<String, Value> datePeriodMap) {
    this.datePeriodMap = datePeriodMap;
  }

  public Map<String, Value> getTimePeriodMap() {
    return timePeriodMap;
  }

  public void setTimePeriodMap(Map<String, Value> timePeriodMap) {
    this.timePeriodMap = timePeriodMap;
  }

  public String getTimeValue() {
    return timeValue;
  }

  public void setTimeValue(String timeValue) {
    this.timeValue = timeValue;
  }
}
