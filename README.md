# Mail Agent
This project is the back-end service to serve a scheduling bot based on NLP (Dialog Flow) written in Java, Spring Boot.

# Install
- Install [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- Install [gradle](https://gradle.org/install/)
- Install [git](https://git-scm.com/downloads)
- Clone the project.
```
git clone git@13.198.103.232:smart_assistant/mail_agent.git
```
- Build the project.
```
gradle build clean
```
- Add a system variable into your Environment Variables  
Name: `GOOGLE_APPLICATION_CREDENTIALS`   
Value: `mail_agent\tokens\meeting-bot-1535683525121-b52a1b368a97.json`
Note: You have to change value according to your folder path.

# Run:

1. Register Mailbox's notification.
```
gradle registerMailboxNotification
```

2. Start the service to listen Mailbox's notification.
Note: This service must be always live. Do not stop this service.
```
gradle subcribeMailbox
```