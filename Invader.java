import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

public abstract class Invader extends Entity {
    private final double SPEED = 50; // Adjust based on game difficulty
    private final Random random = new Random();
    public int value;

    private boolean movingRight = true; // Start by moving right
    private final double MOVE_STEP = 15; // pixels to move in each step
    private final double DOWN_STEP = 30; 

    public Invader(int x, int y) {
        super(x, y);

        InputStream inputStream = getClass().getResourceAsStream("/sprites/invader.png");
        assert inputStream != null; // Ensure file exists
        Image invaderImage = new Image(inputStream);
        sprite = new ImageView(invaderImage); // ✅ Initialize sprite

        // Initialize sprite in subclass
        this.sprite.setX(x);
        this.sprite.setY(y);

        // Initialize hitbox
        this.hitbox = new Rectangle(sprite.getImage().getWidth(), sprite.getImage().getHeight());
        this.hitbox.setX(x);
        this.hitbox.setY(y);

        this.alive = true;

        this.value = 100;
    }

     public void moveRight() {
        this.x += MOVE_STEP;
    }
    
    public void moveLeft() {
        this.x -= MOVE_STEP;
    }
    
    public void moveDown() {
        this.y += DOWN_STEP;
    }

    /** Randomly determines if the invader should shoot */
    public Bullet shoot() {
        if (random.nextDouble() < 0.02) { // 2% chance per frame to shoot
            return new Bullet(x + sprite.getFitWidth() / 2, y + sprite.getFitHeight());
        }
        return null;
    }

    /** Updates position of sprite and hitbox */
    @Override
    public void update() {
        sprite.setX(x);
        sprite.setY(y);
        hitbox.setX(x);
        hitbox.setY(y);
    }
    

}
