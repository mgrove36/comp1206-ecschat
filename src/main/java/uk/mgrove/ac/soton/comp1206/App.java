package uk.mgrove.ac.soton.comp1206;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import uk.mgrove.ac.soton.comp1206.network.Communicator;
import uk.mgrove.ac.soton.comp1206.ui.BaseWindow;
import uk.mgrove.ac.soton.comp1206.ui.chat.ChatWindow;
import uk.mgrove.ac.soton.comp1206.ui.game.GameWindow;
import uk.mgrove.ac.soton.comp1206.ui.LoginWindow;
import uk.mgrove.ac.soton.comp1206.utility.Utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Our Chat application main class. This will be responsible for co-ordinating the application and handling the GUI.
 */
public class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);
    private Communicator communicator;
    private Stage stage;
    private final StringProperty username = new SimpleStringProperty("Guest");
    private final BooleanProperty gameOpen = new SimpleBooleanProperty(false);
    private final Map<BaseWindow, Stage> windowStageMap = new HashMap<>();
    private ChatWindow chatWindow;

    /**
     * Launch the JavaFX application
     *
     * @param args arguments given when starting the client
     */
    public static void main(String[] args) {
        logger.info("Starting client");
        launch();
    }

    /**
     * Start the Java FX process - prepare and display the first window
     *
     * @param stage the stage to use
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9500");

        stage.setTitle("ECS Instant Messenger (EIM)");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/uos_logo_square.png")));
        stage.setOnCloseRequest(ev -> {
            shutdown();
        });

        openLogin();
    }

    /**
     * Display the login window
     */
    public void openLogin() {
        logger.info("Opening login window");
        var window = new LoginWindow(this);
        stage.setScene(window.getScene());

        stage.show();
        stage.centerOnScreen();
    }

    /**
     * Display the chat window
     */
    public void openChat() {
        logger.info("Opening chat window");
        chatWindow = new ChatWindow(this, communicator);

        stage.setScene(chatWindow.getScene());

        stage.show();
        stage.centerOnScreen();

        Utility.playAudio("connected.mp3");
    }

    /**
     * Display the game
     */
    public void openGame() {
        if (gameOpen.get()) return;

        logger.info("Opening game window");
        gameOpen.set(true);

        Stage stage = new Stage();
        stage.setTitle("Game - ECS Instant Messenger (EIM)");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/uos_logo_square.png")));
        var window = new GameWindow(this, communicator);

        stage.setScene(window.getScene());
        stage.show();
        stage.centerOnScreen();

        windowStageMap.put(window, stage);

        hideChat();

        stage.setOnCloseRequest((e) -> {
            shutdown();
        });
    }

    /**
     * Set the value of the gameOpen variable, which is used to track if the game window is open
     * @param value value to set
     */
    public void setGameOpen(boolean value) {
        gameOpen.set(value);
    }

    /**
     * Return to chat window from other open window
     * @param window current window, which should be closed
     */
    public void returnToChat(BaseWindow window) {
        if (windowStageMap.containsKey(window)) windowStageMap.get(window).close();
        showChat();
    }

    /**
     * Move chat window to the background
     */
    private void hideChat() {
        if (chatWindow != null) stage.hide();
    }

    /**
     * Show chat window
     */
    private void showChat() {
        if (chatWindow != null) stage.show();
    }

    /**
     * Shutdown the application
     */
    public void shutdown() {
        logger.info("Shutting down");
        System.exit(0);
    }

    /**
     * Set the username from the login window
     *
     * @param username new username for current user
     */
    public void setUsername(String username) {
        logger.info("Username set to: " + username);
        this.username.set(username);
    }

    /**
     * Get the currently logged-in username
     *
     * @return username of current user
     */
    public String getUsername() {
        return this.username.get();
    }

    /**
     * Get the username property
     * @return username property
     */
    public StringProperty usernameProperty() {
        return username;
    }
}
