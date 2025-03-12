package ppa.spaceinvaders;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SpaceInvadersApplication extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private final Pane root = new Pane();
    private Player player;
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<Invader> invaders = new ArrayList<>(); // Add Invaders list

    private Parent createContent() {
        root.setPrefSize(WIDTH, HEIGHT);

        // Load the background image
        InputStream inputStream = getClass().getResourceAsStream("/sprites/spaceinvadersbackground.png");
        assert inputStream != null;
        Image bgImage = new Image(inputStream);
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(WIDTH);
        bgView.setFitHeight(HEIGHT);
        root.getChildren().add(bgView);

        // Initialize player
        player = new Player(300, 300);
        player.setX((double) WIDTH / 2 - 25); // Center horizontally
        player.setY(HEIGHT - 100);   // Place near bottom
        root.getChildren().add(player.getSprite());

        // Spawn invaders
        spawnInvaders();

        // Start game loop
        GameLoop gameLoop = new GameLoop(player, bullets, invaders, root);
        gameLoop.start();

        return root;
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(createContent(), WIDTH, HEIGHT);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                player.moveLeft();
            } else if (event.getCode() == KeyCode.RIGHT) {
                player.moveRight();
            } else if (event.getCode() == KeyCode.SPACE) {
                fireBullet();
            }
        });

        stage.setScene(scene);
        stage.setTitle("Space Invaders");
        stage.show();
    }

    private void fireBullet() {
        Bullet bullet = new Bullet(player.getX() + 20, player.getY() - 10);
        bullets.add(bullet);
        root.getChildren().add(bullet.getSprite());
    }

    private void spawnInvaders() {
        int rows = 3;
        int cols = 8;
        int startX = 100;
        int startY = 50;
        int spacing = 60;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Invader invader;

                // Assign different types of invaders based on row index
                if (row == 0) {
                    invader = new Xenomorph(startX + col * spacing, startY + row * spacing);
                } else if (row == 1) {
                    invader = new GreenAlien(startX + col * spacing, startY + row * spacing);
                } else {
                    invader = new BlueAlien(startX + col * spacing, startY + row * spacing);
                }

                invaders.add(invader);
                root.getChildren().add(invader.getSprite()); // Ensure getSprite() returns a Node
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}



