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

        Rectangle2D rect, rect2, rect3;
        Rectangle2D collisionRect, collisionRect2, collisionRect3; // Little buffer added

        rect = new Rectangle2D.Float();
        rect.setFrame(200, 400, 10, 50);
        collisionRect = new Rectangle2D.Float();
        collisionRect.setFrame(185, 385, 40, 80);
        walls.add(rect);
        collisionWalls.add(collisionRect);

        rect2 = new Rectangle2D.Float();
        rect2.setFrame(500, 100, 10, 100);
        collisionRect2 = new Rectangle2D.Float();
        collisionRect2.setFrame(485, 85, 40, 130);
        walls.add(rect2);
        collisionWalls.add(collisionRect2);

        rect3 = new Rectangle2D.Float();
        rect3.setFrame(50, 150, 200, 10);
        collisionRect3 = new Rectangle2D.Float();
        collisionRect3.setFrame(35, 135, 230, 40);
        walls.add(rect3);
        collisionWalls.add(collisionRect3);
    }

    public boolean checkCollision(Point2D p) {
        boolean collision = false;
        for (int i=0; i < collisionWalls.size(); i++) {
            if (collisionWalls.get(i).contains(p)) {
                collision = true;
            }
        }
        return collision;
    }

    public boolean checkCollision(Line2D line2D) {
        boolean collision = false;
        for (int i=0; i < collisionWalls.size(); i++) {
            if (collisionWalls.get(i).intersectsLine(line2D)) {
                collision = true;
            }
        }
        return collision;
    }

    public void paint(Graphics2D g) {
        for (int i=0; i < walls.size(); i++) {
            g.setPaint(Color.BLACK);
            g.draw(walls.get(i));
            g.fill(walls.get(i));
        }
    }
}
