 

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Player extends Entity {
    private final double SPEED = 10;
    private int lives = 3;
    private final int score = 0;
    private boolean canShoot = true;
    private static final double COOLDOWN_TIME = 3;
    private final Set<KeyCode> activeKeys = new HashSet<>();

    public Player(double startX, double startY) {
        super(startX, startY);
        this.sprite = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/playersprite.png"))));
        this.x = startX;
        this.y = startY;
        this.alive = true;
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    public void moveLeft() {
        if(getX()>0){
            setX(getX() - SPEED);
            getSprite().setX(getX());
        }
    }

    public void moveRight() {
        if(getX()<750){
            setX(getX() + SPEED);
            getSprite().setX(getX());
        }
    }

    public void takeDamage() {
        lives--;
        if (lives <= 0) {
            alive = false;
        }
    }

    @Override
    public void update() {
        sprite.setX(x);
        sprite.setY(y);
    }
}
