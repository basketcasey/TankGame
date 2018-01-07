package polytanks;

import org.w3c.dom.css.Rect;
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
import java.util.Random;

import javax.swing.JFrame;

/* This is the best motion created so far */

public class Main extends JFrame {
    boolean leftPressed, rightPressed, upPressed, downPressed, firePressed, startGamePressed;
    private UserTank userTank;

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
        Random rand = new Random();
        leftPressed = rightPressed = upPressed = downPressed = false;
        removeGameArtifacts();
        wall = new Wall(screenWidth, screenHeight);
        userTank = new UserTank(this, screenWidth, screenHeight, 50, 60);

        // Add CPU tanks
        for (int i=1; i <= level; i++) {
            int angle = rand.nextInt(360);

            int valueX = rand.nextInt(screenWidth);
            int valueY = rand.nextInt(screenHeight);

            double radius = 40;
            Rectangle rect = new Rectangle((int)(valueX-radius/2), (int)(valueY-radius/2), (int)radius, (int)radius);
            Tank ghost = new CpuTank(this, screenWidth, screenHeight, valueX, valueY, wall, userTank);
            ghost.setCollisionRectangle(rect);
            ghost.setAngle(Math.toRadians(angle));

            while (checkCollisions(ghost)) { // find a place for this thing
                valueX = rand.nextInt(screenWidth);
                valueY = rand.nextInt(screenHeight);
                rect = new Rectangle((int)(valueX-radius/2), (int)(valueY-radius/2), (int)radius, (int)radius);
                ghost = new CpuTank(this, screenWidth, screenHeight, valueX, valueY, wall, userTank);
                ghost.setCollisionRectangle(rect);
            }
            CpuTanks.put("CpuTank" + i, ghost);
        }

        // Define the frame variables
        setSize(screenWidth, screenHeight);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(new KeyInputHandler());

        // Start the game loop where all the actual game play comes from
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
        g2.setColor(new Color(186, 219, 197));
        g2.fillRect(0, 0, 800, 600);

        // Add scoring
//        RenderingHints rh =
//                new RenderingHints(RenderingHints.KEY_ANTIALIASING,
//                        RenderingHints.VALUE_ANTIALIAS_ON);
//
//        rh.put(RenderingHints.KEY_RENDERING,
//                RenderingHints.VALUE_RENDER_QUALITY);
//        g2.setRenderingHints(rh);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Purisa", Font.PLAIN, 18));
        g2.drawString("Tank Battle", 20, 40);
        g2.drawString("Level: " + level + "  Health: " + userTank.getHealth() +
                "%  Score: " + score, screenWidth/2 - 140, 40 );

        if (userTank.isDestroyed()) {
            g2.drawString("Game Over - Press N to start game", screenWidth/2  - 140, screenHeight/2 - 60);
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

    // Ensure tank doesn't drive through another tank or a wall
    public boolean checkCollisions(Tank tank) {
        if (wall.checkCollision(tank.getCollisionRectangle())) {
            return true;
        }
        // Can't drive through tanks
        if (!tank.equals(userTank)){ // Skip if this is the same tank
            // Check for collision with user
            if (userTank.getCollisionRectangle().intersects(tank.getCollisionRectangle())) {
                return true;
            }
        }

        for(Map.Entry<String, Tank> entry : CpuTanks.entrySet()) {
            Tank testTank = entry.getValue();
            if (!tank.equals(testTank)) { // Skip if this is the same tank
                if(testTank.getCollisionRectangle().intersects(tank.getCollisionRectangle())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void gameLoop() {
        while (userTank.getHealth() > 0) {
            processUserControls();
            userTank.move(); // Move user tank per user input

            HashMap<String, Tank> tanksToDelete = new HashMap<>();
            for(Map.Entry<String, Tank> entry : CpuTanks.entrySet()) {
                Tank tank = entry.getValue();
                if (tank.isDestroyed()) {
                    tanksToDelete.put(entry.getKey(), tank);
                } else {
                    tank.move(); // CPU tanks move based on pursue method
                }
            }

            // Delete destroyed tanks
            for(Map.Entry<String, Tank> entry : tanksToDelete.entrySet()) {
                CpuTanks.remove(entry.getKey(), entry.getValue());
            }

            // Check for board cleared
            if (CpuTanks.isEmpty()) {
                initGame(); // start next level
            }

            // Check shots for collisions
            ArrayList<Tank> tankShotsToDelete = new ArrayList<>();
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