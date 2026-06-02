package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class BulletControlSystem implements IEntityProcessingService {

    private static final double SPEED = 10;
    private static final double ENEMY_FIRE_CHANCE = 0.005;

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getKeys().isPressed(GameKeys.SPACE)) {
            for (Entity entity : world.getEntities()) {
                if ("player".equals(entity.getEntityType())) {
                    world.addEntity(createBullet(entity, "playerBullet"));
                }
            }
        }

        for (Entity entity : world.getEntities()) {
            if ("enemy".equals(entity.getEntityType()) && Math.random() < ENEMY_FIRE_CHANCE) {
                world.addEntity(createBullet(entity, "enemyBullet"));
            }
        }

        for (Entity bullet : world.getEntities(Bullet.class)) {
            double changeX = Math.cos(Math.toRadians(bullet.getRotation())) * SPEED;
            double changeY = Math.sin(Math.toRadians(bullet.getRotation())) * SPEED;
            bullet.setX(bullet.getX() + changeX);
            bullet.setY(bullet.getY() + changeY);

            if (bullet.getX() < 0 || bullet.getX() > gameData.getDisplayWidth()
                    || bullet.getY() < 0 || bullet.getY() > gameData.getDisplayHeight()) {
                world.removeEntity(bullet);
            }
        }
    }

    private Entity createBullet(Entity shooter, String bulletType) {
        Entity bullet = new Bullet();
        bullet.setPolygonCoordinates(-2, -2, 2, -2, 2, 2, -2, 2);
        bullet.setRadius(2);
        bullet.setEntityType(bulletType);
        bullet.setX(shooter.getX());
        bullet.setY(shooter.getY());
        bullet.setRotation(shooter.getRotation());
        return bullet;
    }
}
