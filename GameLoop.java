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
    private Explosion explosion;
    private final SpaceInvadersApplication gameApp;
    private boolean isRespawning = false; // Flag to prevent multiple respawn attempts
    private List<Block> walls;
    
    public GameLoop(Player player, List<Bullet> bullets, List<Invader> invaders, Pane root, SpaceInvadersApplication gameApp, List<Block> walls) {
        this.player = player;
        this.bullets = bullets;
        this.invaders = invaders; // Store invaders
        this.root = root;
        this.walls = walls;
        this.gameApp = gameApp;
        explosion = new Explosion();
        scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        scoreLabel.setTranslateX(10); 
        scoreLabel.setTranslateY(10);
        root.getChildren().add(scoreLabel); 
    }
    
    @Override
    public void handle(long now) {
        updateEntities();
        checkCollisions();
        removeOffscreenBullets();
        
        if(invaders.isEmpty() && !isRespawning){
            spawnNewWave();
        }
    }
    
    private void updateEntities() {
        player.update();
        for (Bullet bullet : bullets) {
            bullet.update();
        }
        for (Invader invader : invaders) {
            invader.moveInPattern();
            invader.update();
        }
    }
    
    private void checkCollisions() {
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
         bulletIterator = bullets.iterator();
         while (bulletIterator.hasNext()) {
             Bullet bullet = bulletIterator.next();
             Iterator<Block> wallIterator = walls.iterator();
             while (wallIterator.hasNext()) {
                 Block wall = wallIterator.next();
                 if(wall.getAlive()==false){
                         root.getChildren().removeAll(wall.getSprite());
                         wallIterator.remove();
                     }
                 if (bullet.collidesWith(wall)) {
                     
                     System.out.println(bullet.collidesWith(wall));
                     
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
    
    private void updateScore() {
        score += 100;  // Increase score by 100 per alien killed
        scoreLabel.setText("Score: " + score);  // Update the displayed score
    }
    
    private void spawnNewWave() {
        isRespawning = true; 
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5)); //0.5 second delay in respawning aliens
        pause.setOnFinished(event -> {
            gameApp.spawnInvaders();
            isRespawning = false; // Reset flag after respawning, stops multiple respawns
        });
        pause.play();
    }
}