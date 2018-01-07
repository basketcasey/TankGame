package polytanks.environment;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Wall {
    ArrayList<Rectangle2D> walls = new ArrayList<Rectangle2D>();
    ArrayList<Rectangle2D> collisionWalls = new ArrayList<Rectangle2D>();

    private int screenWidth;
    private int screenHeight;

    public Wall(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        Rectangle2D rect1 = new Rectangle2D.Float();
        Rectangle2D rect2 = new Rectangle2D.Float();
        Rectangle2D rect3 = new Rectangle2D.Float();
        Rectangle2D rect4 = new Rectangle2D.Float();
        Rectangle2D rect5 = new Rectangle2D.Float();
        Rectangle2D rect6 = new Rectangle2D.Float();
        Rectangle2D rect7 = new Rectangle2D.Float();
        Rectangle2D rect8 = new Rectangle2D.Float();
        Rectangle2D rect9 = new Rectangle2D.Float();

        rect1.setFrame(400, 45, 20, 155);
        walls.add(rect1);
        rect2.setFrame(400, 400, 20, 200);
        walls.add(rect2);

        rect3.setFrame(100, 150, 200, 20);
        walls.add(rect3);
        rect4.setFrame(100, 450, 200, 20);
        walls.add(rect4);
        rect5.setFrame(1, 300, 100, 20);
        walls.add(rect5);

        rect6.setFrame(520, 150, 200, 20);
        walls.add(rect6);
        rect7.setFrame(520, 450, 200, 20);
        walls.add(rect7);
        rect8.setFrame(700, 300, 100, 20);
        walls.add(rect8);

        rect9.setFrame(300, 300, 220, 20);
        walls.add(rect9);
    }

    public boolean checkCollision(Point2D p) {
        boolean collision = false;
        for (int i=0; i < walls.size(); i++) {
            if (walls.get(i).contains(p)) {
                collision = true;
            }
        }
        return collision;
    }

    public boolean checkCollision(Line2D line2D) {
        boolean collision = false;
        for (int i=0; i < walls.size(); i++) {
            if (walls.get(i).intersectsLine(line2D)) {
                collision = true;
            }
        }
        return collision;
    }

    public boolean checkCollision(Rectangle rect) {
        boolean collision = false;
        for (int i=0; i < walls.size(); i++) {
            if (walls.get(i).intersects(rect)) {
                collision = true;
            }
        }
        return collision;
    }

    public void paint(Graphics2D g) {
        for (int i=0; i < walls.size(); i++) {
            g.setPaint(new Color(6, 40, 18));
            g.draw(walls.get(i));
            g.fill(walls.get(i));
        }
    }
}
