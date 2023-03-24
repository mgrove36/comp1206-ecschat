package uk.mgrove.ac.soton.comp1206.ui;

/**
 * Interface for listeners that handle new messages being received by the Communicator class
 * @see uk.mgrove.ac.soton.comp1206.network.Communicator
 */
public interface MessageListener {

  /**
   * Receive a message
   * @param message the message to receive
   */
  void receiveMessage(String message);

}
