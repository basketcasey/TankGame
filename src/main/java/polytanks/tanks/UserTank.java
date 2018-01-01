package polytanks.tanks;

import polytanks.environment.Wall;

import java.awt.*;
import java.awt.geom.Point2D;

import static polytanks.utils.GameMathUtils.CheckAngleBoundary;

public class UserTank extends Tank{
    public UserTank(int xSize, int ySize, int origPosX, int origPosY, Wall wall) {
        this.wall = wall;
        hitPoints = 10;  // adjust damage var

        screenWidth = xSize; // set screen limits
        screenHeight = ySize; // set screen limits

        health = 100;
        destroyed = false;

        posX = startingX = origPosX;  // Set initial position
        posY = startingY = origPosY;  // Set initial position

        velX = 0; // Not moving
        velY = 0; // Not moving
        accelerationRate = 1.5; // Used to calc velocity
        decay = 0.1; // slowing rate for current velocity (inertia)
        accelDecay = 0.06; // slowing rate for acceleration (movement under power)
        angle = Math.PI/2; // Point it right (90 Degrees)
        turningRate = 0.155555555555555555; // Turns in radians (0 - 2PI)
    }

    public void move() {
        Double origX = posX;
        Double origY = posY;

        if (turningLeft) {
            angle -= turningRate; // decrease angle by rate so turn speed can be easily modified
            // Make sure angle is within bounds of 360 degrees (2 PI rad)
            angle = CheckAngleBoundary(angle);


            turningLeft = false; // reset user control

            // If currently moving, make movement follow this new angle
            if (velX != 0 || velY != 0) {
                velX = Math.cos(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
                velY = Math.sin(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            }
        }
        if (turningRight) {
            angle += turningRate;
            angle = CheckAngleBoundary(angle);

            turningRight = false; // reset user control
            // If currently moving, make movement follow this new angle
            if (velX != 0 || velY != 0) {
                velX = Math.cos(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
                velY = Math.sin(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            }
        }

        if (accelForward) {
            // move in direction where pointing
            // Problem here is that the current angle doesn't match with front of tank
            accelerationRate += 0.75;
            velX = Math.cos(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            velY = Math.sin(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            accelForward = false;
        }
        if (accelBackward) {
            // move in negative direction for where pointing
            accelerationRate -= 0.75;
            // move in opposite direction where pointing
            velX = Math.cos(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            velY = Math.sin(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            accelBackward = false;
        }

        // Set tanks new position
        posX += velX;
        posY += velY;

        // Can't drive through walls
        Point2D p = new Point2D.Double();
        p.setLocation(posX, posY);
        if (wall.checkCollision(p)) {
            velX = velY = 0;
            posX = origX;
            posY = origY;
        }

        // Limit tanks position to screen
        if (posX > screenWidth-20) { posX = screenWidth-20; velX = 0;}
        if (posX < 20) { posX = 20; velX = 0;}
        if (posY > screenHeight -20) { posY = screenHeight-20; velY = 0;}
        if (posY < 45) { posY = 45; velY = 0;}

        // Modify velocity by slowing in opposite direction
        if(velX > 0 ) {
            velX -= velX * decay;
            if (velX < 0) {velX = 0;}
        }
        if(velX < 0 ) {
            velX -= velX * decay;
            if (velX > 0) {velX = 0;}
        }
        if(velY > 0 ) {
            velY -= velY * decay;
            if (velY < 0) {velX = 0;}
        }
        if(velY < 0 ) {
            velY -= velY * decay;
            if (velY > 0) {velY = 0;}
        }
        // Modify accelerationRate by slowing in opposite direction
        if(accelerationRate > 0 ) { // If positive, reduce by decay
            accelerationRate -= accelerationRate * accelDecay;

            if (accelerationRate < 0.1) {
                accelerationRate = 0; // decaying can't reverse direction so set to zero
            }
        }
        if(accelerationRate < 0 ) { // If going backwards, slow it by going more positive
            accelerationRate -= accelerationRate * accelDecay;
            if (accelerationRate > -0.1) {
                accelerationRate = 0; // decaying can't reverse direction so set to zero
            }
        }
    }


    public void paint(Graphics2D g) {
        for (int i=0; i < xOrigPtsBody.length; i++) {
            xOrigPtsBodyInt[i] = (int)(xOrigPtsBody[i]*Math.cos(angle) - yOrigPtsBody[i]*Math.sin(angle)+posX+.5);
            yOrigPtsBodyInt[i] = (int)(xOrigPtsBody[i]*Math.sin(angle) + yOrigPtsBody[i]*Math.cos(angle)+posY+.5);

            boolean debug_rotation = false;
            if (debug_rotation) {
                System.out.println("OffsetX: " + xOrigPtsBody[i]
                        + "\tOffset Y: " + yOrigPtsBody[i]);
                System.out.println("xOrigin: " + posX + "\tyOrigin: "
                        + posY + "\tAngle (Rad): " + angle);
                System.out.println("newX: " + xOrigPtsBodyInt[i] + "\tnewY: "
                        + yOrigPtsBodyInt[i]);
                System.out.println("cos(angle): " + Math.cos(angle)
                        + "\tsin(angle): " + Math.sin(angle));
                System.out.println((xOrigPtsBody[i] * Math.cos(angle)) + " - "
                        + (yOrigPtsBody[i] * Math.sin(angle)) + " + "
                        + (posX + 0.5));
                System.out.println((xOrigPtsBody[i] * Math.sin(angle)) + " + "
                        + (yOrigPtsBody[i] * Math.cos(angle)) + " + "
                        + (posY + 0.5));
                System.out.println("");
            }

            xOrigPtsTurretInt[i] = (int)(xOrigPtsTurret[i]*Math.cos(angle) - yOrigPtsTurret[i]*Math.sin(angle)+posX+.5);
            yOrigPtsTurretInt[i] = (int)(xOrigPtsTurret[i]*Math.sin(angle) + yOrigPtsTurret[i]*Math.cos(angle)+posY+.5);

            xOrigPtsBarrelInt[i] = (int)(xOrigPtsBarrel[i]*Math.cos(angle) - yOrigPtsBarrel[i]*Math.sin(angle)+posX+.5);
            yOrigPtsBarrelInt[i] = (int)(xOrigPtsBarrel[i]*Math.sin(angle) + yOrigPtsBarrel[i]*Math.cos(angle)+posY+.5);
        }

        g.setColor(Color.gray);
        g.fillPolygon(xOrigPtsBodyInt, yOrigPtsBodyInt, xOrigPtsBodyInt.length);

        collisionPoly.reset();
        for (int i=0; i<xOrigPtsBodyInt.length; i++) {
            collisionPoly.addPoint(xOrigPtsBodyInt[i], yOrigPtsBodyInt[i]);
        }

        g.setColor(Color.darkGray);
        g.fillPolygon(xOrigPtsTurretInt, yOrigPtsTurretInt, xOrigPtsTurretInt.length);
        g.setColor(Color.black);
        g.fillPolygon(xOrigPtsBarrelInt, yOrigPtsBarrelInt, xOrigPtsBarrelInt.length);
    }
}