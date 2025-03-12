package ppa.spaceinvaders;

import javafx.scene.image.Image;

import java.util.Objects;

public class GreenAlien extends Invader {

    public GreenAlien(int x, int y) {
        super(x, y);

        // Set sprite
        this.sprite.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/greenalien.png"))));

        // Ensure hit box matches sprite size
        this.hitbox.setWidth(this.sprite.getImage().getWidth());
        this.hitbox.setHeight(this.sprite.getImage().getHeight());

        this.value = 300;
    }
}