package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Updates a feature's entities once per frame (movement, shooting, spawning).
 * Implementations are loaded at runtime via ServiceLoader.
 */
public interface IEntityProcessingService {

    /**
     * Updates this feature's entities for one frame.
     * Pre: gameData and world are non-null.
     * Post: entity state is updated; entities may be added or removed.
     *
     * @param gameData shared game state
     * @param world    entity container to read and update
     */
    void process(GameData gameData, World world);
}
