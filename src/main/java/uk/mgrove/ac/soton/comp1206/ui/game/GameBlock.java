package uk.mgrove.ac.soton.comp1206.ui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Class for a game block, which is a single block with a colour that can be changed
 */
public class GameBlock extends Canvas {

  private final int x;
  private final int y;
  private final Color[] colours;
  private final double width;
  private final double height;
  private final IntegerProperty value;
  private final boolean showStrokes;
  private final boolean roundedCorners;


  /**
   * Initialise the game block with a set of available colours, a set size,
   * a set location in the grid, and outlines around the block
   * @param colours available colours
   * @param x column index of location in grid
   * @param y row index of location in grid
   * @param width block width
   * @param height block height
   */
  public GameBlock(Color[] colours, int x, int y, double width, double height) {
    this(colours,x,y,width,height,true,false);
  }

  /**
   * Initialise the game block with a set of available colours, a set size,
   * a set location in the grid, and optional outlines around the block
   * @param colours available colours
   * @param x column index of location in grid
   * @param y row index of location in grid
   * @param width block width
   * @param height block height
   * @param showStrokes whether block should have an outline
   * @param roundedCorners whether block should have rounded corners
   */
  public GameBlock(Color[] colours, int x, int y, double width, double height, boolean showStrokes, boolean roundedCorners) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.colours = colours;
    this.showStrokes = showStrokes;
    this.roundedCorners = roundedCorners;
    value = new SimpleIntegerProperty();

    setWidth(width);
    setHeight(height);

    value.addListener(((observableValue, oldValue, newValue) -> paint()));

    paint();
  }

  /**
   * Paint the block - i.e. draw it
   */
  public void paint() {
    GraphicsContext gc = getGraphicsContext2D();
    gc.clearRect(0,0,width,height);

    if (value.get() >= 0) {
      // fill in colour block
      gc.setFill(colours[value.get()]);
      gc.setStroke(Color.BLACK);

      if (roundedCorners) {
        gc.fillRoundRect(0,0,width,height,16,16);
        if (showStrokes) gc.strokeRoundRect(0,0,width,height,16,16);
      } else {
        gc.fillRect(0,0,width,height);
        if (showStrokes) gc.strokeRect(0,0,width,height);
      }
    }
  }

  /**
   * Get the x-coordinate of this block in the grid
   * @return x-coordinate
   */
  public int getX() {
    return x;
  }

  /**
   * Get the y-coordinate of this block in the grid
   * @return y-coordinate
   */
  public int getY() {
    return y;
  }

  /**
   * Get the value property for binding, which holds the current colour value
   * @return value property
   */
  public IntegerProperty valueProperty() {
    return value;
  }

}
