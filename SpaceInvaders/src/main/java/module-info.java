module ppa.spaceinvaders {
    requires javafx.controls;
    requires javafx.fxml;


    opens ppa.spaceinvaders to javafx.fxml;
    exports ppa.spaceinvaders;
}