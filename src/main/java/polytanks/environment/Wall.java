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

        Rectangle2D rect, rect2;
        Rectangle2D collisionRect, collisionRect2; // Little buffer added

        rect = new Rectangle2D.Float();
        rect.setFrame(200, 200, 100, 100);
        collisionRect = new Rectangle2D.Float();
        collisionRect.setFrame(185, 185, 130, 130);
        walls.add(rect);
        collisionWalls.add(collisionRect);

        rect2 = new Rectangle2D.Float();
        rect2.setFrame(500, 100, 10, 400);
        collisionRect2 = new Rectangle2D.Float();
        collisionRect2.setFrame(485, 85, 40, 430);
        walls.add(rect2);
        collisionWalls.add(collisionRect2);
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
