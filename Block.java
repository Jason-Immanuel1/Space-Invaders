
import javafx.scene.image.Image;

import java.util.Objects;

public class Block extends Invader
{
    private int stage;
    
    public Block(int x, int y){
        super(x, y);
        stage = 1;
        // Set sprite
        this.sprite.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/wall1.png"))));

        // Ensure hit box matches sprite size
        this.hitbox.setWidth(this.sprite.getImage().getWidth());
        this.hitbox.setHeight(this.sprite.getImage().getHeight());
        
        this.value = 0;
    }
    
    public void update(){
        stage += 1;
        if (stage > 4){
            setAlive(false);
            this.sprite.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/sprites/blank.png"))));
        }
        else{
            String toGet = "/sprites/wall" + stage + ".png";
            this.sprite.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(toGet))));
        }
    }
}
