package dk.sdu.mmmi.cbse.collisionsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IAsteroidSplitter;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class CollisionDetector implements IPostEntityProcessingService {

    // Let the asteroid feature handle splitting so this part stays separate from it.
    private final ServiceLoader<IAsteroidSplitter> splitterLoader = ServiceLoader.load(IAsteroidSplitter.class);

    @Override
    public void process(GameData gameData, World world) {
        List<Entity> entities = new ArrayList<>(world.getEntities());

        for (int i = 0; i < entities.size(); i++) {
            Entity e1 = entities.get(i);
            if (!world.getEntities().contains(e1)) continue;

            for (int j = i + 1; j < entities.size(); j++) {
                Entity e2 = entities.get(j);
                if (!world.getEntities().contains(e2)) continue;
                // You can't be hit by your own bullet.
                if (isOwnBullet(e1, e2)) continue;
                // Let asteroids overlap so new split pieces don't instantly kill each other.
                if (isAsteroid(e1) && isAsteroid(e2)) continue;

                if (collides(e1, e2)) {
                    handleCollision(e1, e2, gameData, world);
                    break;
                }
            }
        }
    }

    private void handleCollision(Entity e1, Entity e2, GameData gameData, World world) {
        boolean b1 = isBullet(e1);
        boolean b2 = isBullet(e2);

        if (b1 && b2) {
            world.removeEntity(e1);
            world.removeEntity(e2);
            return;
        }

        if (b1 ^ b2) {
            Entity bullet = b1 ? e1 : e2;
            Entity target = b1 ? e2 : e1;
            boolean playerBullet = "playerBullet".equals(bullet.getEntityType());

            world.removeEntity(bullet);

            if (isAsteroid(target)) {
                world.removeEntity(target);
                splitAsteroid(target, world);
                if (playerBullet) gameData.setScore(gameData.getScore() + 1);
            } else {
                target.setHealth(target.getHealth() - 1);
                if (target.getHealth() <= 0) {
                    world.removeEntity(target);
                    if (playerBullet) gameData.setScore(gameData.getScore() + 1);
                }
            }
            return;
        }

        world.removeEntity(e1);
        world.removeEntity(e2);
    }

    private void splitAsteroid(Entity asteroid, World world) {
        for (IAsteroidSplitter splitter : splitterLoader) {
            splitter.createSplitAsteroid(asteroid, world);
        }
    }

    private boolean collides(Entity e1, Entity e2) {
        double dx = e1.getX() - e2.getX();
        double dy = e1.getY() - e2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (e1.getRadius() + e2.getRadius());
    }

    private boolean isBullet(Entity e) {
        return "playerBullet".equals(e.getEntityType()) || "enemyBullet".equals(e.getEntityType());
    }

    private boolean isAsteroid(Entity e) {
        return "asteroid".equals(e.getEntityType());
    }

    private boolean isOwnBullet(Entity e1, Entity e2) {
        return ("playerBullet".equals(e1.getEntityType()) && "player".equals(e2.getEntityType()))
            || ("player".equals(e1.getEntityType()) && "playerBullet".equals(e2.getEntityType()))
            || ("enemyBullet".equals(e1.getEntityType()) && "enemy".equals(e2.getEntityType()))
            || ("enemy".equals(e1.getEntityType()) && "enemyBullet".equals(e2.getEntityType()));
    }
}
