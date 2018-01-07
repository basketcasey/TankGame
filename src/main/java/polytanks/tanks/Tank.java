package polytanks.tanks;

import polytanks.Main;
import polytanks.environment.Wall;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Tank {
    Main game = null;
    boolean turningLeft = false;
    boolean turningRight = false;
    boolean accelForward = false;
    boolean accelBackward = false;

    int hitPoints = 50; // damage done by shots

    // Physics and position
    double posX, posY, angle, velX, velY, accelerationRate, decay, accelDecay, turningRate;
    double startingX, startingY;

    // Polygon specs for each part of the tank respective of central origin point
    double[] xOrigPtsBody = {-10, 10, 10, -10};
    double[] yOrigPtsBody = {-15, -15, 15, 15};
    double[] xOrigPtsTurret = {-5, 5, 5, -5};
    double[] yOrigPtsTurret = {-5, -5, 10, 10};
    double[] xOrigPtsBarrel = {-1, 1, 1, -1};
    double[] yOrigPtsBarrel = {-18, -18, -5, -5};
    int[] xOrigPtsBodyInt = {0,0,0,0}, yOrigPtsBodyInt = {0,0,0,0};
    int[] xOrigPtsTurretInt = {0,0,0,0}, yOrigPtsTurretInt = {0,0,0,0};
    int[] xOrigPtsBarrelInt = {0,0,0,0}, yOrigPtsBarrelInt = {0,0,0,0};

    Wall wall;
    int screenWidth, screenHeight;

    boolean destroyed;
    int health;
    Rectangle collisionRectangle = new Rectangle();

    public abstract void paint(Graphics2D g);
    public abstract void move();

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Rectangle getCollisionRectangle() {
        return collisionRectangle;
    }


    public void setCollisionRectangle(Rectangle rect) {
        collisionRectangle = rect;
    }

    public boolean checkCollision(Point2D p) {
        return  (getCollisionRectangle().contains(p)) ? true : false;
    }

    public Point2D getTankLocation() {
        Point2D p = new Point2D.Double();
        p.setLocation(posX, posY);
        return p;
    }

    public int getHealth() {
        return health;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public double getBarrelAngle() {
        return angle;
    }

    public Point getBarrelPosition() {
        return new Point(xOrigPtsBarrelInt[2], yOrigPtsBarrelInt[2]);
    }

    public void reset() {
        posX = startingX;
        posY = startingY;
        health = 100;
        destroyed = false;
    }

    public void processHit() {
        health -= hitPoints;
        if (health <= 0) {
            destroyed = true;
        }
    }

    //  Setter methods for user controls
    public void setTurningLeft() { turningLeft = true; }
    public void setTurningRight() { turningRight = true; }
    public void setAccelForward() { accelForward = true; }
    public void setAccelBackward() { accelBackward = true; }
}
