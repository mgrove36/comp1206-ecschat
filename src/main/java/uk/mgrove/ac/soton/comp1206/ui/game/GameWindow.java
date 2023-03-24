package uk.mgrove.ac.soton.comp1206.ui;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.mgrove.ac.soton.comp1206.App;
import uk.mgrove.ac.soton.comp1206.network.Communicator;
import uk.mgrove.ac.soton.comp1206.utility.Utility;

import java.util.*;

/**
 * Game window which will display mini-game for users to play
 */
public class GameWindow extends BaseWindow {

  private static final Logger logger = LogManager.getLogger(GameWindow.class);

  private static final int maxColourChanges = 30;

  private final Grid grid;
  private final Random random = new Random();
  private final Color[] colours;
  private final IntegerProperty currentColour = new SimpleIntegerProperty();
  private final IntegerProperty score = new SimpleIntegerProperty(0);
  private Timer gameTimer;
  private int colourChangesCount;
  private final List<String> scores = new ArrayList<>();
  @FXML
  private BorderPane gameBoardContainer;
  @FXML
  private Text currentScoreField;
  @FXML
  private VBox currentColourContainer;
  @FXML
  private VBox highScoresContainer;
  @FXML
  private VBox gameCoverContainer;
  @FXML
  private Text gameCoverTitle;
  @FXML
  private ProgressBar progressBar;

  /**
   * Initialise the game window
   *
   * @param app the app
   * @param communicator the communicator
   */
  public GameWindow(App app, Communicator communicator) {
    super(app, communicator, "/game.fxml");

    colours = new Color[]{
        Color.web("0x005C84"),
        Color.web("0x74C9E5"),
        Color.web("0x4BB694"),
        Color.web("0xC1D100"),
        Color.web("0xEF7D00"),
        Color.web("0xE73037"),
        Color.web("0xD5007F"),
        Color.web("0x8D3970")
    };

    grid = new Grid(6, 6);

    currentScoreField.textProperty().bind(score.asString());

    var currentColourBlock = new GameBlock(colours, 0, 0, 100, 100, false, true);
    currentColourContainer.getChildren().add(currentColourBlock);
    currentColourBlock.valueProperty().bind(currentColour);

    communicator.addListener((message) -> {
      if (!message.startsWith("SCORES")) return;
      Platform.runLater(() -> this.receiveScore(message));
    });

    communicator.send("SCORES");
  }

  /**
   * Events that occur at regular intervals to maintain the game: update the current colour and handle game ending
   */
  public void gameLoop() {
    if (colourChangesCount < maxColourChanges) {
      var randomColour = random.nextInt(colours.length);
      logger.info("Game loop - Colour chosen: {}", randomColour);
      currentColour.set(randomColour);
      colourChangesCount++;
      progressBar.setProgress((double) (maxColourChanges - colourChangesCount + 1) / maxColourChanges);
    } else {
      progressBar.setProgress(0);
      endGame();
    }
  }

  /**
   * Handle game block being clicked - adjust score appropriately
   *
   * @param block block that was clicked
   */
  public void blockClicked(GameBlock block) {
    var gridBlock = grid.getGridProperty(block.getX(), block.getY());
    if (currentColour.get() == gridBlock.get()) {
      logger.info("{} is {}, scoring 5 points", currentColour.get(), gridBlock.getValue());
      score.set(score.get() + 5);
      gridBlock.set(random.nextInt(colours.length));
    } else {
      logger.info("{} is not {}, losing 1 point", currentColour.get(), gridBlock.getValue());
      if (score.get() > 0) {
        score.set(score.get() - 1);
      }
    }
  }

  /**
   * Receive and store scores from server
   * @param message message containing scores
   */
  private void receiveScore(String message) {
    scores.clear();

    for (var line : message.split("\n")) {
      if (!line.equals("SCORES")) {
        int splitIndex = line.lastIndexOf("=");
        try {
          String[] info = {line.substring(0,splitIndex), line.substring(splitIndex+1)};
          scores.add(info[0] + ": " + info[1]);
        } catch (Exception ignored) {
          logger.error("Couldn't load score from message {}",message);
        }
      }
    }

    highScoresContainer.getChildren().clear();
    for (var i=0; i < Math.min(scores.size(),5); i++) {
      highScoresContainer.getChildren().add(new Text(scores.get(i)));
    }

  }

  /**
   * Show game over screen
   */
  private void showGameOver() {
    gameCoverTitle.setText("Game Over!");
    Platform.runLater(() -> gameBoardContainer.setCenter(gameCoverContainer));
    // TODO: include a timer bar
  }

  /**
   * Start a new game
   */
  private void startGame() {
    colourChangesCount = 0;
    score.set(0);

    // set up grid with random values
    for (int y = 0; y < grid.getRows(); y++) {
      for (int x = 0; x < grid.getCols(); x++) {
        grid.set(x, y, random.nextInt(colours.length));
      }
    }

    currentColour.set(random.nextInt(colours.length));

    var gameGrid = new GameGrid(colours, grid, 400, 350);
    gameGrid.addListener(this::blockClicked);

    gameBoardContainer.setCenter(gameGrid);

    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        gameLoop();
      }
    };
    gameTimer = new Timer("Timer");
    gameTimer.schedule(timerTask, 0, 2000);
  }

  /**
   * Start game when button clicked
   * @param event button clicked
   */
  public void startGame(ActionEvent event) {
    startGame();
  }

  /**
   * Return to the chat window when button clicked
   * @param event button clicked
   */
  public void returnToChat(ActionEvent event) {
    stopGameTimer();
    app.setGameOpen(false);
    app.returnToChat(this);
  }

  /**
   * Stop the game
   */
  private void endGame() {
    // game finished
    stopGameTimer();
    currentColour.set(-1);
    communicator.send(String.format("SCORE %s %d",app.getUsername(),score.get()));
    communicator.send("SCORES");
    showGameOver();
  }

  /**
   * Stop the game timer
   */
  public void stopGameTimer() {
    if (gameTimer != null) gameTimer.cancel();
  }

}
