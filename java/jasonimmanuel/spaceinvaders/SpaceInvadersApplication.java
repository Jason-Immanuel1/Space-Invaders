package jasonimmanuel.spaceinvaders;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import java.io.InputStream;
import java.util.*;


public class SpaceInvadersApplication extends Application {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 900;
    private static final int SCREEN_WIDTH = 800;
    private final Pane gamePane = new Pane();
    private Player player;
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Bullet> playerBullets = new ArrayList<>();
    private final List<Invader> invaders = new ArrayList<>(); // Add an Invaders list

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private BorderPane root;
    private MenuBar menuBar;
    private final int score = 0; //initial score is 0
    private Label scoreLabel;
    private final List<Shield> walls = new ArrayList<>();

    private static MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {
        // Show the start menu first
        Scene startMenuScene = StartMenu.createStartMenu(stage);
        stage.setScene(startMenuScene);
        stage.setTitle("Space Invaders");
        stage.show();
    }

    public static void startGame(Stage stage) {
        // Create a new instance of the game
        SpaceInvadersApplication app = new SpaceInvadersApplication();

        playBackgroundMusic();

        // Create the BorderPane to hold menu and game content
        app.root = new BorderPane();

        // Add menu bar at the top (with sliding animation)
        app.menuBar = GameMenu.createMenuBar();
        app.root.setTop(app.menuBar);

        // Add game content in the center
        app.root.setCenter(app.createContent());

        // Set up the scene
        Scene gameScene = new Scene(app.root, WIDTH, HEIGHT);

        // Setup key event handling for the game scene
        gameScene.setOnKeyPressed(e -> {
            app.activeKeys.add(e.getCode());
            if (e.getCode() == KeyCode.SPACE) {
                app.fireBullet();
            }
        });

        // Track when keys are released
        gameScene.setOnKeyReleased(e -> app.activeKeys.remove(e.getCode()));

        // Setup menu bar sliding effect
        app.menuBarDropDown();

        // Set the scene
        stage.setScene(gameScene);
    }

    private Parent createContent() {
        gamePane.setPrefSize(WIDTH, HEIGHT);

        // Load the background image
        InputStream inputStream = getClass().getResourceAsStream("/sprites/backgrounds/space_invaders_background.png");
        assert inputStream != null;
        Image bgImage = new Image(inputStream);
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(WIDTH);
        bgView.setFitHeight(HEIGHT);
        gamePane.getChildren().add(bgView);

        // Initialize player
        player = new Player(300, 300);
        player.setX((double) WIDTH / 2 - 25); // Center horizontally
        player.setY(HEIGHT - 100);   // Place near bottom
        gamePane.getChildren().add(player.getSprite());

        // Spawn invaders
        spawnInvaders();
        spawnWalls();

        // Game loop and player movement control
        GameLoop gameLoop = new GameLoop(player, bullets, invaders, gamePane, this, walls, SCREEN_WIDTH) {
            @Override
            public void handle(long now) {
                // Handle player movement
                if (activeKeys.contains(KeyCode.LEFT) || activeKeys.contains(KeyCode.A)) {
                    player.moveLeft();
                }
                if (activeKeys.contains(KeyCode.RIGHT) || activeKeys.contains(KeyCode.D)) {
                    player.moveRight();
                }
                super.handle(now);
            }
        };
        gameLoop.start();

        return gamePane;
    }

    // Background music methods
    public static void playBackgroundMusic() {
        try {
            String musicFile = Objects.requireNonNull(StartMenu.class.getResource("/music/game_playlist/1-07. Guile's Theme [CPS-1].mp3")).toExternalForm();
            Media media = new Media(musicFile);
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop music
            mediaPlayer.setVolume(0.5); // Adjust volume (0.0 to 1.0)
            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    /**
     * Called when life count is set to 0. restarts game
     */
    public void restartGame() {
        gamePane.getChildren().clear(); //clear all elements

        // Reload the background
        InputStream inputStream = getClass().getResourceAsStream("/sprites/spaceinvadersbackground.png");
        assert inputStream != null;
        Image bgImage = new Image(inputStream);
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(WIDTH);
        bgView.setFitHeight(HEIGHT);
        gamePane.getChildren().add(bgView);

        // Reset collections
        bullets.clear();
        invaders.clear();
        walls.clear();

        // Reinitialize player
        player = new Player(300, 300);
        player.setX((double) WIDTH / 2 - 25); // Center horizontally
        player.setY(HEIGHT - 100);   // Place near bottom
        gamePane.getChildren().add(player.getSprite());

        // Spawn new invaders and walls
        spawnInvaders();
        spawnWalls();

        // Create and start a new game loop
        GameLoop gameLoop = new GameLoop(player, bullets, invaders, gamePane, this, walls, SCREEN_WIDTH) {
            @Override
            public void handle(long now) {
                // Handle player movement
                if (activeKeys.contains(KeyCode.LEFT) || activeKeys.contains(KeyCode.A)) {
                    player.moveLeft();
                }
                if (activeKeys.contains(KeyCode.RIGHT) || activeKeys.contains(KeyCode.D)) {
                    player.moveRight();
                }
                super.handle(now);
            }
        };
        gameLoop.start();
    }

    private void fireBullet() {
        if(bullets.size()<10 ){
            Bullet bullet = new Bullet(player.getX() + 20, player.getY() - 10, false);
            bullets.add(bullet);
            gamePane.getChildren().add(bullet.getSprite());
        }
    }

    public void spawnInvaders() {
        int rows = 5;  // Changed to 5 rows to accommodate the pattern
        int cols = 11;
        int startX = 100;
        int startY = 70;
        int spacing = 60;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Invader invader;

                // Updated alien distribution:
                // Row 0: Purple
                // Row 1-2: Blue
                // Row 3-4: Green
                if (row == 0) {
                    invader = new PurpleAlien(startX + col * spacing, startY);
                } else if (row == 1 || row == 2) {
                    invader = new BlueAlien(startX + col * spacing, startY + row * spacing);
                } else {  // row == 3 || row == 4
                    invader = new GreenAlien(startX + col * spacing, startY + row * spacing);
                }

                invaders.add(invader);
                gamePane.getChildren().add(invader.getSprite()); // Add invaders to the game pane
            }
        }
    }

    private void menuBarDropDown() {
        menuBar.setTranslateY(-30);

        root.setOnMouseMoved(e -> {
            if (e.getY() <= 50) {
                TranslateTransition slideDown = new TranslateTransition(Duration.seconds(0.1), menuBar);
                slideDown.setToY(0);
                slideDown.play();
            } else {
                menuBar.setTranslateY(-30);
            }
        });
    }


    private void spawnWalls() {
        int row = 750;
        int numWalls = 5;
        int paneWidth = 900;
        int spacing = paneWidth / (numWalls + 1); // Distribute evenly

        for (int i = 1; i <= numWalls; i++) {
            int x = spacing * i; // Position each wall evenly
            Shield wall = new Shield(x, row);
            walls.add(wall);
            gamePane.getChildren().add(wall.getSprite());
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}