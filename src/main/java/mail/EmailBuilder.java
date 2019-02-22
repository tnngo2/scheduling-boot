package mail;

import com.google.protobuf.Value;
import schedule.model.TimeOption;
import schedule.model.TimePreferences;
import java.util.*;

import static datetime.DateTime.*;

public class EmailBuilder {
  public static StringBuilder generateEmail(TimePreferences pref, String guestName, String ownerName) {
    StringBuilder content = new StringBuilder();
    getEmailMessageBeginning(content, guestName, ownerName);

    if (! isEmpty(pref.getDateTimeValue())) {
      getEmailMessageForDateTimeValue(content, pref.getDateTimeValue());
    } else if (! isEmpty(pref.getDates())) {
      getEmailMessageForDates(content, pref.getTimeOptions());
    } else if (! isEmpty(pref.getDatePeriodMap())) {
      getEmailMessageForDatePeriod(content, pref.getTimeValue(), pref.getTimePeriodMap(), pref.getDatePeriodMap());
    } else if (! isEmpty(pref.getDateTimeMap())) {
      getEmailMessageForDateTimeMap(content, pref.getDateTimeMap());
    }

    getEmailMessageEnding(content);
    return content;
  }

  public static StringBuilder generateWelcomeEmail () {
    StringBuilder content = new StringBuilder();

    Random rand = new Random();
    int choice = (rand.nextInt(50) % 4 );

    switch (choice) {
      case 0 :
        content.append("You're welcome.");
        break;
      case 1:
        content.append("Don't mention it.");
        break;
      case 2:
        content.append("it's nothing.");
        break;
      case 3:
        content.append("it's no bother.");
        break;
    }

    appendSignature(content);
    return content;
  }

