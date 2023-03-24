package uk.mgrove.ac.soton.comp1206.ui;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Game grid class, which displays a grid of game blocks for the game
 */
public class GameGrid extends GridPane {

  private final List<BlockClickedListener> listeners = new ArrayList<>();

  /**
   * Initialise game grid with an existing grid, a set size, and a set of available colours
   * @param colours available colours
   * @param grid existing grid to use
   * @param width game grid width
   * @param height game grid height
   */
  public GameGrid(Color[] colours, Grid grid, int width, int height) {
    var cols = grid.getCols();
    var rows = grid.getRows();

    setMaxWidth(width);
    setMaxHeight(height);
    setGridLinesVisible(true);

    // create each internal game block
    for (var y=0; y < rows; y++) {
      for (var x=0; x < cols; x++) {
        var block = new GameBlock(colours, x, y, (double) width/cols, (double) height/rows);
        add(block,x,y);

        block.valueProperty().bind(grid.getGridProperty(x,y));

        // handle when game block is clicked
        block.setOnMouseClicked((e) -> blockClicked(block));
      }
    }
  }

  /**
   * Add listener for blocks being clicked
   * @param listener listener
   */
  public void addListener(BlockClickedListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Handle block being clicked by calling all appropriate listeners
   * @param block block that was clicked
   */
  public void blockClicked(GameBlock block) {
    for (BlockClickedListener listener : listeners) {
      listener.blockClicked(block);
    }
  }

}
