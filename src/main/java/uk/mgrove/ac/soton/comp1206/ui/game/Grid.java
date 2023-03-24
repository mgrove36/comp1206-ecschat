package uk.mgrove.ac.soton.comp1206.ui.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Grid class, used to store a grid of game blocks for the game
 */
public class Grid {

  private final int cols;
  private final int rows;
  final SimpleIntegerProperty[][] grid;

  /**
   * Initialise the grid
   * @param cols number of columns in the grid
   * @param rows number of rows in the grid
   */
  public Grid(int cols, int rows) {
    this.cols = cols;
    this.rows = rows;

    grid = new SimpleIntegerProperty[cols][rows];

    for (var y=0; y < rows; y++) {
      for (var x=0; x < cols; x++) {
        grid[x][y] = new SimpleIntegerProperty(0);
      }
    }
  }

  /**
   * Get the property relating to a specific element in the grid
   * @param x column index
   * @param y row index
   * @return the property relating to the element at the given location
   */
  public IntegerProperty getGridProperty(int x, int y) {
    return grid[x][y];
  }

  /**
   * Set the value of a specific element in the grid
   * @param x column index
   * @param y row index
   * @param value the value to set
   */
  public void set(int x, int y, int value) {
    grid[x][y].set(value);
  }

  /**
   * Get the value of a specific element in the grid
   * @param x column index
   * @param y row index
   * @return the value of the element
   */
  public int get(int x, int y) {
    return grid[x][y].get();
  }

  /**
   * Get number of columns in the grid
   * @return number of columns
   */
  public int getCols() {
    return cols;
  }

  /**
   * Get number of rows in the grid
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

}
