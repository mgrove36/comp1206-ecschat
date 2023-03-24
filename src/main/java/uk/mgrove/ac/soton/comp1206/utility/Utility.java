package uk.mgrove.ac.soton.comp1206.utility;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

/**
 * A utility class for quick and handy static functions
 */
public class Utility {

    private static final Logger logger = LogManager.getLogger(Utility.class);
    private static BooleanProperty audioEnabled = new SimpleBooleanProperty(true);
    private static MediaPlayer mediaPlayer;
    private static final String urlRegex = "((https?|file):((//)|(\\\\))+[\\w:#@%/;$()~_?+-=\\\\.&]*)";
    private static final Pattern urlPattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);

    /**
     * Play an audio file
     * @param file filepath of file to play
     */
    public static void playAudio(String file) {
        if (!audioEnabled.get()) return;

        String toPlay = Utility.class.getResource("/" + file).toExternalForm();
        logger.info("Playing audio: " + toPlay);

        try {
            Media play = new Media(toPlay);
            mediaPlayer = new MediaPlayer(play);
            mediaPlayer.play();
        } catch (Exception e) {
            audioEnabled.set(false);
            e.printStackTrace();
            logger.error("Unable to play audio file, disabling audio");
        }
    }

    /**
     * Get property for whether audio is enabled
     * @return property
     */
    public static BooleanProperty audioEnabledProperty() {
        return audioEnabled;
    }

    /**
     * Get regex pattern for URLs
     * @return URL pattern
     */
    public static Pattern getUrlPattern() {
        return urlPattern;
    }

}
