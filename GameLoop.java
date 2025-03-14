import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Iterator;
import java.util.List;
import javafx.scene.control.Label;

public class GameLoop extends AnimationTimer {
    private final Player player;
    private final List<Bullet> bullets;
    private final List<Invader> invaders; // Add invaders list
    private final Pane root;
    private int score = 0;  // Initialize score to 0
    private Label scoreLabel;
    private int lives = 3; // Initialize lives to 3
    private Label livesLabel;
    private int round = 0; //intialise round to 0
    private Label roundLabel;
    private Explosion explosion;
    private final SpaceInvadersApplication gameApp;
    private boolean isRespawning = false; // Flag to prevent multiple respawn attempts
    private List<Block> walls;
    private long lastInvaderUpdate = 0;
    private final long INVADER_UPDATE_INTERVAL = 200_000_000; // 200ms in nanoseconds
    private boolean shouldMoveDown = false;
    private boolean groupMovingRight = true;
    private final double SCREEN_WIDTH;
    
    public GameLoop(Player player, List<Bullet> bullets, List<Invader> invaders, Pane root, SpaceInvadersApplication gameApp, List<Block> walls, double screenWidth) {
        this.player = player;
        this.bullets = bullets;
        this.invaders = invaders; // Store invaders
        this.root = root;
        this.walls = walls;
        this.gameApp = gameApp;
        this.SCREEN_WIDTH = screenWidth;
        explosion = new Explosion();
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        scoreLabel.setTranslateX(10); 
        scoreLabel.setTranslateY(10);
        root.getChildren().add(scoreLabel); 
        livesLabel = new Label("Lives: " + lives);
        livesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        livesLabel.setTranslateX(10);
        livesLabel.setTranslateY(40); 
        root.getChildren().add(livesLabel);
        roundLabel = new Label("Round: " + round);
        roundLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        roundLabel.setTranslateX(10);
        roundLabel.setTranslateY(70);
        root.getChildren().add(roundLabel); // Make sure to add it to the root; 
    }
    
    @Override
    public void handle(long now) {
        updatePlayer();
        updateBullets();
        
        // Update invaders with controlled timing
        if (now - lastInvaderUpdate >= INVADER_UPDATE_INTERVAL) {
            updateInvaders();
            lastInvaderUpdate = now;
        }
        
        checkCollisions();
        removeOffscreenBullets();
        
        if(invaders.isEmpty() && !isRespawning){
            spawnNewWave();
        }
    }
    
    private void updateInvaders() {
        // Check if any invader is at the edge
        checkInvaderBoundaries();
        
        // Move all invaders together
        for (Invader invader : invaders) {
            if (shouldMoveDown) {
                invader.moveDown();
            } else if (groupMovingRight) {
                invader.moveRight();
            } else {
                invader.moveLeft();
            }
            invader.update();
        }
        
        // Reset the moveDown flag after processing
        if (shouldMoveDown) {
            shouldMoveDown = false;
            groupMovingRight = !groupMovingRight; // Change direction after moving down
        }
    }
    
    private void checkInvaderBoundaries() {
        // Check if any invader is at the edge
        for (Invader invader : invaders) {
            if (groupMovingRight && invader.getX() + invader.getSprite().getFitWidth() >= SCREEN_WIDTH - 10) {
                shouldMoveDown = true;
                break;
            } else if (!groupMovingRight && invader.getX() <= 10) {
                shouldMoveDown = true;
                break;
            }
        }
    }
    
    private void updatePlayer() {
        player.update();
    }
    
    private void updateBullets() {
        for (Bullet bullet : bullets) {
            bullet.update();
        }
    }
    
    private void updateEntities() {
        player.update();
        for (Bullet bullet : bullets) {
            bullet.update();
        }
        for (Invader invader : invaders) {
            invader.update();
        }
    }
    
