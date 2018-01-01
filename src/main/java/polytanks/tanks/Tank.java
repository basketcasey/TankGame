package polytanks.tanks;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Tank {
    private static int id = 100;
    public int tankId;
    Polygon collisionPoly = new Polygon();

    public Tank() {
        this.tankId = id++;
    }

    public abstract double getBarrelAngle();
    public abstract Point getBarrelPosition();
    public abstract void paint(Graphics2D g);
    public abstract void move();
    public abstract void reset();
    public boolean checkCollision(Point2D p) {
        return (collisionPoly.contains(p)) ? true : false;
    }

    public boolean checkCollision(double x, double y, double w, double h) {
        return (collisionPoly.intersects(x, y, w, h)) ? true : false;
    }

}
