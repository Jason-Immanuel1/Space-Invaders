package ppa.spaceinvaders;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public abstract class Entity {

    protected ImageView sprite;
    protected Rectangle hitbox;
    protected double x, y;
    protected double dx, dy;
    protected boolean alive;
    protected boolean canShoot;


    public void setSprite(ImageView sprite) {
        this.sprite = sprite;
    }

    public ImageView getSprite() {
        return sprite;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setCanShoot(boolean canShoot){
        this.canShoot = canShoot;
    }

    public boolean getCanShoot(){
        return canShoot;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean getAlive(){
        return alive;
    }

    public Entity(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(){
        x += dx;
        y += dy;

        if(sprite != null){
            sprite.setTranslateX(x);
            sprite.setTranslateY(y);
        }
        hitbox.setTranslateX(x);
        hitbox.setTranslateY(y);
    }

    public boolean collidesWith(Entity other){
        return this.hitbox.getBoundsInParent().intersects(other.hitbox.getBoundsInParent());
    }

    public abstract void update();
}



