package jasonimmanuel.spaceinvaders;

import javafx.scene.image.Image;

import java.util.Objects;

public class BlueAlien extends Invader {

    public BlueAlien(int x, int y) {
        super(x, y);

        // Set sprite
        this.sprite.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/blue_alien/blue_alien_frame_1.png"))));

        // Ensure hit box matches sprite size
        this.hitbox.setWidth(this.sprite.getImage().getWidth());
        this.hitbox.setHeight(this.sprite.getImage().getHeight());

        this.value = 100;
    }
}
