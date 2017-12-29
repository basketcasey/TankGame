package polytanks;

import polytanks.environment.Wall;
import polytanks.tanks.CpuTank;
import polytanks.tanks.Tank;
import polytanks.tanks.UserTank;
import polytanks.weapons.TankShot;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

/* This is the best motion created so far */

public class Main extends JFrame {
    boolean leftPressed, rightPressed, upPressed, downPressed, firePressed;
    private UserTank userTank;
    private CpuTank cpuTank, cpuTank2;
    TankShot shot, cpushot, cpushot2 = null;

    private boolean gameRunning;
    private int screenWidth = 800;
    private int screenHeight = 600;
    private int health = 100;
    private int score = 0;
    private Wall wall;

    private HashMap<Tank, TankShot> shots = new HashMap<>();

    // To get around static class issues for main game, an instance is created
    // and control is immediately handed here
    public Main() {
        // Create game objects that load images early so they are available to draw
        // once the display is created
        wall = new Wall(screenWidth, screenHeight);
        userTank = new UserTank(screenWidth, screenHeight, 100, 100, wall);
        cpuTank = new CpuTank(this, screenWidth, screenHeight, screenWidth/2, screenHeight/2, wall, userTank);
        cpuTank2 = new CpuTank(this, screenWidth, screenHeight, screenWidth - 40, screenHeight - 40, wall, userTank);

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

        // Add the tanks
        userTank.paint(g2);
        cpuTank.paint(g2);
        cpuTank2.paint(g2);
        wall.paint(g2);

        // handle shots taken
        ArrayList<Tank> tankShotsToDelete = new ArrayList<>();
        for(Map.Entry<Tank, TankShot> entry : shots.entrySet()) {
            Tank tank = entry.getKey();
            TankShot shot = entry.getValue();
            shot.move(wall);
            shot.paint(g2);
            if (shot.getShotComplete()) {
                // queue shots to delete - Deleting here causes exception
                tankShotsToDelete.add(tank);
            }
        }
        for (int i=0; i < tankShotsToDelete.size(); i++) {
            shots.remove(tankShotsToDelete.get(i));
        }

    }

    private void gameLoop() {
        while (gameRunning) {
            processUserControls();
            userTank.move();
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