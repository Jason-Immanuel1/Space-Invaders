 

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.control.Label;

public class GameLoop extends AnimationTimer {
    private final Player player;
    private final List<Bullet> bullets;
    private final List<Invader> invaders; // Add the invader list
    private final Pane root;
    private int score = 0;  // Initialize score to 0
    private int lives = 3; // Initialize lives to 3
    private int round = 1; //initialise round to 1
    private final Explosion explosion;
    private final SpaceInvadersApplication gameApp;
    private boolean isRespawning = false; // Flag to prevent multiple respawn attempts
    private final List<Shield> walls;
    private long lastInvaderUpdate = 0;
    private boolean shouldMoveDown = false;
    private boolean groupMovingRight = true;
    private final double SCREEN_WIDTH;
    List<ImageView> hearts;
    Image hollowHeartImage;

    private Text roundText;
    private Text scoreText;
    private Text livesText;

    public GameLoop(Player player, List<Bullet> bullets, List<Invader> invaders, Pane root, SpaceInvadersApplication gameApp, List<Shield> walls, double screenWidth) {
        this.player = player;
        this.bullets = bullets;
        this.invaders = invaders;
        this.root = root;
        this.walls = walls;
        this.gameApp = gameApp;
        this.SCREEN_WIDTH = screenWidth;

        Font font;

        try (InputStream fontStream = StartMenu.class.getResourceAsStream("/fonts/PressStart2P.ttf")) {
            if (fontStream != null) {
                font = Font.loadFont(fontStream, 20);
            } else {
                throw new RuntimeException("Font not found!");
            }
        } catch (Exception e) {
            font = Font.font("Arial", 20); // Fallback font
        }

        // Load full heart image
        InputStream heartStream = StartMenu.class.getResourceAsStream("/sprites/heart/heart.png");
        if (heartStream == null) {
            throw new RuntimeException("Heart image not found!");
        }
        Image heartImage = new Image(heartStream);

        // Load hollow heart image
        InputStream hollowHeartStream = StartMenu.class.getResourceAsStream("/sprites/heart/hollow_heart.png"); // Ensure you have this image!
        if (hollowHeartStream == null) {
            throw new RuntimeException("Hollow heart image not found!");
        }
        hollowHeartImage = new Image(hollowHeartStream); // Correct usage

        // Create ImageView objects for the hearts
        ImageView heart1 = new ImageView(heartImage);
        ImageView heart2 = new ImageView(heartImage);
        ImageView heart3 = new ImageView(heartImage);

        int heartSize = 45;

        // Set heart sizes
        heart1.setFitWidth(heartSize);
        heart1.setFitHeight(heartSize);
        heart2.setFitWidth(heartSize);
        heart2.setFitHeight(heartSize);
        heart3.setFitWidth(heartSize);
        heart3.setFitHeight(heartSize);

        // Position the hearts
        double heartSpacing = 40;
        double heartY = 0;
        heart1.setTranslateX(700);
        heart1.setTranslateY(heartY);
        heart2.setTranslateX(700 + heartSpacing);
        heart2.setTranslateY(heartY);
        heart3.setTranslateX(700 + 2 * heartSpacing);
        heart3.setTranslateY(heartY);

        // Store hearts in a list
        hearts = new ArrayList<>(List.of(heart1, heart2, heart3));

        // Set initial lives
        lives = hearts.size();

        // Initialize explosion
        explosion = new Explosion();

        // Create and style game info text
        roundText = new Text("Round: " + round);
        scoreText = new Text("Score: " + score);
        livesText = new Text("Lives: ");

        roundText.setFont(font);
        scoreText.setFont(font);
        livesText.setFont(font);

        roundText.setFill(Color.WHITE);
        scoreText.setFill(Color.WHITE);
        livesText.setFill(Color.WHITE);

        roundText.setStyle("-fx-font-weight: bold;");
        scoreText.setStyle("-fx-font-weight: bold;");
        livesText.setStyle("-fx-font-weight: bold;");

        // Position text elements
        roundText.setTranslateX(10);
        roundText.setTranslateY(30);

        scoreText.setTranslateX(300);
        scoreText.setTranslateY(30);

        livesText.setTranslateX(575);
        livesText.setTranslateY(30);

        // Add elements to root
        root.getChildren().addAll(roundText, scoreText, livesText);
        root.getChildren().addAll(heart1, heart2, heart3);
    }
    
    @Override
    public void handle(long now) {
        updatePlayer();
        updateBullets();


        long invaderUpdateInterval = getInvaderUpdateInterval();

        if (now - lastInvaderUpdate >= invaderUpdateInterval) {
            updateInvaders();
            lastInvaderUpdate = now;
        }
        
        for (Invader invader : invaders) {
            Bullet invaderBullet = invader.shoot();
            if (invaderBullet != null) {
                bullets.add(invaderBullet); // Add the bullet to the list
                root.getChildren().add(invaderBullet.getSprite()); // Add the bullet sprite to the root pane
            }
        }
        checkCollisions();
        removeOffscreenBullets();
        
        if (invaders.isEmpty() && !isRespawning) {
            spawnNewWave(false);
        }
    }

