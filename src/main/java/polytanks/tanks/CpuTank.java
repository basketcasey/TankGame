package polytanks.tanks;

import polytanks.Main;
import polytanks.environment.Wall;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;
import polytanks.utils.GameMathUtils;
import static polytanks.utils.GameMathUtils.CheckAngleBoundary;

public class CpuTank extends Tank {
    UserTank enemyTank;

    public CpuTank(Main main, int xSize, int ySize, int origPosX, int origPosY, Wall wall, UserTank userTank) {
        this.game = main;
        this.wall = wall;
        enemyTank = userTank;
        health = 100;
        screenWidth = xSize; // set screen limits
        screenHeight = ySize; // set screen limits

        posX = startingX = origPosX;  // Set initial position
        posY = startingY = origPosY;  // Set initial position

        velX = 0; // Not moving
        velY = 0; // Not moving
        accelerationRate = .10; // Used to calc velocity
        decay = 0.1; // slowing rate for current velocity (inertia)
        accelDecay = 0.1; // slowing rate for acceleration (movement under power)
        angle = 0;
        turningRate = 0.025; // Turns in radians (0 - 2PI)
    }

    public void move() {
        if (isDestroyed()) {
            return;
        }
        pursue();
        Double origX = posX;
        Double origY = posY;
        if (accelerationRate > 1.5) { accelerationRate = 1.5; }
        if (accelerationRate < -1.5) { accelerationRate = -1.5; }

        if (turningLeft) {
            angle -= turningRate;
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
            accelerationRate += 0.25;
            velX = Math.cos(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            velY = Math.sin(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            accelForward = false;
        }
        if (accelBackward) {
            // move in negative direction for where pointing
            accelerationRate -= 0.25;
            // move in opposite direction where pointing
            velX = Math.cos(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            velY = Math.sin(angle - (Math.PI / 2)) * accelerationRate;  // subtracting PI / 2 corrects direction
            accelBackward = false;
        }

        // Set tanks new position
        posX += velX;
        posY += velY;

        if(game.checkCollisions(this)) {
            velX = velY = 0;
            posX = origX;
            posY = origY;
        }

        // Limit tanks position to screen
        if (posX > screenWidth - 20) {
            posX = screenWidth - 20;
            velX = 0;
        }
        if (posX < 20) {
            posX = 20;
            velX = 0;
        }
        if (posY > screenHeight - 20) {
            posY = screenHeight - 20;
            velY = 0;
        }
        if (posY < 45) {
            posY = 45;
            velY = 0;
        }

        // Modify velocity by slowing in opposite direction
        if (velX > 0) {
            velX -= velX * decay;
            if (velX < 0) {
                velX = 0;
            }
        }
        if (velX < 0) {
            velX -= velX * decay;
            if (velX > 0) {
                velX = 0;
            }
        }
        if (velY > 0) {
            velY -= velY * decay;
            if (velY < 0) {
                velX = 0;
            }
        }
        if (velY < 0) {
            velY -= velY * decay;
            if (velY > 0) {
                velY = 0;
            }
        }
        // Modify accelerationRate by slowing in opposite direction
        if (accelerationRate > 0) { // If positive, reduce by decay
            accelerationRate -= accelerationRate * accelDecay;

            if (accelerationRate < 0.1) {
                accelerationRate = 0; // decaying can't reverse direction so set to zero
            }
        }
        if (accelerationRate < 0) { // If going backwards, slow it by going more positive
            accelerationRate -= accelerationRate * accelDecay;
            if (accelerationRate > -0.1) {
                accelerationRate = 0; // decaying can't reverse direction so set to zero
            }
        }
    }

    public void pursue() {
        // Get enemy tank location
        Point2D enemyLocation = enemyTank.getTankLocation();
        // Get angle to enemy tank
        Double angleToEnemy = GameMathUtils.GetAngleBetweenTwoPoints(this.getTankLocation(), enemyLocation);

        if (!isWallBlockingView()) {
            if (Math.toDegrees(this.angle) >= Math.toDegrees(angleToEnemy) - 3 &&
                    Math.toDegrees(this.angle) <= Math.toDegrees(angleToEnemy) + 3) {
                // Only fire so often
                Random rand = new Random();
                int value = rand.nextInt(30);
                if (value == 5) {
                    game.TankFireCannon(this);
                }
            } else {
                // Rotate tank to target
                if (GameMathUtils.isAngleToLeft(angle, angleToEnemy)) {
                    turningLeft = true;
                } else {
                    turningRight = true;
                }
            }
        } else {
            // Need to drive to better spot
            // Rotate tank to target
            if (GameMathUtils.isAngleToLeft(angle, angleToEnemy)) {
                turningLeft = true;
            } else {
                turningRight = true;
            }
            accelForward = true;
        }

    }

    private boolean isWallBlockingView() {
        // Create line to enemy tank
        // Ask wall to check all walls for intersection with that line
        Line2D line2D = new Line2D.Double();
        line2D.setLine(this.getTankLocation(), enemyTank.getTankLocation());
        return (wall.checkCollision(line2D)) ? true : false;
    }

    private Double getDistanceToEnemy(Point2D enemyLocation, Point2D cpuLocation) {
        return Math.sqrt(Math.pow(getDistanceY(enemyLocation, cpuLocation), 2) +
                Math.pow(getDistanceX(enemyLocation, cpuLocation), 2));
    }

    private Double getDistanceX(Point2D enemyLocation, Point2D cpuLocation) {
        return enemyLocation.getX() - cpuLocation.getX();
    }

    private Double getDistanceY(Point2D enemyLocation, Point2D cpuLocation) {
        return cpuLocation.getY() - enemyLocation.getY();
    }

    public void paint(Graphics2D g) {

        for (int i = 0; i < xOrigPtsBody.length; i++) {
            xOrigPtsBodyInt[i] = (int) (xOrigPtsBody[i] * Math.cos(angle) - yOrigPtsBody[i] * Math.sin(angle) + posX + .5);
            yOrigPtsBodyInt[i] = (int) (xOrigPtsBody[i] * Math.sin(angle) + yOrigPtsBody[i] * Math.cos(angle) + posY + .5);

            xOrigPtsTurretInt[i] = (int) (xOrigPtsTurret[i] * Math.cos(angle) - yOrigPtsTurret[i] * Math.sin(angle) + posX + .5);
            yOrigPtsTurretInt[i] = (int) (xOrigPtsTurret[i] * Math.sin(angle) + yOrigPtsTurret[i] * Math.cos(angle) + posY + .5);

            xOrigPtsBarrelInt[i] = (int) (xOrigPtsBarrel[i] * Math.cos(angle) - yOrigPtsBarrel[i] * Math.sin(angle) + posX + .5);
            yOrigPtsBarrelInt[i] = (int) (xOrigPtsBarrel[i] * Math.sin(angle) + yOrigPtsBarrel[i] * Math.cos(angle) + posY + .5);
        }

        g.setColor(Color.gray);
        g.fillPolygon(xOrigPtsBodyInt, yOrigPtsBodyInt, xOrigPtsBodyInt.length);

        double radius = 40;
        collisionRectangle.setBounds((int)(posX - radius/2), (int)(posY - radius/2), (int)radius, (int)radius);

        g.setColor(Color.blue);
        g.fillPolygon(xOrigPtsTurretInt, yOrigPtsTurretInt, xOrigPtsTurretInt.length);
        g.setColor(Color.black);
        g.fillPolygon(xOrigPtsBarrelInt, yOrigPtsBarrelInt, xOrigPtsBarrelInt.length);
    }
}