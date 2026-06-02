package dk.sdu.mmmi.cbse.common.services;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 * Runs each frame after all entities have moved, for cross-entity work
 * like collision detection and scoring.
 * Implementations are loaded at runtime via ServiceLoader.
 */
public interface IPostEntityProcessingService {

    /**
     * Applies cross-entity effects for one frame.
     * Pre: all entity processing has finished, so positions are final.
     * Post: collisions are resolved and the score may be updated.
     *
     * @param gameData shared game state
     * @param world entity container to inspect and update
     */
    void process(GameData gameData, World world);
}
