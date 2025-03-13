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
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;  
import javafx.scene.paint.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class SpaceInvadersApplication extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private final Pane gamePane = new Pane();
    private Player player;
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Invader> invaders = new ArrayList<>(); // Add Invaders list
    
    private final Set<KeyCode> activeKeys = new HashSet<>();
    private BorderPane root;
    private MenuBar menuBar;
    private int score = 0; //intial score is 0
    private Label scoreLabel; 

    @Override
    public void start(Stage stage) {
        // Show start menu first
        Scene startMenuScene = StartMenu.createStartMenu(stage);
        stage.setScene(startMenuScene);
        stage.setTitle("Space Invaders");
        stage.show();
    }

    public static void startGame(Stage stage) {
        // Create a new instance of the game
        SpaceInvadersApplication app = new SpaceInvadersApplication();
        
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
        gameScene.setOnKeyReleased(e -> {
            app.activeKeys.remove(e.getCode());
        });

        // Setup menu bar sliding effect
        app.menuBarDropDown();

        // Set the scene
        stage.setScene(gameScene);
    }

    private Parent createContent() {
        gamePane.setPrefSize(WIDTH, HEIGHT);

        // Load the background image
        InputStream inputStream = getClass().getResourceAsStream("/sprites/spaceinvadersbackground.png");
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
        

        // Game loop and player movement control
        GameLoop gameLoop = new GameLoop(player, bullets, invaders, gamePane, this) {
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

    private void fireBullet() {
        Bullet bullet = new Bullet(player.getX() + 20, player.getY() - 10);
        bullets.add(bullet);
        gamePane.getChildren().add(bullet.getSprite());
    }

    public void spawnInvaders() {
        int rows = 3;
        int cols = 8;
        int startX = 100;
        int startY = 50;
        int spacing = 60;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Invader invader;
                // Assign different types of invaders
                if (row == 0) {
                    invader = new Xenomorph(startX + col * spacing, startY + row * spacing);
                } else if (row == 1) {
                    invader = new GreenAlien(startX + col * spacing, startY + row * spacing);
                } else {
                    invader = new BlueAlien(startX + col * spacing, startY + row * spacing);
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
    
     public void updateScore() {
        score += 100;  
        scoreLabel.setText("Score: " + score);  
    }


    public static void main(String[] args) {
        launch(args);
    }
}
