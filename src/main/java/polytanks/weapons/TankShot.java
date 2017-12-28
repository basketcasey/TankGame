package polytanks.weapons;

import polytanks.environment.Wall;
import polytanks.tanks.Tank;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;


public class TankShot {
    private int screenWidth, screenHeight;
    private double angle, velX, velY, posX, posY, startX, startY, accelerationRate;
    private boolean shotComplete = false;
    private boolean justTaken = true;
    Point2D position = new Point2D.Double();

    public TankShot(int sizeX, int sizeY, double shotAngle, int shotXpos, int shotYpos) {
        screenWidth = sizeX;
        screenHeight = sizeY;
        angle = shotAngle;
        posX = startX = shotXpos;
        posY = startY = shotYpos;
        position.setLocation(posX, posY);
        accelerationRate = 1;
    }

    public void move(Wall wall) {
        accelerationRate = 30.75; // static speed, artillery should get faster after leaving gun
        velX = Math.cos(angle) * accelerationRate;
        velY = Math.sin(angle) * accelerationRate;
        if (justTaken) {
            // Done move position from barrel on first draw of the shot
            justTaken = false;
        } else {
            posX += velX;
            posY += velY;
        }

        position.setLocation(posX, posY);

        if (wall.checkCollision(position)) {
            shotComplete = true;
        }

        // Make sure the shot didn't go off the screen
        if (posX > screenWidth || posX < 0 || posY > screenHeight || posY < 0) {
            shotComplete = true;
        }
    }

    public void paint(Graphics2D g) {
        if (!shotComplete) {
            g.setColor(Color.black);

            //System.out.println("Shot fired to x: " + posX + " end " + shotEndX);
            //System.out.println("Shot fired to y: " + posY + " end " + shotEndY);
            //g.drawLine((int)startX, (int)startY, (int)posX, (int)posY);
            g.drawLine((int)posX, (int)posY, (int)(posX + velX), (int)(posY + velY));
            // g.fillOval((int)(posX-.5), (int)(posY-.5), 3, 3);
        }
    }

    public Point2D getPosition() {
        return position;
    }

    public boolean getShotComplete() { return shotComplete; }
}
