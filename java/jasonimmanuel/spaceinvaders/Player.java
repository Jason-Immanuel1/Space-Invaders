package jasonimmanuel.spaceinvaders;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Player extends Entity {
    private final double SPEED = 5;


    public Player(double startX, double startY) {
        super(startX, startY);
        this.sprite = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/player/playersprite.png"))));
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

    @Override
    public void update() {
        sprite.setX(x);
        sprite.setY(y);
    }
}