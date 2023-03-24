package uk.mgrove.ac.soton.comp1206.ui.chat;

import javafx.animation.FadeTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import uk.mgrove.ac.soton.comp1206.ui.chat.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * List of users to be displayed in chat window
 */
public class UserList extends ScrollPane {

  private final Map<String,IntegerProperty> userMessageCounts = new HashMap<>();
  private final VBox container;
  private final ArrayList<User> userElements = new ArrayList<>();
  private final StringProperty userSearchProperty;

  /**
   * Initialise the list of users
   * @param userSearchProperty property for content of users search field
   */
  public UserList(StringProperty userSearchProperty) {
    setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    setHbarPolicy(ScrollBarPolicy.NEVER);
    getStyleClass().add("user-list");
    setFitToWidth(true);
    container = new VBox();
    container.setSpacing(12);
    container.setFillWidth(true);
    setContent(container);
    this.userSearchProperty = userSearchProperty;
  }

  /**
   * Add user to list of users
   * @param username username of user to add
   */
  public void addUser(String username) {
    if (userMessageCounts.containsKey(username)) {
      var property = userMessageCounts.get(username);
      property.set(property.get() + 1);
    } else {
      var newUser = new User(username);
      userMessageCounts.put(username, newUser.messageCountProperty());

      newUser.setOpacity(0);
      FadeTransition fadeMessage = new FadeTransition(Duration.millis(150), newUser);
      fadeMessage.setToValue(1);
      userElements.add(newUser);
      fadeMessage.play();

      sortUserElements();
      updateDisplayedUsers();
    }
  }

  private void sortUserElements() {
    userElements.sort(Comparator.comparing(User::getUsername));
  }

  /**
   * Update the list of users displayed
   */
  public void updateDisplayedUsers() {
    if (userSearchProperty.get().isBlank()) {
      container.getChildren().setAll(userElements);
    } else {
      container.getChildren().setAll(userElements.stream().filter((user) -> user.getUsername().toLowerCase().contains(userSearchProperty.get().toLowerCase())).toList());
    }
  }

}
