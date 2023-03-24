package uk.mgrove.ac.soton.comp1206.ui.chat;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.mgrove.ac.soton.comp1206.App;
import uk.mgrove.ac.soton.comp1206.network.Communicator;
import uk.mgrove.ac.soton.comp1206.ui.BaseWindow;
import uk.mgrove.ac.soton.comp1206.ui.chat.Message;
import uk.mgrove.ac.soton.comp1206.ui.chat.UserList;
import uk.mgrove.ac.soton.comp1206.utility.Utility;

import java.awt.event.ActionEvent;
import java.util.Arrays;

/**
 * Chat window for displaying chat interface
 */
public class ChatWindow extends BaseWindow {

  private final static Duration messageFadeInDuration = Duration.millis(150);

  @FXML
  private TextField usersSearchField;
  @FXML
  private Text myUsernameText;
  @FXML
  private TextField myUsernameInput;
  @FXML
  private ScrollPane messagesContainer;
  @FXML
  private Label noMessagesYetText;
  @FXML
  private VBox messages;
  @FXML
  private TextField messageToSend;
  @FXML
  private CheckBox audioEnabled;
  @FXML
  private HBox myUsernameInputContainer;
  @FXML
  private HBox myUsernameTextContainer;
  @FXML
  private VBox myUsernameContainer;
  @FXML
  private final UserList userList;
  @FXML
  private VBox rightPanel;
  @FXML
  private VBox leftPanel;
  @FXML
  private SplitPane mainSplitPane;
  @FXML
  private ImageView showLeftPanel;
  @FXML
  private ImageView showRightPanel;
  @FXML
  private ImageView hideLeftPanel;
  @FXML
  private ImageView hideRightPanel;
  @FXML
  private StackPane mainStackPane;
  @FXML
  private Button openGame;
  private boolean messagesReceived;
  private boolean scrollToBottom;
  private boolean lastMessageFromMe;

  /**
   * Initialise the chat window
   * @param app the app
   * @param communicator the communicator
   */
  public ChatWindow(App app, Communicator communicator) {
    super(app, communicator, "/chat.fxml");

    //Link the communicator to this window
    communicator.setWindow(this);

    getScene().addPostLayoutPulseListener(this::scrollToBottom);

    StringProperty userSearchProperty = new SimpleStringProperty("");
    userList = new UserList(userSearchProperty);
    Platform.runLater(() -> rightPanel.getChildren().add(userList));
    usersSearchField.textProperty().bindBidirectional(userSearchProperty);
    usersSearchField.setOnKeyReleased((event) -> {
      // refresh list of people
      userList.updateDisplayedUsers();
    });
    hideMyUsernameInput(false);

    rightPanelPane = mainSplitPane.getItems().get(2);
    leftPanelPane = mainSplitPane.getItems().get(0);
    mainSplitPaneDividerPositions = mainSplitPane.getDividerPositions();
    sidePanelsVisible = new boolean[2];
    Arrays.fill(sidePanelsVisible, Boolean.TRUE);
    mainStackPane.getChildren().removeAll(showLeftPanel, showRightPanel);

    messagesReceived = false;
    myUsernameInput.textProperty().bindBidirectional(app.usernameProperty());
    myUsernameText.textProperty().bind(app.usernameProperty());
    audioEnabled.selectedProperty().bindBidirectional(Utility.audioEnabledProperty());

    openGame.setOnAction((e) -> {
      app.openGame();
    });

    communicator.addListener(this::receiveMessage);

    Platform.runLater(() -> messageToSend.requestFocus());
  }

  /**
   * Handle what happens when the user presses enter on the message to send field
   *
   * @param event key pressed
   */
  @FXML
  protected void handleUsernameKeypress(KeyEvent event) {
    if (event.getCode() != KeyCode.ENTER) return;
    // hide input
    hideMyUsernameInput(true);
    messageToSend.requestFocus();
  }

  /**
   * Handle what happens when the user presses enter on the message to send field
   *
   * @param event key pressed
   */
  @FXML
  protected void handleMessageToSendKeypress(KeyEvent event) {
    if (event.getCode() != KeyCode.ENTER) return;
    sendCurrentMessage(messageToSend.getText());
  }

  /**
   * Handle what happens when the user presses the send message button
   *
   * @param event button clicked
   */
  @FXML
  protected void handleSendMessage(ActionEvent event) {
    String message = messageToSend.getText();
    if (message.isBlank()) return;
    sendCurrentMessage(message);
  }

  /**
   * Handle event to show username input when button pressed
   *
   * @param event button clicked
   */
  public void showMyUsernameInput(MouseEvent event) {
    showMyUsernameInput();
  }

  /**
   * Handle show username input
   */
  public void showMyUsernameInput() {
    myUsernameContainer.getChildren().remove(myUsernameTextContainer);
    myUsernameContainer.getChildren().add(myUsernameInputContainer);
    myUsernameInput.requestFocus();
    myUsernameInput.end();
  }

  /**
   * Handle event to hide username input when button pressed
   *
   * @param event button clicked
   */
  public void hideMyUsernameInput(MouseEvent event) {
    hideMyUsernameInput(true);
  }

  /**
   * Handle hide username input, with option to only remove input container and not add text container
   *
   * @param showTextContainer add text container to window
   */
  private void hideMyUsernameInput(boolean showTextContainer) {
    myUsernameContainer.getChildren().remove(myUsernameInputContainer);
    if (showTextContainer) {
      myUsernameContainer.getChildren().add(myUsernameTextContainer);
      messageToSend.requestFocus();
    }
  }

  /**
   * Handle an incoming message from the Communicator
   *
   * @param text The message that has been received, in the form User:Message
   */
  public void receiveMessage(String text) {
    if (!text.contains(":")) return;
    if (!messagesReceived) {
      messages.getChildren().remove(noMessagesYetText);
      messagesReceived = true;
    }
    var components = text.split(":");
    if (components.length < 2) return;
    var username = components[0];
    var message = String.join(":", Arrays.copyOfRange(components, 1, components.length));
    userList.addUser(username);

    Message receivedMessage = new Message(app, username, message);
    receivedMessage.setOpacity(0);
    FadeTransition fadeMessage = new FadeTransition(messageFadeInDuration, receivedMessage);
    fadeMessage.setToValue(1);
    messages.getChildren().add(receivedMessage);
    fadeMessage.play();

    if (messagesContainer.getVvalue() == 1.0f) scrollToBottom = true;

    if (!lastMessageFromMe) Utility.playAudio("incoming.mp3");
    else lastMessageFromMe = false;
  }

  /**
   * Scroll to bottom of messages list.
   */
  private void scrollToBottom() {
    if (!scrollToBottom) return;
    messagesContainer.setVvalue(1.0);
    scrollToBottom = false;
  }

  /**
   * Send an outgoing message from the Chat window.
   *
   * @param text The text of the message to send to the Communicator
   */
  private void sendCurrentMessage(String text) {
    if (!text.isBlank()) {
      if (text.strip().equals("/game")) app.openGame();
      else if (text.matches("/nick (.+)$")) app.setUsername(text.substring(6));
      else {
        communicator.send(app.getUsername() + ":" + text);
        lastMessageFromMe = true;
      }
      messageToSend.clear();
    }
  }

  /**
   * Unhide this window
   */
  public void show() {
    super.show();
  }

}