    private void checkCollisions() {
    // Bullet-Invader Collision
    Iterator<Bullet> bulletIterator = bullets.iterator();
    while (bulletIterator.hasNext()) {
        Bullet bullet = bulletIterator.next();
        Iterator<Invader> invaderIterator = invaders.iterator();
        while (invaderIterator.hasNext()) {
            Invader invader = invaderIterator.next();
            if (bullet.collidesWith(invader)) {
                // Mark both bullet and invader as dead
                bullet.setAlive(false);
                invader.setAlive(false);
                
                explosion.playExplosion(root, invader.getX(), invader.getY());      
                // Remove from scene
                root.getChildren().removeAll(bullet.getSprite(), invader.getSprite());
                // Remove from lists
                bulletIterator.remove();
                invaderIterator.remove();
                updateScore();
                break;
            }
        }
    }

    // Bullet-Wall Collision
    bulletIterator = bullets.iterator();
    while (bulletIterator.hasNext()) {
        Bullet bullet = bulletIterator.next();
        Iterator<Block> wallIterator = walls.iterator();
        while (wallIterator.hasNext()) {
            Block wall = wallIterator.next();
            if (!wall.getAlive()) {
                root.getChildren().removeAll(wall.getSprite());
                wallIterator.remove();
            }
            if (bullet.collidesWith(wall)) {
                bullet.setAlive(false);
                wall.update();
                explosion.playExplosion(root, bullet.getX(), bullet.getY());      

                // Remove from scene
                root.getChildren().removeAll(bullet.getSprite());

                // Remove from lists
                bulletIterator.remove();
                break;
            }
        }
    }

    Iterator<Invader> invaderIterator = invaders.iterator();
    while (invaderIterator.hasNext()) {
        Invader invader = invaderIterator.next();
        Iterator<Block> wallIterator = walls.iterator();
        while (wallIterator.hasNext()) {
            Block wall = wallIterator.next();
            if (invader.collidesWith(wall)) {
                wall.update(); // Damage the wall
                if (!wall.getAlive()) { // If the wall is destroyed
                    root.getChildren().removeAll(wall.getSprite());
                    wallIterator.remove();
                }
                loseLife();
                clearAllInvaders();
                spawnNewWave();
                return; 
                }
            }
        }
    }

    
    private void removeOffscreenBullets() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            if (bullet.getY() < 0) {
                bullet.setAlive(false);
                root.getChildren().remove(bullet.getSprite());
                bulletIterator.remove();
            }
        }
    }
    
    private void clearAllInvaders() {
        for (Invader invader : invaders) {
            root.getChildren().remove(invader.getSprite());
        }
        invaders.clear();
    }
    
    //edited
    private void loseLife() {
    lives--; 
    livesLabel.setText("Lives: " + lives); 
    
    if (lives <= 0) {
        gameOver();
    }
    
    }    
    
    /**
     * Stope the game when live count reaches 0
     */
    private void gameOver() {
        this.stop();
        
        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: red; -fx-font-weight: bold;");
        gameOverLabel.setLayoutX(SCREEN_WIDTH/2 - 100); 
        gameOverLabel.setLayoutY(300);
        
        
        javafx.scene.control.Button restartButton = new javafx.scene.control.Button("Restart");
        restartButton.setStyle("-fx-font-size: 24px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        restartButton.setLayoutX(SCREEN_WIDTH/2 - 20); 
        restartButton.setLayoutY(400);
        
        restartButton.setOnAction(event -> {
            root.getChildren().clear();
            gameApp.restartGame();
        });
        
        root.getChildren().addAll(gameOverLabel, restartButton);
    }
    
    /**
     * popints scored for killing aliens
     */
    private void updateScore() {
        score += 100;  // Increase score by 100 per alien killed
        scoreLabel.setText("Score: " + score);  // Update the displayed score
    }
    
    /**
     * Re-intialised original variable values
     */
    private void restartGame() {
        lives = 3;
        score = 0;
        round = 0;
        
        bullets.clear();
        invaders.clear();
        walls.clear();
        
        livesLabel.setText("Lives: " + lives);
        scoreLabel.setText("Score: " + score);
        roundLabel.setText("Round: " + round);
        
        gameApp.restartGame();
    }
    
    private void spawnNewWave() {
        isRespawning = true; 
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5)); //0.5 second delay in respawning aliens
        pause.setOnFinished(event -> {
            round++;
            roundLabel.setText("Round: " + round); 
            gameApp.spawnInvaders();
            isRespawning = false; // Reset flag after respawning, stops multiple respawns
        });
        pause.play();
    }
}