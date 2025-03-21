package jasonimmanuel.spaceinvaders;

import javafx.scene.image.Image;
import java.util.Objects;

public class Shield extends Invader {
    private int stage;

    public Shield(int x, int y) {
        super(x, y);
        stage = 1;

        // Set sprite
        this.sprite.setImage(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/sprites/shields/shield_1.png"))));

        // Ensure hit box matches sprite size
        this.hitbox.setWidth(this.sprite.getImage().getWidth());
        this.hitbox.setHeight(this.sprite.getImage().getHeight());

        this.value = 0;
    }

    public void update() {
        stage++;

        if (stage > 10) {
            setAlive(false);
            this.sprite.setImage(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/sprites/shields/blank.png"))));
        } else {
            String toGet = String.format("/sprites/shields/shield_%d.png", stage);
            this.sprite.setImage(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(toGet))));
        }
    }
}

