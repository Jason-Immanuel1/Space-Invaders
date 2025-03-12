package ppa.spaceinvaders;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Bullet extends Entity {

    public Bullet(double startX, double startY) {
        super(startX, startY);
        this.sprite = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/rocket.png"))));
        this.x = startX;
        this.y = startY;
        this.alive = true;
        this.sprite.setX(x);
        this.sprite.setY(y);
    }

    @Override
    public void update() {
        double SPEED = 10;
        setY(getY() - SPEED);
        getSprite().setY(getY());
        }

    public boolean collidesWith(Entity entity) {
        return sprite.getBoundsInParent().intersects(entity.getSprite().getBoundsInParent());
    }
}


