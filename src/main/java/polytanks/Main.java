package polytanks;

import polytanks.environment.Wall;
import polytanks.tanks.CpuTank;
import polytanks.tanks.Tank;
import polytanks.tanks.UserTank;
import polytanks.weapons.TankShot;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

/* This is the best motion created so far */

public class Main extends JFrame {
    boolean leftPressed, rightPressed, upPressed, downPressed, firePressed, startGamePressed;
    private UserTank userTank;

    private boolean gameRunning;
    private int screenWidth = 800;
    private int screenHeight = 600;
    private int health = 100;
    private int score = 0;
    private Wall wall;

    private HashMap<Tank, TankShot> shots = new HashMap<>();
    private HashMap<String, Tank> CpuTanks = new HashMap<>();

    // To get around static class issues for main game, an instance is created
    // and control is immediately handed here
    public Main() {
        initGame();
    }

    private void initGame() {
        removeGameArtifacts();
        leftPressed = rightPressed = upPressed = downPressed = false;
        health = 100;
        score = 0;

        wall = new Wall(screenWidth, screenHeight);

        userTank = new UserTank(screenWidth, screenHeight, 100, 100, wall);
        CpuTanks.put("CpuTank1", new CpuTank(this, screenWidth, screenHeight, screenWidth/2, screenHeight/2, wall, userTank));
        CpuTanks.put("CpuTank2", new CpuTank(this, screenWidth, screenHeight, screenWidth - 40, screenHeight - 40, wall, userTank));

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

    private void removeGameArtifacts() {
        userTank = null;
        CpuTanks.clear();
        wall = null;
        shots.clear();
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        // Reset frame to a fresh background
        g2.setColor(Color.white);
        g2.fillRect(0, 0, 800, 600);

        // Add scoring
//        RenderingHints rh =
//                new RenderingHints(RenderingHints.KEY_ANTIALIASING,
//                        RenderingHints.VALUE_ANTIALIAS_ON);
//
//        rh.put(RenderingHints.KEY_RENDERING,
//                RenderingHints.VALUE_RENDER_QUALITY);
//        g2.setRenderingHints(rh);
        g2.setColor(Color.BLUE);
        g2.setFont(new Font("Purisa", Font.PLAIN, 18));
        g2.drawString("UserTank Battle", 20, 40);
        g2.drawString("Health: " + health + "%   Score: " + score, screenWidth/2 - 100, 40 );

        if (health <= 0) {
            health = 0;
            g2.drawString("Game Over - Press N to start game", screenWidth/2  - 100, screenHeight/2 - 40);
        }

        // Add the tanks
        userTank.paint(g2);

        for(Map.Entry<String, Tank> entry : CpuTanks.entrySet()) {
           Tank tank = entry.getValue();
           tank.paint(g2);
        }

        // Add the obstructions
        wall.paint(g2);

        // handle shots taken
        for(Map.Entry<Tank, TankShot> entry : shots.entrySet()) {
            TankShot shot = entry.getValue();
            shot.paint(g2);
        }


    }

    private void gameLoop() {
        while (health > 0) {
            processUserControls();
            // Move tanks
            userTank.move();
            for(Map.Entry<String, Tank> entry : CpuTanks.entrySet()) {
                Tank tank = entry.getValue();
                tank.move();
            }

            // Check for collisions
            // Shots
            ArrayList<Tank> tankShotsToDelete = new ArrayList<>();
            int count=0;
            for(Map.Entry<Tank, TankShot> entry : shots.entrySet()) {
                Tank tank = entry.getKey();
                TankShot shot = entry.getValue();
                Point2D shotLoc = shot.getPosition();
                shot.move(wall);
                // Did shot hit a wall?
                if (shot.getShotComplete()) {
                    // queue shots to delete - Deleting here causes exception
                    tankShotsToDelete.add(tank);
                }
                // Did shot hit a tank?
                if (tank instanceof CpuTank) {
                    if (userTank.checkCollision(shotLoc)) {
                        health -= 25;
                        tankShotsToDelete.add(tank);
                    }
                }

                if (tank instanceof UserTank) {
                    for (Map.Entry<String, Tank> entry2 : CpuTanks.entrySet()) {
                        Tank testTank = entry2.getValue();
                        // check collision
                        if (testTank.checkCollision(shotLoc)) {
                            score += 100;
                            tankShotsToDelete.add(tank); // References the shooting tank not target tank
                        }
                    }
                }
            }
            // Delete completed shots
            for (int i=0; i < tankShotsToDelete.size(); i++) {
                shots.remove(tankShotsToDelete.get(i));
            }

            repaint();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        while (health <= 0) {
            // wait for new game request
            processUserControls();
        }
    }

    // Needed for long presses of buttons
    private void processUserControls() {
        if (leftPressed) {
            userTank.setTurningLeft();
        }
        if (rightPressed) {
            userTank.setTurningRight();
        }
        if (upPressed) {
            userTank.setAccelForward();
        }
        if (downPressed) {
            userTank.setAccelBackward();
        }
        if (startGamePressed) {
            startGamePressed = false;
            initGame();
        }
    }

    public void TankFireCannon(Tank tank) {
        double shotAngle = tank.getBarrelAngle() - (Math.PI / 2);
        Point barrelTip = tank.getBarrelPosition();
        if (!shots.containsKey(tank)) {
            shots.put(tank, new TankShot(screenWidth, screenHeight, shotAngle, barrelTip.x, barrelTip.y));
        }
    }

    private void FireUserTankCannon() {
        TankFireCannon(userTank);
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
                FireUserTankCannon();
            }
            if (e.getKeyCode() == KeyEvent.VK_N) {
                startGamePressed = true;
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