package polytanks;

import polytanks.environment.Wall;
import polytanks.tanks.CpuTank;
import polytanks.tanks.Tank;
import polytanks.weapons.TankShot;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import javax.swing.JFrame;

/* This is the best motion created so far */

public class Main extends JFrame {
    boolean leftPressed, rightPressed, upPressed, downPressed, firePressed;
    private Tank tank;
    private CpuTank cpuTank, cpuTank2;
    TankShot shot, cpushot, cpushot2 = null;

    private boolean gameRunning;
    private int screenWidth = 800;
    private int screenHeight = 600;
    private Wall wall;

    // To get around static class issues for main game, an instance is created
    // and control is immediately handed here
    public Main() {
        // Create game objects that load images early so they are available to draw
        // once the display is created
        wall = new Wall(screenWidth, screenHeight);
        tank = new Tank(screenWidth, screenHeight, 100, 100, wall);
        cpuTank = new CpuTank(this, screenWidth, screenHeight, screenWidth/2, screenHeight/2, wall, tank);
        cpuTank2 = new CpuTank(this, screenWidth, screenHeight, screenWidth - 40, screenHeight - 40, wall, tank);

        // Define the frame variables
        setSize(screenWidth, screenHeight);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(new KeyInputHandler());

        // Start the game loop where all the actual game play comes from
        gameRunning = true;
        gameLoop();
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        // Reset frame to a fresh background
        g2.setColor(Color.white);
        g2.fillRect(0, 0, 800, 600);

        // Add the tank
        tank.paint(g2);
        cpuTank.paint(g2);
        cpuTank2.paint(g2);
        wall.paint(g2);

        // If took a shot, draw it
        if (shot != null) {
            shot.move(wall);
            shot.paint(g2);
            if (shot.getShotComplete()) {
                shot = null;
            }
        }

        // If CPU tank took a shot, draw it
        if (cpushot != null) {
            cpushot.move(wall);
            cpushot.paint(g2);
            if (cpushot.getShotComplete()) {
                cpushot = null;
            }
        }

        // If CPU tank took a shot, draw it
        if (cpushot2 != null) {
            cpushot2.move(wall);
            cpushot2.paint(g2);
            if (cpushot2.getShotComplete()) {
                cpushot2 = null;
            }
        }
    }

    private void gameLoop() {
        while (gameRunning) {
            processUserControls();
            tank.move();
            cpuTank.move();
            cpuTank2.move();
            repaint();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // Needed for long presses of buttons
    private void processUserControls() {
        if (leftPressed) {
            tank.setTurningLeft();
        }
        if (rightPressed) {
            tank.setTurningRight();
        }
        if (upPressed) {
            tank.setAccelForward();
        }
        if (downPressed) {
            tank.setAccelBackward();
        }
    }

    public void CpuTankFireCannon(CpuTank tank) {
        double shotAngle = tank.getBarrelAngle() - (Math.PI / 2);
        Point barrelTip = tank.getBarrelPosition();
        if (cpushot == null) {
            cpushot = new TankShot(screenWidth, screenHeight, shotAngle, barrelTip.x, barrelTip.y);
        }
    }

    private void FireTankCannon() {
        // shoot in angle currently pointing
        Double shotAngle = tank.getBarrelAngle() - (Math.PI / 2);

        // locate the tip of the barrel so the shot comes from the right part of tank
        Point barrelTip = tank.getBarrelPosition();

        // can only take one shot at a time
        if (shot == null) {
            shot = new TankShot(screenWidth, screenHeight, shotAngle, barrelTip.x, barrelTip.y);
        }

        // Need shot object array to hold active shots
        // iterate through shot array to pass graphics object to draw on
        // shot object keeps track of its own ordinance position
    }


    public static void main(String[] args) {
        Main main = new Main();
    }

    private class KeyInputHandler extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                upPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                FireTankCannon();
            }
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                upPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                downPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                firePressed = false;
            }
        }

        public void keyTyped(KeyEvent e) {
            // if we hit escape, then quit the game
            if (e.getKeyChar() == 27) {
                System.exit(0);
            }
        }
    }
}