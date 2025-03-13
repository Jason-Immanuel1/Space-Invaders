import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import java.util.Iterator;
import java.util.List;

public class GameLoop extends AnimationTimer {
    private final Player player;
    private final List<Bullet> bullets;
    private final List<Invader> invaders; // Add invaders list
    private final Pane root;
    
    private List<Block> walls;
    
    private Explosion explosion;
    public GameLoop(Player player, List<Bullet> bullets, List<Invader> invaders, Pane root, List<Block> walls) {
        this.player = player;
        this.bullets = bullets;
        this.invaders = invaders; // Store invaders
        this.root = root;
        
        this.walls = walls;
        explosion = new Explosion();
    }

    @Override
    public void handle(long now) {
        updateEntities();
        checkCollisions();
        removeOffscreenBullets();
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
                    break;
                }
            }
        }
        //check wall collisions
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
}