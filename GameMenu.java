import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameMenu {
    private boolean isPaused = false;
    private VBox pauseMenu;
    private StackPane pauseOverlay;

    public GameMenu(Scene gameScene, Stage stage) {
        createPauseMenu(stage);

        StackPane root = new StackPane();
        if (gameScene.getRoot() instanceof StackPane) {
            root = (StackPane) gameScene.getRoot();
        } else {
            root.getChildren().add(gameScene.getRoot());
            gameScene.setRoot(root);
        }

        pauseOverlay.setVisible(false);
        root.getChildren().add(pauseOverlay);

        // toggle pause, press p
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                togglePause();
            }
        });
    }

    private void createPauseMenu(Stage stage) {
        Rectangle overlay = new Rectangle(800, 800); 
        overlay.setFill(Color.rgb(0, 0, 0, 0.7)); 

        Label pausedLabel = new Label("Game Paused");
        pausedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        pausedLabel.setTextFill(Color.WHITE);

        Button resumeButton = new Button("Resume");
        resumeButton.setPrefWidth(200);
        resumeButton.setOnAction(e -> togglePause());

        Button helpButton = new Button("Help");
        helpButton.setPrefWidth(200);
        helpButton.setOnAction(e -> showHelp());

        Button quitButton = new Button("Quit");
        quitButton.setPrefWidth(200);
        quitButton.setOnAction(e -> {
            stage.close();
            System.exit(0);
        });

        pauseMenu = new VBox(20); 
        pauseMenu.getChildren().addAll(pausedLabel, resumeButton, helpButton, quitButton);
        pauseMenu.setAlignment(Pos.CENTER);

        pauseOverlay = new StackPane();
        pauseOverlay.getChildren().addAll(overlay, pauseMenu);
    }

    public void togglePause() {
        isPaused = !isPaused;
        pauseOverlay.setVisible(isPaused);
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void showHelp() {
        Label titleLabel = new Label("Welcome to Space Invaders!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Label helpText = new Label(
            "Controls:\n" +
            "- Left/Right arrows to move player\n" +
            "- Space: Fire bullet\n" +
            "- P: Pause/Resume game"
        );
        helpText.setTextFill(Color.LIGHTGRAY);

        
        TextField creatorField = new TextField("Created by Jason Immanuel, Jonathan Guest and Oscar Wilson-Troy");
        creatorField.setEditable(false); 
        creatorField.setAlignment(Pos.CENTER);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        VBox helpBox = new VBox(20);
        helpBox.getChildren().addAll(titleLabel, helpText, creatorField, closeButton);
        helpBox.setAlignment(Pos.CENTER);

        StackPane helpPane = new StackPane();
        Rectangle helpBg = new Rectangle(400, 350);
        helpBg.setFill(Color.rgb(50, 50, 50, 0.9)); 
        helpPane.getChildren().addAll(helpBg, helpBox);

        Stage helpStage = new Stage();
        helpStage.setScene(new Scene(helpPane, 400, 350));
        helpStage.setTitle("Help");

        helpStage.show();
    }
}
