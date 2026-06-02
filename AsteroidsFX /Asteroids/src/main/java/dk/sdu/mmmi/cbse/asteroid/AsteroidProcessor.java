package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import java.util.Random;

public class AsteroidProcessor implements IEntityProcessingService {

    private static final double SPEED = 0.8;
    private static final int MAX_ASTEROIDS = 8;

    @Override
    public void process(GameData gameData, World world) {
        if (world.getEntities(Asteroid.class).size() < MAX_ASTEROIDS) {
            world.addEntity(createAsteroid(gameData));
        }

        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            double changeX = Math.cos(Math.toRadians(asteroid.getRotation())) * SPEED;
            double changeY = Math.sin(Math.toRadians(asteroid.getRotation())) * SPEED;

            asteroid.setX(asteroid.getX() + changeX);
            asteroid.setY(asteroid.getY() + changeY);

            if (asteroid.getX() < 0) asteroid.setX(gameData.getDisplayWidth());
            if (asteroid.getX() > gameData.getDisplayWidth()) asteroid.setX(0);
            if (asteroid.getY() < 0) asteroid.setY(gameData.getDisplayHeight());
            if (asteroid.getY() > gameData.getDisplayHeight()) asteroid.setY(0);
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
}
