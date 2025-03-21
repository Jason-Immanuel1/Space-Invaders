package jasonimmanuel.spaceinvaders;

import javafx.animation.TranslateTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.util.Duration;
import javafx.scene.layout.BorderPane;

public class GameMenu {

    public static MenuBar createMenuBar() {
        // Create a menu bar
        MenuBar menuBar = new MenuBar();

        // Create File menu
        Menu fileMenu = new Menu("File");
        // Quit option
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction(e -> System.exit(0)); 
        fileMenu.getItems().add(quitItem);

        // Create Help menu
        Menu helpMenu = new Menu("Help");

        // About option
        MenuItem aboutItem = new MenuItem("About");

        // Define the about action directly here
        aboutItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About Space Invaders");
            alert.setHeaderText("Space Invaders Game");
            alert.setContentText("Developed by: Jason Immanuel, \nOscar Wilson-Troy, and Jonathan Guest. \nVersion 1.0");
            alert.showAndWait();
        });
        helpMenu.getItems().add(aboutItem);

        // Add menus to menu bar
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        // Set initial position off-screen (above the scene)
        menuBar.setTranslateY(-50);  // Menu is hidden initially

        return menuBar;
    }

    // Call this method to setup sliding effect for the menu bar
    public static void setupMenuBarSlidingEffect(MenuBar menuBar, BorderPane root) {
        // Mouse enters the root area: Slide down the menu bar
        root.setOnMouseEntered(event -> {
            TranslateTransition slideDown = new TranslateTransition(Duration.seconds(0.2), menuBar);
            slideDown.setToY(0);  // Bring the menu down into view
            slideDown.play();
        });

        // Mouse exits the root area: Slide up the menu bar
        root.setOnMouseExited(event -> {
            TranslateTransition slideUp = new TranslateTransition(Duration.seconds(0.2), menuBar);
            slideUp.setToY(-50);  // Move the menu back up out of view
            slideUp.play();
        });
    }
}
