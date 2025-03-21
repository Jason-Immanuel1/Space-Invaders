 

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Objects;
import javafx.animation.ScaleTransition;


public class Explosion{
    
    public void playExplosion(Pane root, double x, double y) {
        // Load explosion images
        Image explOne = new Image("/sprites/explosion/explOne.png");
        Image explTwo = new Image("/sprites/explosion/explTwo.png");
        Image explThree = new Image("/sprites/explosion/explThree.png");

        ImageView explosion = new ImageView(explOne);
        explosion.setX(x);
        explosion.setY(y);
        
        root.getChildren().add(explosion);
        
        int length = 300;
        
         Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(length/2), e -> explosion.setImage(explTwo)), // Show explTwo after `length` ms
            new KeyFrame(Duration.millis(length), e -> explosion.setImage(explThree)), // Show explThree after another `length` ms
            new KeyFrame(Duration.millis(length), e -> {
                ScaleTransition shrinkTransition = new ScaleTransition(Duration.millis(length), explosion);
                shrinkTransition.setByX(-0.6); 
                shrinkTransition.setByY(-0.6); 
                shrinkTransition.setCycleCount(1);
                shrinkTransition.play();

                shrinkTransition.setOnFinished(evt -> root.getChildren().remove(explosion));
            })
        );
            
        timeline.setCycleCount(1);
        timeline.play();
    }
}
