package jasonimmanuel.spaceinvaders;

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
        Image explOne = new Image("file:" + System.getProperty("user.home") + "/Desktop/SpaceInvaders/sprites/explOne.png");
        Image explTwo = new Image("file:" + System.getProperty("user.home") + "/Desktop/SpaceInvaders/sprites/explTwo.png");
        Image explThree = new Image("file:" + System.getProperty("user.home") + "/Desktop/SpaceInvaders/sprites/explThree.png");

        ImageView explosion = new ImageView(explOne);
        explosion.setX(x);
        explosion.setY(y);
        
        root.getChildren().add(explosion);
        
        int length = 300;

        
        /*ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(length), explosion);
        scaleTransition.setByX(1.25); // Scale the X dimension by 2 times
        scaleTransition.setByY(1.25); // Scale the Y dimension by 2 times
        scaleTransition.setCycleCount(1);
        scaleTransition.play();*/
        
         Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(length/2), e -> explosion.setImage(explTwo)), // Show explTwo after `length` ms
            new KeyFrame(Duration.millis(length), e -> explosion.setImage(explThree)), // Show explThree after another `length` ms
            new KeyFrame(Duration.millis(1.5*length), e -> {
                ScaleTransition shrinkTransition = new ScaleTransition(Duration.millis(3*length), explosion);
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
