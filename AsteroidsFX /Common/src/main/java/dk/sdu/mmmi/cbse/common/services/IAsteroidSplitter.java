package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.World;

public interface IAsteroidSplitter {

    void createSplitAsteroid(Entity asteroid, World world);
}
