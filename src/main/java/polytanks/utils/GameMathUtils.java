package polytanks.utils;

import java.awt.geom.Point2D;

public class GameMathUtils {
    final static Double maxRadsInCircle = 2 * Math.PI;

    public static Double CheckAngleBoundary(Double angle){
        if (Math.toDegrees(angle) < 0) {
            angle = Math.abs(angle);
            angle = (Math.PI * 2) - angle;
            return angle;
        } else {
            return (angle >= maxRadsInCircle) ? angle = angle - maxRadsInCircle : angle;
        }
    }

    public static Double GetAngleBetweenTwoPoints(Point2D a, Point2D b) {
        Double result = 0.0;
        //  a is origin of xy plane, figure out what quadrant

        //  II  |  I
        //  III |  IV

        if ( (b.getX() >= a.getX()) && (b.getY() <= a.getY())) {
            // I
            result = Math.atan(getXbetweenTwoPoints(a,b)/getYbetweenTwoPoints(a,b)) * -1;
            return result;
        }

        if ( (b.getX() <= a.getX()) && (b.getY()  <= a.getY())) {
            // II
            result = Math.atan(getYbetweenTwoPoints(b,a)/getXbetweenTwoPoints(b, a));
            result += Math.PI * 1.5;
            return result;
        }

        if ( (b.getX() <= a.getX()) && (b.getY() >= a.getY())) {
            // III
            result = Math.atan(getXbetweenTwoPoints(a,b)/getYbetweenTwoPoints(b, a));
            result += Math.PI;
            return result;
        }

        if ( (b.getX() >= a.getX()) && (b.getY() >= a.getY())) {
            // IV
            result = Math.atan(getYbetweenTwoPoints(b,a)/getXbetweenTwoPoints(b, a));
            result += Math.PI/2;
            return result;
        }
        return result;
    }

    public static boolean isAngleToLeft(double t1, double t2) {
        t1 = GameMathUtils.CheckAngleBoundary(t1);
        t2 = GameMathUtils.CheckAngleBoundary(t2);
        // Normalize t1 to 180 degrees to simplify calculation
        double offset = t1 - Math.PI;

        t1 -= offset;
        t2 -= offset;
        t1 = GameMathUtils.CheckAngleBoundary(t1);
        t2 = GameMathUtils.CheckAngleBoundary(t2);
        double t1High = t1 + Math.PI;
        return (t1 >= t2 && t2 <= t1High) ? true : false;
    }

    // Find location from a point by angle and X distance from that point
    public static Point2D getPointForAnglePoint(double angle, Point2D point, double distance) {
        Point2D result = new Point2D.Double();
        double y = distance/Math.tan(angle);

        // if angle 0-180, x is added, else subtracted
        if (Math.toDegrees(angle) <= 180) {
            result.setLocation(point.getX() + distance,point.getY() - y);
        } else {
            result.setLocation(point.getX() - distance,point.getY() - y);
        }
        return result;
    }

    public static double getYbetweenTwoPoints(Point2D a, Point2D b) {
        return a.getY() - b.getY();
    }

    public static double getXbetweenTwoPoints(Point2D a, Point2D b) {
        return a.getX() - b.getX();
    }

    public static Double GetHypotenusForTwoPoints(Point2D a, Point2D b) {
        return Math.sqrt(Math.pow(getYbetweenTwoPoints(a, b), 2) +
          Math.pow(getXbetweenTwoPoints(a, b), 2));
    }


}
