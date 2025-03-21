module jasonimmanuel.spaceinvaders {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;


    opens jasonimmanuel.spaceinvaders to javafx.fxml;
    exports jasonimmanuel.spaceinvaders;
}