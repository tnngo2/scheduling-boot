package schedule;

import schedule.model.Session;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {
  List<Session> sessions;

  public SessionManager() {
    this.sessions = new ArrayList<>();
  }

  public List<Session> getSessions() {
    return sessions;
  }

  public void setSessions(List<Session> sessions) {
    this.sessions = sessions;
  }

  public Session initializeSession () {
    Session session = new Session();
    sessions.add(session);
    return session;
  }

  public Session getCurrentSession(){
    return (sessions.size() ==1) ? sessions.get(0) : null;
  }

  public void destroySession() {
    sessions.remove(0);
  }
}
