package dk.sdu.mmmi.cbse.playersystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerControlSystemTest {

    private GameData gameData;
    private World world;
    private Entity player;
    private PlayerControlSystem system;

    @BeforeEach
    void setUp() {
        gameData = new GameData();
        world = new World();
        player = new Player();
        player.setX(400);
        player.setY(400);
        player.setRotation(0);
        world.addEntity(player);
        system = new PlayerControlSystem();
    }

    @Test
    void rotatesLeftWhenLeftKeyHeld() {
        gameData.getKeys().setKey(GameKeys.LEFT, true);
        system.process(gameData, world);
        assertEquals(-5, player.getRotation(), 0.001);
    }

    @Test
    void rotatesRightWhenRightKeyHeld() {
        gameData.getKeys().setKey(GameKeys.RIGHT, true);
        system.process(gameData, world);
        assertEquals(5, player.getRotation(), 0.001);
    }

    @Test
    void movesForwardWhenUpKeyHeld() {
        gameData.getKeys().setKey(GameKeys.UP, true);
        system.process(gameData, world);
        assertEquals(401, player.getX(), 0.001);
        assertEquals(400, player.getY(), 0.001);
    }

    @Test
    void wrapsAroundLeftEdge() {
        player.setX(-1);
        system.process(gameData, world);
        assertEquals(gameData.getDisplayWidth(), player.getX(), 0.001);
    }
}
