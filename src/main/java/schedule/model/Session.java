package schedule.model;

import java.util.List;

public class Session {
  private List<TimeOption> timeOptions;
  private List<Person> guests;
  private Person owner;

  public List<Person> getGuests() {
    return guests;
  }

  public void setGuests(List<Person> guests) {
    this.guests = guests;
  }

  public Person getOwner() {
    return owner;
  }

  public void setOwner(Person owner) {
    this.owner = owner;
  }

  public List<TimeOption> getTimeOptions() {
    return timeOptions;
  }

  public void setTimeOptions(List<TimeOption> timeOptions) {
    this.timeOptions = timeOptions;
  }
}