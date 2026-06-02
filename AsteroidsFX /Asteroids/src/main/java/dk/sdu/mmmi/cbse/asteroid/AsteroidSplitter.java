package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IAsteroidSplitter;

import java.util.Random;

public class AsteroidSplitter implements IAsteroidSplitter {

    private static final float MIN_SPLIT_RADIUS = 10f;

    @Override
    public void createSplitAsteroid(Entity asteroid, World world) {
        // Too small to split into new pieces, so do nothing.
        if (asteroid.getRadius() < MIN_SPLIT_RADIUS) return;

        int newSize = (int) (asteroid.getRadius() / 2);
        Random rnd = new Random();

        for (int i = 0; i < 2; i++) {
            Entity piece = new Asteroid();
            piece.setPolygonCoordinates(newSize, -newSize, -newSize, -newSize, -newSize, newSize, newSize, newSize);
            piece.setRadius(newSize);
            piece.setRotation(rnd.nextInt(360));
            piece.setX(asteroid.getX());
            piece.setY(asteroid.getY());
            piece.setEntityType("asteroid");
            world.addEntity(piece);
        }
    }
}
