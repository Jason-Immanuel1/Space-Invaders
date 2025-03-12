import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartMenu {
    public static Scene createStartMenu(Stage stage) {
        // Create a BorderPane as the main layout
        BorderPane borderPane = new BorderPane();
        
        // Create the menu bar
        MenuBar menuBar = GameMenu.createMenuBar();
        
        // Create the center content with the start button
        StackPane centerContent = new StackPane();
        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            // When the button is clicked, transition to the game
            SpaceInvadersApplication.startGame(stage);
        });
        centerContent.getChildren().add(startButton);
        
        // Set the menu bar at the top and content in the center
        borderPane.setTop(menuBar);
        borderPane.setCenter(centerContent);
        
        return new Scene(borderPane, 800, 800);
    }
}