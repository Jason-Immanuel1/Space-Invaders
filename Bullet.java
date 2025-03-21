 

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Bullet extends Entity {
    private final boolean isAlienShot; // Flag to determine if the bullet is shot by an alien

    // Constructor accepts an additional boolean to determine the direction
    public Bullet(double startX, double startY, boolean isAlienShot) {
        super(startX, startY);
        this.isAlienShot = isAlienShot; // Set the direction based on the flag
        if(isAlienShot){
            this.sprite = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/projectiles/rocket_alien.png"))));
        }
        else{
            this.sprite = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/projectiles/rocket.png"))));
        }
        this.x = startX;
        this.y = startY;
        this.alive = true;
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    @Override
    public void update() {
        double SPEED = 2;
        
        // If the bullet is shot by the player, it moves up
        // If shot by an alien, it moves down
        if (isAlienShot) {
            setY(getY() + SPEED);  // Moving down
        } else {
            setY(getY() - SPEED);  // Moving up
        }
        
        getSprite().setY(getY());
    }

    public boolean collidesWith(Entity entity) {
        return sprite.getBoundsInParent().intersects(entity.getSprite().getBoundsInParent());
    }
    
    public boolean isAlienShot() {
        return isAlienShot;
    }

}



