package polytanks.tanks;

import java.awt.*;

public abstract class Tank {
    private static int id = 100;
    public int tankId;

    public Tank() {
        this.tankId = id++;
    }

    public abstract double getBarrelAngle();
    public abstract Point getBarrelPosition();

}
