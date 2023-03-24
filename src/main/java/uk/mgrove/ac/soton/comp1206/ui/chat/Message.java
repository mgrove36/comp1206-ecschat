package uk.mgrove.ac.soton.comp1206.ui.chat;

import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import uk.mgrove.ac.soton.comp1206.App;
import uk.mgrove.ac.soton.comp1206.utility.Utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;

/**
 * Class for a message, which displays the message in a formatted way
 */
public class Message extends TextFlow {

  private final App app;
  private final String username;
  private final String message;
  private final LocalDateTime received;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

  /**
   * Initialise the message with basic info
   * @param app the app
   * @param username username of user who sent the message
   * @param message the message contents
   */
  public Message(App app, String username, String message) {
    this.app = app;
    this.username = username;
    this.message = message;
    received = LocalDateTime.now();
    build();
  }

  /**
   * Build the message
   */
  public void build() {
    Text timeField = new Text("[" + formatter.format(received) + "] ");
    timeField.getStyleClass().add("timestamp");
    Text usernameField = new Text(username + ": ");
    usernameField.getStyleClass().add("username");
    getChildren().addAll(timeField,usernameField);

    Matcher urlMatcher = Utility.getUrlPattern().matcher(message);
    var previousMatchEnd = 0;
    while (urlMatcher.find()) {
      if (urlMatcher.start() > previousMatchEnd) {
        getChildren().add(new Text(message.substring(previousMatchEnd, urlMatcher.start())));
      }
      var url = message.substring(urlMatcher.start(), urlMatcher.end());
      var urlField = new Hyperlink(url);
      urlField.setOnAction((event) -> app.getHostServices().showDocument(url));
      getChildren().add(urlField);
      previousMatchEnd = urlMatcher.end();
    }
    if (message.length() > previousMatchEnd) {
      getChildren().add(new Text(message.substring(previousMatchEnd)));
    }

    getStyleClass().add("message-container");
  }

}
