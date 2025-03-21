package jasonimmanuel.spaceinvaders;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

public class GreenAlien extends Invader {

    public GreenAlien(int x, int y) {
        super(x, y);

        // Set sprite
        this.sprite.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/green_alien/green_alien_frame_1.png"))));

        // Ensure hit box matches sprite size
        this.hitbox.setWidth(this.sprite.getImage().getWidth());
        this.hitbox.setHeight(this.sprite.getImage().getHeight());

        this.value = 300;
    }
}
