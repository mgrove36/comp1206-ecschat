package uk.mgrove.ac.soton.comp1206.ui;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.mgrove.ac.soton.comp1206.App;
import uk.mgrove.ac.soton.comp1206.network.Communicator;

import java.util.Arrays;

/**
 * Base window class, providing template from which all windows should extend
 */
public abstract class BaseWindow extends Window {
  protected static final Logger logger = LogManager.getLogger(ChatWindow.class);

  protected final App app;
  protected final Communicator communicator;

  /**
   * Duration over which menus should slide open/closed
   */
  protected final static Duration menuTransitionDuration = Duration.millis(300);
  /**
   * Duration over which buttons should rotate - used for menu open/close buttons
   */
  protected final static Duration buttonRotationDuration = Duration.millis(150);
  /**
   * Duration over which windows should fade in
   */
  protected final static Duration windowFadeInDuration = Duration.millis(500);

  @FXML
  protected VBox rightPanel;
  @FXML
  protected VBox leftPanel;
  @FXML
  protected SplitPane mainSplitPane;
  @FXML
  protected ImageView showLeftPanel;
  @FXML
  protected ImageView showRightPanel;
  @FXML
  protected ImageView hideLeftPanel;
  @FXML
  protected ImageView hideRightPanel;
  @FXML
  protected StackPane mainStackPane;

  protected Parent root;
  protected Node rightPanelPane;
  protected Node leftPanelPane;
  protected double[] mainSplitPaneDividerPositions;
  protected boolean[] sidePanelsVisible;

