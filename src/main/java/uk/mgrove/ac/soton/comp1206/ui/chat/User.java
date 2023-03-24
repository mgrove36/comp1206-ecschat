package uk.mgrove.ac.soton.comp1206.ui;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Class for a user, which is an element to be displayed in the chat window
 */
public class User extends VBox {

  private final String username;
  private final IntegerProperty messageCount;

  /**
   * Initialise the user with a username
   * @param username username of user
   */
  public User(String username) {
    setSpacing(2);
    setPadding(new Insets(0, 12,0,12));
    setFillWidth(true);
    this.username = username;
    this.messageCount = new SimpleIntegerProperty(1);
    var messageCountLabel = new Label("");
    messageCountLabel.getStyleClass().add("italic");
    messageCountLabel.setWrapText(true);
    messageCountLabel.textProperty().bind(Bindings.concat(messageCount.asString()," message(s)"));
    var usernameLabel = new Label(username);
    usernameLabel.setWrapText(true);
    getChildren().addAll(
        usernameLabel,
        messageCountLabel
    );
  }

  /**
   * Get property for number of messages sent by user
   * @return property
   */
  public IntegerProperty messageCountProperty() {
    return messageCount;
  }

  /**
   * Get user's username
   * @return username
   */
  public String getUsername() {
    return username;
  }

}
