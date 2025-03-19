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
    private static final int SCREEN_WIDTH = 750;
    private final Pane gamePane = new Pane();
    private Player player;
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Invader> invaders = new ArrayList<>(); // Add Invaders list
    
    private final Set<KeyCode> activeKeys = new HashSet<>();
    private BorderPane root;
    private int score = 0; //intial score is 0
    private Label scoreLabel; 
     private List<Block> walls = new ArrayList<>();
     private GameMenu gameMenu;

    @Override
    public void start(Stage stage) {
        // Show start menu first
        Scene startMenuScene = StartMenu.createStartMenu(stage);
        stage.setScene(startMenuScene);
        stage.setTitle("Space Invaders");
        stage.show();
    }

    public static void startGame(Stage stage) {
        SpaceInvadersApplication app = new SpaceInvadersApplication();
        
        app.root = new BorderPane();
        
        app.root.setCenter(app.createContent());
        
        Scene gameScene = new Scene(app.root, WIDTH, HEIGHT);
        
        app.gameMenu = new GameMenu(gameScene, stage);
        
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                app.gameMenu.togglePause();
            } else {
                app.activeKeys.add(e.getCode());
                if (e.getCode() == KeyCode.SPACE) {
                    app.fireBullet();
                }
            }
        });
    
        gameScene.setOnKeyReleased(e -> {
            app.activeKeys.remove(e.getCode());
        });
    
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
        spawnWalls();
    
        GameLoop gameLoop = new GameLoop(player, bullets, invaders, gamePane, this, walls, SCREEN_WIDTH, gameMenu) {
            @Override
            public void handle(long now) {
                // Skip if paused
                if (gameMenu != null && gameMenu.isPaused()) {
                    return;
                }
                
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
    
    /**
     * Called when life count is set to 0. restarts oprignial gamew
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
        
        GameLoop gameLoop = new GameLoop(player, bullets, invaders, gamePane, this, walls, SCREEN_WIDTH, gameMenu) {
            @Override
            public void handle(long now) {
                // Skip if paused
                if (gameMenu != null && gameMenu.isPaused()) {
                    return;
                }
                
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
        if(bullets.size()<4 ){
        Bullet bullet = new Bullet(player.getX() + 20, player.getY() - 10, false);
        bullets.add(bullet);
        gamePane.getChildren().add(bullet.getSprite());
        }
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

    
     public void updateScore() {
        score += 100;  
        scoreLabel.setText("Score: " + score);  
    }

    
    private void spawnWalls(){
         int row = 600;
         Block wall;
         for(int col = 1; col < 9; col += 2){
         wall = new Block(90*col, row);
         walls.add(wall);
         gamePane.getChildren().add(wall.getSprite());
         }
        
     }
     
    public static void main(String[] args) {
        launch(args);
    }
}