    private long getInvaderUpdateInterval() {
        int currentRound = getRound();
        long invaderUpdateInterval;

        // change delay between movement based off round, higher round moves faster
        if (currentRound == 1) {
            //timings in which aliens can move
            invaderUpdateInterval = 1000_000_000;
        } else if (currentRound == 2) {
            invaderUpdateInterval = 150_000_000;
        } else if (currentRound == 3) {
            invaderUpdateInterval = 100_000_000;
        } else {
            invaderUpdateInterval = 50_000_000;
        }
        return invaderUpdateInterval;
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
            groupMovingRight = !groupMovingRight; // Change the direction after moving down
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
        
        // Alien bullets cannot kill one another
        if (bullet.isAlienShot()) {
            continue;
        }
        
        Iterator<Invader> invaderIterator = invaders.iterator();
        while (invaderIterator.hasNext()) {
            Invader invader = invaderIterator.next();
            if (bullet.collidesWith(invader)) {
                // Mark both bullet and invader as dead
                bullet.setAlive(false);
                invader.setAlive(false);
                
                explosion.playExplosion(root, invader.getX(), invader.getY());
                
                // Remove from the scene
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
    Iterator<Bullet> bulletIterator2 = bullets.iterator();
    while (bulletIterator2.hasNext()) {
        Bullet bullet = bulletIterator2.next();
        Iterator<Shield> wallIterator = walls.iterator();
        while (wallIterator.hasNext()) {
            Shield wall = wallIterator.next();
            if (!wall.getAlive()) {
                root.getChildren().removeAll(wall.getSprite());
                wallIterator.remove();
            }
            if (bullet.collidesWith(wall)) {
                bullet.setAlive(false);
                wall.update();
                explosion.playExplosion(root, bullet.getX(), wall.getY());      

                // Remove from the scene
                root.getChildren().removeAll(bullet.getSprite());

                // Remove from lists
                bulletIterator2.remove();
                break;
            }
        }
    }

    // Invader-Wall Collision
        for (Invader invader : invaders) {
            Iterator<Shield> wallIterator = walls.iterator();
            while (wallIterator.hasNext()) {
                Shield wall = wallIterator.next();
                if (invader.collidesWith(wall)) {
                    wall.update(); // Damage the wall
                    if (!wall.getAlive()) { // If the wall is destroyed
                        root.getChildren().removeAll(wall.getSprite());
                        wallIterator.remove();
                    }
                    loseLife();
                    clearAllInvaders();
                    spawnNewWave(true);
                    return;
                }
            }
        }
    
    Iterator<Bullet> bulletIterator3 = bullets.iterator();
    while(bulletIterator3.hasNext()){
        Bullet bullet = bulletIterator3.next();
        if(bullet.isAlienShot() && bullet.collidesWith(player)){
            loseLife();
            explosion.playExplosion(root, bullet.getX(), bullet.getY());
            clearAllInvaders();
            spawnNewWave(true);
            bullet.setAlive(false);
            
            root.getChildren().removeAll(bullet.getSprite());
            bulletIterator3.remove();
            
            return;
        }
    }
    
    
     for (Invader invader : invaders) {
         double dangerZoneY = 750;
         if (invader.getY() >= dangerZoneY) {
            loseLife();
            clearAllInvaders();
            spawnNewWave(true);
            return; 
        }
    }
    }

    private void removeOffscreenBullets() {
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            
            if (!bullet.isAlienShot() && bullet.getY() < 0) {
                bullet.setAlive(false);
                root.getChildren().remove(bullet.getSprite());
                bulletIterator.remove();
            }
            
            if (bullet.isAlienShot() && bullet.getY() > root.getHeight()) { 
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

    private void loseLife() {
        if (lives > 0) {
            lives--;

            // Swap full heart to hollow heart
            if (lives < hearts.size()) {
                hearts.get(lives).setImage(hollowHeartImage);
            }

            // Check for game over
            if (lives == 0) {
                gameOver();
            }
        }
    }
    
    /**
     * Stop the game when live count reaches 0
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
     * points scored for killing aliens
     */
    private void updateScore() {
        score += 100;  // Increase the score by 100 per alien killed
        scoreText.setText("Score: " + score);  // Update the displayed score
    }
    
    /**
     * Re-initialized original variable values
     */
    private void restartGame() {
        lives = 3;
        score = 0;
        round = 0;
        
        bullets.clear();
        invaders.clear();
        walls.clear();
        
        livesText.setText("Lives: " + lives);
        scoreText.setText("Score: " + score);
        roundText.setText("Round: " + round);
        
        gameApp.restartGame();
    }
    
    private void spawnNewWave(boolean isWallCollision) {
        isRespawning = true; 
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5)); // 1.5-second delay before respawning aliens
        pause.setOnFinished(event -> {
            if (!isWallCollision) {
                round++;  // Only increment round if it's not due to a wall collision
                roundText.setText("Round: " + round);
            }
            gameApp.spawnInvaders();
            isRespawning = false; // Reset flag after respawning, stops multiple respawns
        });
        pause.play();
    }
    
    //get current round
    public int getRound() {
        return round;
    }

}
