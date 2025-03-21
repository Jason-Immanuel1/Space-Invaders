package jasonimmanuel.spaceinvaders;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public abstract class Invader extends Entity {
    private final double SPEED = 10; // Adjust based on game difficulty
    private final Random random = new Random();
    public int value;

    private boolean movingRight = true; // Start by moving right
    private final double MOVE_STEP = 15; // pixels to move in each step
    private final double DOWN_STEP = 20;

    public Invader(int x, int y) {
        super(x, y);

        InputStream inputStream = getClass().getResourceAsStream("/sprites/invader/invader.png");
        assert inputStream != null; // Ensure file exists
        Image invaderImage = new Image(inputStream);
        sprite = new ImageView(invaderImage);



        // Initialize sprite in subclass
        this.sprite.setX(x);
        this.sprite.setY(y);

        // Initialize hit dbox
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
        if (random.nextDouble() < 0.0001) { // 00.5% chance shooting per frame
            return new Bullet(x + sprite.getFitWidth() / 2, y + sprite.getFitHeight(), true);
        }
        return null;
    }

    public static List<Image> loadAlienFrames(String alienType) {
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

    /** Updates position of sprite and hit box */
    @Override
    public void update() {
        sprite.setX(x);
        sprite.setY(y);
        hitbox.setX(x);
        hitbox.setY(y);
    }
    

}