  private static void appendSignature(StringBuilder content){
    content.append("<br/><br/>Cheers,<br/>");
    content.append("Fuji<br/>");
    content.append("An AI scheduling bot from Fuji Xerox.");
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

  private static void getEmailMessageForDateTimeValue(StringBuilder content, String dateTimeValue) {
    getEmailMessageOptionText(content, false);
    content.append(formatTime(dateTimeValue));
    content.append(" ");
    content.append(formatDate(dateTimeValue));
    content.append(".<br/>");
    getEmailMessagePreference(content, false);
  }

  private static void getEmailMessageForDateTimeMap(StringBuilder content, Map<String, Value>  dateTimeMap) {
    getEmailMessageOptionText(content, true);

    Value startDateTime = dateTimeMap.get("startDateTime");
    Value endDateTime = dateTimeMap.get("endDateTime");

    String timeValue = null;
    if (!isEmpty(startDateTime)) {
      timeValue = startDateTime.getStringValue();
    }

    appendDatePeriod(content, timeValue, startDateTime, endDateTime);

    content.append(".<br/>");
    getEmailMessagePreference(content, true);
  }

  private static void getEmailMessageForDates(StringBuilder content, List<TimeOption> timeOptions) {
    getEmailMessageOptionText(content, (timeOptions.size() > 1));

    for (int i = 0; i < timeOptions.size() && i < 2; i ++) {
      if (i > 0) {
        content.append(" or ");
      }
      content.append("<b>");
      content.append(timeOptions.get(i).getTime());
      content.append("</b>");
    }

    content.append(".<br/>");
    getEmailMessagePreference(content,  (timeOptions.size() > 1));
  }

  private static void getEmailMessageOptionText(StringBuilder content, Boolean multiple) {
    if (multiple) {
      Random rand = new Random();
      int choice = (rand.nextInt(50) % 3 );

      switch (choice) {
        case 0 :
          content.append("There are two options either : ");
          break;
        case 1:
          content.append("Are you free either ? ");
          break;
        case 2:
          content.append("<br/>Does the following options work? <br/>");
          break;
      }

    } else {
      content.append("Here is the option: ");
    }
  }

  private static void getEmailMessageForDatePeriod(StringBuilder content, String timeValue,
                                                   Map<String, Value> timePeriodMap, Map<String, Value>  date_period) {
    getEmailMessageOptionText(content, true);
    appendTime(content, timeValue);

    Value startDate = date_period.get("startDate");
    Value endDate = date_period.get("endDate");
    appendDatePeriod(content, timeValue, startDate, endDate);
    appendTimePeriod(content, timeValue, timePeriodMap);
    content.append(".<br/>");
  }

  private static void appendDatePeriod(StringBuilder content, String timeValue, Value startDate, Value endDate) {
    List<String> options = getDatePeriodOptions(timeValue, startDate, endDate);
    content.append(options.get(0));
    content.append(" or ");
    content.append(options.get(1));
  }

  private static List<String> getDatePeriodOptions(String timeValue, Value startDate, Value endDate){
    List<String> options = new ArrayList<String>();
    StringBuilder opt = new StringBuilder();

    if (startDate != null) {
      appendTime(opt, timeValue);
      opt.append(formatDate(startDate.getStringValue()));
      options.add(opt.toString());

      opt = new StringBuilder();
      appendTime(opt, timeValue);
      opt.append(formatDate(startDate.getStringValue(), 1));
      options.add(opt.toString());
    } else if (endDate != null) {
      appendTime(opt, timeValue);
      opt.append(formatDate(endDate.getStringValue(), -1));
      options.add(opt.toString());

      opt = new StringBuilder();
      appendTime(opt, timeValue);
      opt.append(formatDate(endDate.getStringValue()));
      options.add(opt.toString());
    }
    return options;
  }

  private static void getEmailMessageBeginning(StringBuilder content, String guestName, String ownerName) {
    content.append("Hi ");
    content.append(guestName);
    Random rand = new Random();
    int choice = (rand.nextInt(50) % 3 );
    switch (choice) {
      case 0 :
        content.append(",<br/><br/>My pleasure to arrange a meeting for you and " + ownerName + ".<br/>");
        break;
      case 1:
        content.append(",<br/><br/>Happy to get something on " + ownerName + "\'s calendar.<br/>");
        break;
      case 2:
        content.append(",<br/><br/>Great to find out some available time slots from " + ownerName + "\'s calendar.<br/>");
        break;
    }
  }

  private static void getEmailMessagePreference(StringBuilder content, Boolean multiple) {
    if (multiple) {
      Random rand = new Random();
      int choice = (rand.nextInt(50) % 3 );

      switch (choice) {
        case 0 :
          content.append("Please let me know your preference.<br/>");
          break;
        case 1:
          content.append("The choice is yours.<br/>");
          break;
        case 2:
          content.append("May I have your availability?<br/>");
          break;
      }
    } else {
      content.append("Please let me know if it's OK for you.<br/>");
    }
  }

  private static void getEmailMessageEnding(StringBuilder content) {
    Random rand = new Random();
    int choice = (rand.nextInt(50) % 3 );

    switch (choice) {
      case 0 :
        content.append("<br/>Thank you,<br/>");
        break;
      case 1:
        content.append("<br/>Thanks,<br/>");
        break;
      case 2:
        content.append("<br/>Looking forward for your reply,<br/>");
        break;
    }

    appendSignature(content);
  }

  public static StringBuilder generateConfirmation(TimeOption option, String name) {
    StringBuilder content = new StringBuilder();
    content.append("Hi "+ name +", <br/><br/>");
    Random rand = new Random();
    int choice = (rand.nextInt(50) % 3 );

    switch (choice) {
      case 0 :
        content.append("I have received your preference. I will send the meeting request : ");
        break;
      case 1:
        content.append("Noted with thanks. The meeting request will be sent in a few minutes with the timing : ");
        break;
      case 2:
        content.append("Thank you for your reply. Will send the meeting request with : ");
        break;
    }

    content.append("<b>");
    content.append(option.getTime());
    content.append("</b>");
    content.append(".");

    appendSignature(content);
    return content;
  }

  private static void appendTimePeriod(StringBuilder content, String timeValue, Map<String, Value> timePeriodMap) {
    if (isEmpty(timeValue) && !isEmpty(timePeriodMap)) {
      if (!isEmpty(timePeriodMap.get("startTime"))) {
        content.append(" from ");
        content.append(formatTime(timePeriodMap.get("startTime").getStringValue()));

        if (!isEmpty(timePeriodMap.get("endTime"))) {
          content.append(" to ");
          content.append(formatTime(timePeriodMap.get("endTime").getStringValue()));
        }
      } else if (!isEmpty(timePeriodMap.get("endTime"))) {
        content.append(" before ");
        content.append(formatTime(timePeriodMap.get("endTime").getStringValue()));
      }
    }
  }

  private static void appendTime(StringBuilder content, String timeValue) {
    if (!isEmpty(timeValue)) {
      content.append(formatTime(timeValue));
      content.append(" ");
    }
  }
}
