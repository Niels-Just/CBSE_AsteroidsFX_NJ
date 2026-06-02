package dk.sdu.mmmi.cbse.enemysystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class EnemyControlSystem implements IEntityProcessingService {

    private static final int DIRECTION_CHANGE_INTERVAL = 60;
    private static final double SPEED = 1.5;
    private static final int MAX_ENEMIES = 2;
    private int frameCount = 0;

    @Override
    public void process(GameData gameData, World world) {
        frameCount++;

        if (world.getEntities(Enemy.class).size() < MAX_ENEMIES) {
            world.addEntity(createEnemy(gameData));
        }

        for (Entity enemy : world.getEntities(Enemy.class)) {
            if (frameCount % DIRECTION_CHANGE_INTERVAL == 0) {
                enemy.setRotation(enemy.getRotation() + (Math.random() * 90 - 45));
            }

            double changeX = Math.cos(Math.toRadians(enemy.getRotation())) * SPEED;
            double changeY = Math.sin(Math.toRadians(enemy.getRotation())) * SPEED;
            enemy.setX(enemy.getX() + changeX);
            enemy.setY(enemy.getY() + changeY);

            if (enemy.getX() < 0) enemy.setX(gameData.getDisplayWidth());
            if (enemy.getX() > gameData.getDisplayWidth()) enemy.setX(0);
            if (enemy.getY() < 0) enemy.setY(gameData.getDisplayHeight());
            if (enemy.getY() > gameData.getDisplayHeight()) enemy.setY(0);
        }
    }

    private Entity createEnemy(GameData gameData) {
        Entity enemy = new Enemy();
        enemy.setPolygonCoordinates(-5, -5, 10, 0, -5, 5);
        enemy.setX(Math.random() * gameData.getDisplayWidth());
        enemy.setY(50);
        enemy.setRotation(Math.random() * 360);
        enemy.setRadius(8);
        enemy.setEntityType("enemy");
        enemy.setHealth(3);
        return enemy;
    }
}