  /**
   * Initialise the window with an FXML template
   * @param app app to use
   * @param communicator communicator to use
   * @param fxmlPath path to FXML file
   */
  public BaseWindow(App app, Communicator communicator, String fxmlPath) {

    this.app = app;
    this.communicator = communicator;

    //Load the Chat Window GUI
    try {
      //Instead of building this GUI programmatically, we are going to use FXML
      var loader = new FXMLLoader(getClass().getResource(fxmlPath));

      //Link the GUI in the FXML to this class
      loader.setController(this);
      root = loader.load();
    } catch (Exception e) {
      //Handle any exceptions with loading the FXML
      logger.error("Unable to read file: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }

    root.setOpacity(0);
    fadeInWindow();

    //We are the login window
    setScene(new Scene(root));

    rightPanelPane = mainSplitPane.getItems().get(2);
    leftPanelPane = mainSplitPane.getItems().get(0);
    mainSplitPaneDividerPositions = mainSplitPane.getDividerPositions();
    sidePanelsVisible = new boolean[2];
    Arrays.fill(sidePanelsVisible, Boolean.TRUE);
    mainStackPane.getChildren().removeAll(showLeftPanel, showRightPanel);

  }

  /**
   * Fade the root node from invisible to visible
   */
  protected void fadeInWindow() {
    FadeTransition ft = new FadeTransition(windowFadeInDuration, root);
    ft.setFromValue(0);
    ft.setToValue(1);
    ft.play();
  }

  /**
   * Hide a given pane in a ScrollPane - used to hide menus
   *
   * @param panelPane the pane to hide
   * @param dividerPositionProperty the property for the appropriate divider's position
   * @param hideButton button to hide the pane
   * @param showButton button to show the pane
   * @param panelIndex index of the panel in local info stores (e.g. list of open/closed menus)
   */
  protected void hidePanel(Pane panelPane, DoubleProperty dividerPositionProperty, Node hideButton, Node showButton, int panelIndex) {
    panelPane.setMinWidth(0);

    int finalDividerPosition = panelIndex == 0 ? 0 : 1;

    KeyValue keyValue = new KeyValue(dividerPositionProperty, finalDividerPosition, Interpolator.EASE_BOTH);
    Timeline hidePanel = new Timeline(new KeyFrame(menuTransitionDuration, keyValue));

    RotateTransition rotateButton = new RotateTransition(buttonRotationDuration, hideButton);
    rotateButton.setFromAngle(0);
    rotateButton.setToAngle(panelIndex == 0 ? 180 : -180);
    rotateButton.setOnFinished((event2) -> {
      mainStackPane.getChildren().add(showButton);
      mainStackPane.getChildren().remove(hideButton);
      hideButton.setRotate(0);
    });

    hidePanel.play();

    hidePanel.setOnFinished((event) -> {
      rotateButton.play();
      sidePanelsVisible[panelIndex] = false;
      mainSplitPane.getItems().remove(panelPane);
      if (panelIndex == 0 && sidePanelsVisible[1])
        mainSplitPane.setDividerPosition(0, mainSplitPaneDividerPositions[1]);
    });
  }

  /**
   * Show a given pane in a ScrollPane - used to show menus
   *
   * @param panelPane the pane to show
   * @param hideButton button to hide the pane
   * @param showButton button to show the pane
   * @param panelIndex index of the panel in local info stores (e.g. list of open/closed menus)
   */
  protected void showPanel(Pane panelPane, Node hideButton, Node showButton, int panelIndex) {
    if (panelIndex == 0) mainSplitPane.getItems().add(0, panelPane);
    else mainSplitPane.getItems().add(panelPane);

    DoubleProperty dividerPositionProperty = mainSplitPane.getDividers().get(panelIndex == 1 && !sidePanelsVisible[0] ? 0 : panelIndex).positionProperty();

    int initialDividerPosition = panelIndex == 0 ? 0 : 1;

    mainSplitPane.setDividerPosition(panelIndex == 1 && !sidePanelsVisible[0] ? 0 : panelIndex, initialDividerPosition);
    KeyValue keyValue = new KeyValue(dividerPositionProperty, mainSplitPaneDividerPositions[panelIndex], Interpolator.EASE_BOTH);
    Timeline showPanel = new Timeline(new KeyFrame(menuTransitionDuration, keyValue));

    RotateTransition rotateButton = new RotateTransition(buttonRotationDuration, showButton);
    rotateButton.setFromAngle(0);
    rotateButton.setToAngle(panelIndex == 0 ? -180 : 180);
    rotateButton.setOnFinished((event2) -> {
      mainStackPane.getChildren().add(hideButton);
      mainStackPane.getChildren().remove(showButton);
      showButton.setRotate(0);
    });

    showPanel.play();

    showPanel.setOnFinished((event1) -> {
      rotateButton.play();
      sidePanelsVisible[panelIndex] = true;
      panelPane.setMinWidth(Region.USE_COMPUTED_SIZE);
      if (panelIndex == 0 && sidePanelsVisible[1])
        mainSplitPane.setDividerPosition(1, mainSplitPaneDividerPositions[1]);
    });
  }

  /**
   * Toggle the visibility of the right-hand sidebar
   */
  protected void toggleRightPanel() {
    if (sidePanelsVisible[1]) {
      DoubleProperty positionProperty;
      if (sidePanelsVisible[0]) {
        mainSplitPaneDividerPositions[1] = mainSplitPane.getDividerPositions()[1];
        positionProperty = mainSplitPane.getDividers().get(1).positionProperty();
      } else {
        mainSplitPaneDividerPositions[1] = mainSplitPane.getDividerPositions()[0];
        positionProperty = mainSplitPane.getDividers().get(0).positionProperty();
      }

      hidePanel(rightPanel, positionProperty, hideRightPanel, showRightPanel, 1);
    } else {
      showPanel(rightPanel, hideRightPanel, showRightPanel, 1);
    }
  }

  /**
   * Toggle the visibility of the right-hand sidebar
   */
  protected void toggleLeftPanel() {
    if (sidePanelsVisible[0]) {
      mainSplitPaneDividerPositions[0] = mainSplitPane.getDividerPositions()[0];
      if (sidePanelsVisible[1]) mainSplitPaneDividerPositions[1] = mainSplitPane.getDividerPositions()[1];

      hidePanel(leftPanel, mainSplitPane.getDividers().get(0).positionProperty(), hideLeftPanel, showLeftPanel, 0);
    } else {
      if (sidePanelsVisible[1]) mainSplitPaneDividerPositions[1] = mainSplitPane.getDividerPositions()[0];
      showPanel(leftPanel, hideLeftPanel, showLeftPanel, 0);
    }
  }

  /**
   * Handle mouse click to toggle visibility of right-hand sidebar
   *
   * @param event mouse clicked
   */
  public void toggleRightPanel(MouseEvent event) {
    toggleRightPanel();
  }

  /**
   * Handle mouse click to toggle visibility of right-hand sidebar
   *
   * @param event mouse clicked
   */
  public void toggleLeftPanel(MouseEvent event) {
    toggleLeftPanel();
  }

}
