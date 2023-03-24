module uk.mgrove.ac.soton.comp1206 {
    requires java.scripting;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.swing;
    requires java.desktop;
    requires javafx.base;
    requires org.apache.logging.log4j;
    requires nv.websocket.client;
    opens uk.mgrove.ac.soton.comp1206.ui to javafx.fxml;
    opens uk.mgrove.ac.soton.comp1206.utility to javafx.fxml;
    exports uk.mgrove.ac.soton.comp1206;
    exports uk.mgrove.ac.soton.comp1206.ui;
    exports uk.mgrove.ac.soton.comp1206.network;
    exports uk.mgrove.ac.soton.comp1206.utility;
    exports uk.mgrove.ac.soton.comp1206.ui.game;
    opens uk.mgrove.ac.soton.comp1206.ui.game to javafx.fxml;
    exports uk.mgrove.ac.soton.comp1206.ui.chat;
    opens uk.mgrove.ac.soton.comp1206.ui.chat to javafx.fxml;
}

