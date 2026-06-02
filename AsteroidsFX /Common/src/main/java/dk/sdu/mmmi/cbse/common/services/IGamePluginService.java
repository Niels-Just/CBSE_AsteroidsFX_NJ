package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Adds and removes a feature's entities when the game starts and stops.
 * Implementations are loaded at runtime via ServiceLoader.
 */
public interface IGamePluginService {

    /**
     * Adds this plugin's entities to the world.
     * Pre: gameData and world are non-null.
     * Post: the plugin's entities exist in the world.
     *
     * @param gameData shared game state
     * @param world entity container to add to
     */
    void start(GameData gameData, World world);

    /**
     * Removes the entities added by start.
     * Pre: start was called before.
     * Post: the plugin's entities are removed from the world.
     *
     * @param gameData shared game state
     * @param world entity container to remove from
     */
    void stop(GameData gameData, World world);
}
