package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;

import java.util.Random;

public class AsteroidPlugin implements IGamePluginService {

    private static final int ASTEROID_COUNT = 3;

    @Override
    public void start(GameData gameData, World world) {
        for (int i = 0; i < ASTEROID_COUNT; i++) {
            world.addEntity(createAsteroid(gameData));
        }
    }

    private Entity createAsteroid(GameData gameData) {
        Random rnd = new Random();
        int size = rnd.nextInt(10) + 10;

        Entity asteroid = new Asteroid();
        asteroid.setPolygonCoordinates(size, -size, -size, -size, -size, size, size, size);
        asteroid.setRadius(size);
        asteroid.setRotation(rnd.nextInt(360));
        asteroid.setEntityType("asteroid");

        int edge = rnd.nextInt(4);
        if (edge == 0) {
            asteroid.setX(rnd.nextInt(gameData.getDisplayWidth()));
            asteroid.setY(0);
        } else if (edge == 1) {
            asteroid.setX(gameData.getDisplayWidth());
            asteroid.setY(rnd.nextInt(gameData.getDisplayHeight()));
        } else if (edge == 2) {
            asteroid.setX(rnd.nextInt(gameData.getDisplayWidth()));
            asteroid.setY(gameData.getDisplayHeight());
        } else {
            asteroid.setX(0);
            asteroid.setY(rnd.nextInt(gameData.getDisplayHeight()));
        }

        return asteroid;
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            world.removeEntity(asteroid);
        }
    }
}
