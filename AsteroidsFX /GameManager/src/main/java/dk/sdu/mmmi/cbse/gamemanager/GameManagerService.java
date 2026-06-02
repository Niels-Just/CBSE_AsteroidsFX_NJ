package dk.sdu.mmmi.cbse.gamemanager;

import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameState;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import org.springframework.web.client.RestTemplate;

public class GameManagerService implements IPostEntityProcessingService {

    private static final String SCORES_URL = "http://localhost:8080/scores";

    private boolean playerWasAlive = false;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) {
            playerWasAlive = false;
            return;
        }

        boolean playerAlive = world.getEntities().stream().anyMatch(e -> "player".equals(e.getEntityType()));

        // Only react the moment the player dies, not every frame after.
        if (playerWasAlive && !playerAlive) {
            gameData.setGameState(GameState.GAME_OVER);
            submitScore(gameData);
        }

        playerWasAlive = playerAlive;
    }

    private void submitScore(GameData gameData) {
        try {
            // Send and read the score as plain text to keep it simple.
            restTemplate.postForLocation(SCORES_URL + "/submit/" + gameData.getScore(), null);
            String body = restTemplate.getForObject(SCORES_URL + "/highscore", String.class);
            if (body != null) {
                gameData.setHighScore(Integer.parseInt(body.trim()));
            }
        } catch (Exception e) {
            // If the score server is off, just remember the high score here.
            System.err.println("Scoring service unavailable: " + e.getMessage());
            if (gameData.getScore() > gameData.getHighScore()) {
                gameData.setHighScore(gameData.getScore());
            }
        }
    }
}
