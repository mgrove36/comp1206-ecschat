package uk.mgrove.ac.soton.comp1206.ui.game;

/**
 * Interface for listeners that handle a game block being clicked
 */
public interface BlockClickedListener {

  /**
   * Handle game block being clicked
   * @param block block that was clicked
   */
  void blockClicked(GameBlock block);

}
