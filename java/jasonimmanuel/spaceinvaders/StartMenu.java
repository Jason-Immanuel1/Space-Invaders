package jasonimmanuel.spaceinvaders;

import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StartMenu {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 900;
    private static MediaPlayer mediaPlayer;

    public static Scene createStartMenu(Stage stage) {
        // Start background music when the menu is created
        playBackgroundMusic();

        BorderPane borderPane = new BorderPane();
        Font titleFont;

        try (InputStream fontStream = StartMenu.class.getResourceAsStream("/fonts/PressStart2P.ttf")) {
            if (fontStream != null) {
                titleFont = Font.loadFont(fontStream, 50);
            } else {
                throw new RuntimeException("Font not found!");
            }
        } catch (Exception e) {
            titleFont = Font.font("Arial", 50); // Fallback font
        }

        // Title text
        Text title = new Text("SPACE INVADERS");
        title.setFont(titleFont);
        title.setStyle("-fx-fill: yellow; -fx-stroke: red; -fx-stroke-width: 2px;");
        title.setOpacity(0);

        // Background Image
        InputStream inputStream = StartMenu.class.getResourceAsStream("/sprites/backgrounds/space_invaders_background.png");
        assert inputStream != null;
        Image bgImage = new Image(inputStream);
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(WIDTH);
        bgView.setFitHeight(HEIGHT);

        // Center Content
        StackPane centerContent = new StackPane(bgView);
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(80, 0, 0, 0));

        // Create Start Button
        VBox buttonBox = new VBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(300, 0, 0, 0));

        Button startButton = new Button("Push To Start");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 20px; -fx-padding: 10 20 10 20;");
        startButton.setOnAction(_ -> {
            stopMusic(); // Stop menu music before starting the game
            SpaceInvadersApplication.startGame(stage);
        });

        buttonBox.getChildren().add(startButton);

        centerContent.getChildren().addAll(title, buttonBox);

        // Title Animation (Float Up, Fade In, and Scale Up)
        ParallelTransition titleAnimation = getParallelTransition(title);
        titleAnimation.play();

        stage.setResizable(false);

        // Load alien images with correct frame counts
        List<Image> greenAlienFrames = loadAlienFrames("green_alien");
        List<Image> blueAlienFrames = loadAlienFrames("blue_alien");
        List<Image> purpleAlienFrames = loadAlienFrames("purple_alien");
        List<Image> UFOAlienFrames = loadAlienFrames("ufo");

        // Initialize ImageViews
        ImageView greenAlienView = new ImageView(greenAlienFrames.getFirst());
        ImageView blueAlienView = new ImageView(blueAlienFrames.getFirst());
        ImageView purpleAlienView = new ImageView(purpleAlienFrames.getFirst());
        ImageView UFOAlienView = new ImageView(UFOAlienFrames.getFirst());

        // Animation setup
        final int frameDuration = 500; // Adjust animation speed as needed
        IntegerProperty currentFrameIndex = new SimpleIntegerProperty(0);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(frameDuration), _ -> {
            int index = currentFrameIndex.get();

            greenAlienView.setImage(greenAlienFrames.get(index));
            blueAlienView.setImage(blueAlienFrames.get(index));
            purpleAlienView.setImage(purpleAlienFrames.get(index));
            UFOAlienView.setImage(UFOAlienFrames.get(index));

            currentFrameIndex.set((index + 1) % greenAlienFrames.size()); // Assume the same frame count for all
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Create text labels
        VBox alienContainer = createAlienScoreDisplay(
                new ImageView[]{greenAlienView, blueAlienView, purpleAlienView, UFOAlienView},
                new String[]{"= 100 PTS", "= 200 PTS", "= 300 PTS", "= ??? PTS"}
        );

        StackPane.setAlignment(alienContainer, Pos.CENTER);
        StackPane.setMargin(alienContainer, new Insets(-150, 0, 0, 0));
        alienContainer.setMouseTransparent(true);
        centerContent.getChildren().add(alienContainer);

        // Menu bar
        borderPane.setCenter(centerContent);

        return new Scene(borderPane, WIDTH, HEIGHT);
    }

    // Background music methods
    public static void playBackgroundMusic() {
        try {
            String musicFile = Objects.requireNonNull(StartMenu.class.getResource("/music/menu_playlist/main_menu.mp3")).toExternalForm();
            Media media = new Media(musicFile);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop music
            mediaPlayer.setVolume(0.5); // Adjust volume (0.0 to 1.0)
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Properly release resources
            mediaPlayer = null;
        }
    }

    private static ParallelTransition getParallelTransition(Text title) {
        Duration duration = Duration.seconds(1.5);

        // Fade In
        FadeTransition fadeIn = new FadeTransition(duration, title);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Scale Up
        ScaleTransition scaleUp = new ScaleTransition(duration, title);
        scaleUp.setFromX(0.8);
        scaleUp.setFromY(0.8);
        scaleUp.setToX(1);
        scaleUp.setToY(1);

        // Float Up (Translate Y)
        TranslateTransition floatUp = new TranslateTransition(duration, title);
        floatUp.setFromY(50);  // Start 50 pixels lower
        floatUp.setToY(0);     // Move to original position

        // Play all animations together
        return new ParallelTransition(fadeIn, scaleUp, floatUp);
    }

    private static List<Image> loadAlienFrames(String alienType) {
        List<Image> spriteFrames = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            String framePath = "/sprites/" + alienType + "/" + alienType + "_frame_" + i + ".png";
            InputStream stream = StartMenu.class.getResourceAsStream(framePath);
            if (stream == null) {
                throw new RuntimeException("Frame " + i + " for " + alienType + " not found!");
            }
            System.out.println("Loading frame: " + framePath);
            spriteFrames.add(new Image(stream));
        }
        return spriteFrames;
    }

    private static VBox createAlienScoreDisplay(ImageView[] alienImages, String[] scores) {
        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        Font scoreFont = new Font("Press Start 2P", 24);

        for (int i = 0; i < alienImages.length; i++) {
            Text scoreText = new Text(scores[i]);
            scoreText.setFont(scoreFont);
            scoreText.setFill(Color.WHITE);

            HBox row = new HBox(20, alienImages[i], scoreText);
            row.setAlignment(Pos.CENTER);
            container.getChildren().add(row);
        }
        return container;
    }
}




