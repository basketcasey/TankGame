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
    private int score = 0;
    private int level = 0;
    private Wall wall;

    private HashMap<Tank, TankShot> shots = new HashMap<>();
    private HashMap<String, Tank> CpuTanks = new HashMap<>();

    // To get around static class issues for main game, an instance is created
    // and control is immediately handed here
    public Main() {
        score = 0;
        initGame();
    }

    private void initGame() {
        level++;
        removeGameArtifacts();
        leftPressed = rightPressed = upPressed = downPressed = false;

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
        g2.drawString("Level: " + level + "  Health: " + userTank.getHealth() +
                "%  Score: " + score, screenWidth/2 - 150, 40 );

        if (userTank.isDestroyed()) {
            g2.drawString("Game Over - Press N to start game", screenWidth/2  - 100, screenHeight/2 - 40);
        }

        // Add the tanks
        userTank.paint(g2);

        for(Map.Entry<String, Tank> entry : CpuTanks.entrySet()) {
           Tank tank = entry.getValue();
           if (!tank.isDestroyed()) {
               tank.paint(g2);
           }
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
        while (userTank.getHealth() > 0) {
            processUserControls();
            // Move tanks
            userTank.move();

            HashMap<String, Tank> tanksToDelete = new HashMap<>();
            for(Map.Entry<String, Tank> entry : CpuTanks.entrySet()) {
                Tank tank = entry.getValue();
                if (tank.isDestroyed()) {
                    tanksToDelete.put(entry.getKey(), tank);
                } else {
                    tank.move();
                }
            }

            // Delete destroyed tanks
            for(Map.Entry<String, Tank> entry : tanksToDelete.entrySet()) {
                System.out.println("Removing CPU tank");
                CpuTanks.remove(entry.getKey(), entry.getValue());
                System.out.println(CpuTanks.isEmpty());
                System.out.println(CpuTanks.size());
            }

            // Check for board cleared
            if (CpuTanks.isEmpty()) {
                //restart
                initGame();
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
                        userTank.processHit();
                        tankShotsToDelete.add(tank);
                    }
                }

                if (tank instanceof UserTank) {
                    for (Map.Entry<String, Tank> entry2 : CpuTanks.entrySet()) {
                        Tank testTank = entry2.getValue();
                        // check collision
                        if (testTank.checkCollision(shotLoc)) {
                            score += 100;
                            testTank.processHit();
                            tankShotsToDelete.add(tank); // References the shooting tank not target tank
                        }
                    }
                }
            }
            // Delete completed shots
            for (int i=0; i < tankShotsToDelete.size(); i++) {
                shots.remove(tankShotsToDelete.get(i));
            }

            // Check if cpu tanks are both destroyed


            repaint();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        // Handle ended game
        while (userTank.getHealth() <= 0) {
            // wait for new game request
            if (startGamePressed == true) {
                startGamePressed = false;
                score = 0;
                level = 0;
                initGame();
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
                if (userTank.getHealth() <= 0) {
                    startGamePressed = true;
                }
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