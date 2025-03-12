import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartMenu {
    public static Scene createStartMenu(Stage stage) {
        BorderPane borderPane = new BorderPane();
        
        MenuBar menuBar = GameMenu.createMenuBar();
        
        StackPane centerContent = new StackPane();
        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            SpaceInvadersApplication.startGame(stage);
        });
        centerContent.getChildren().add(startButton);
        
        borderPane.setTop(menuBar);
        borderPane.setCenter(centerContent);
        
        return new Scene(borderPane, 800, 800);
    }
}