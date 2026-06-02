package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.GameState;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

class   Game {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();

    private final Pane gameWindow = new Pane();
    private final Text scoreText = new Text(10, 20, "Score: 0");

    private final StackPane root = new StackPane();
    private final VBox overlay = new VBox(24);
    private final Text titleText = new Text();
    private final Text overlayScoreText = new Text();
    private final Text highScoreText = new Text();
    private final Text playButtonText = new Text("PLAY");

    private GameState previousState = null;

    private final List<IGamePluginService> gamePluginServices;
    private final List<IEntityProcessingService> entityProcessingServiceList;
    private final List<IPostEntityProcessingService> postEntityProcessingServices;

    Game(List<IGamePluginService> gamePluginServices,
         List<IEntityProcessingService> entityProcessingServiceList,
         List<IPostEntityProcessingService> postEntityProcessingServices) {
        this.gamePluginServices = gamePluginServices;
        this.entityProcessingServiceList = entityProcessingServiceList;
        this.postEntityProcessingServices = postEntityProcessingServices;
    }

    public void start(Stage window) throws Exception {
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        gameWindow.setStyle("-fx-background-color: black;");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(16));
        gameWindow.getChildren().add(scoreText);

        titleText.setFill(Color.WHITE);
        titleText.setFont(Font.font(52));
        titleText.setTextAlignment(TextAlignment.CENTER);
        overlayScoreText.setFill(Color.WHITE);
        overlayScoreText.setFont(Font.font(24));
        overlayScoreText.setTextAlignment(TextAlignment.CENTER);
        highScoreText.setFill(Color.WHITE);
        highScoreText.setFont(Font.font(24));
        highScoreText.setTextAlignment(TextAlignment.CENTER);

        Rectangle playBg = new Rectangle(160, 50, Color.WHITE);
        playButtonText.setFill(Color.BLACK);
        playButtonText.setFont(Font.font(20));
        StackPane playButton = new StackPane(playBg, playButtonText);
        playButton.setCursor(Cursor.HAND);
        playButton.setOnMouseClicked(e -> startGame());

        overlay.setAlignment(Pos.CENTER);
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.82);");
        overlay.getChildren().addAll(titleText, overlayScoreText, highScoreText, playButton);

        root.getChildren().addAll(gameWindow, overlay);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.LEFT))  gameData.getKeys().setKey(GameKeys.LEFT,  true);
            if (event.getCode().equals(KeyCode.RIGHT)) gameData.getKeys().setKey(GameKeys.RIGHT, true);
            if (event.getCode().equals(KeyCode.UP))    gameData.getKeys().setKey(GameKeys.UP,    true);
            if (event.getCode().equals(KeyCode.SPACE)) gameData.getKeys().setKey(GameKeys.SPACE, true);
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.LEFT))  gameData.getKeys().setKey(GameKeys.LEFT,  false);
            if (event.getCode().equals(KeyCode.RIGHT)) gameData.getKeys().setKey(GameKeys.RIGHT, false);
            if (event.getCode().equals(KeyCode.UP))    gameData.getKeys().setKey(GameKeys.UP,    false);
            if (event.getCode().equals(KeyCode.SPACE)) gameData.getKeys().setKey(GameKeys.SPACE, false);
        });

        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();

        showMenuScreen();
    }

    public void render() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw();
                gameData.getKeys().update();
            }
        }.start();
    }

    private void update() {
        if (gameData.getGameState() != GameState.RUNNING) return;
        for (IEntityProcessingService s : entityProcessingServiceList) s.process(gameData, world);
        for (IPostEntityProcessingService s : postEntityProcessingServices) s.process(gameData, world);
    }

    private void draw() {
        GameState state = gameData.getGameState();

        if (state != previousState) {
            if (state == GameState.MENU) showMenuScreen();
            else if (state == GameState.GAME_OVER) showGameOverScreen();
            else if (state == GameState.RUNNING) overlay.setVisible(false);
            previousState = state;
        }

        if (state == GameState.RUNNING) {
            scoreText.setText("Score: " + gameData.getScore());
        }

        for (Entity e : new ArrayList<>(polygons.keySet())) {
            if (!world.getEntities().contains(e)) {
                gameWindow.getChildren().remove(polygons.remove(e));
            }
        }

        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity);
            if (polygon == null) {
                polygon = createPolygon(entity);
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }
            polygon.setTranslateX(entity.getX());
            polygon.setTranslateY(entity.getY());
            polygon.setRotate(entity.getRotation());
        }
    }

    private Polygon createPolygon(Entity entity) {
        Polygon polygon = new Polygon(entity.getPolygonCoordinates());
        polygon.setFill(Color.WHITE);
        polygon.setStroke(Color.WHITE);
        return polygon;
    }

    private void showMenuScreen() {
        titleText.setText("ASTEROIDS");
        overlayScoreText.setVisible(false);
        highScoreText.setText("High Score: " + gameData.getHighScore());
        playButtonText.setText("PLAY");
        overlay.setVisible(true);
    }

    private void showGameOverScreen() {
        titleText.setText("GAME OVER");
        overlayScoreText.setText("Score: " + gameData.getScore());
        overlayScoreText.setVisible(true);
        highScoreText.setText("High Score: " + gameData.getHighScore());
        playButtonText.setText("PLAY AGAIN");
        overlay.setVisible(true);
    }

    private void startGame() {
        if (gameData.getGameState() == GameState.GAME_OVER) {
            for (IGamePluginService plugin : gamePluginServices) plugin.stop(gameData, world);
        }
        for (Entity e : new ArrayList<>(world.getEntities())) world.removeEntity(e);
        polygons.clear();
        gameWindow.getChildren().removeIf(n -> !(n instanceof Text));

        gameData.setScore(0);
        gameData.setGameState(GameState.RUNNING);

        for (IGamePluginService plugin : gamePluginServices) plugin.start(gameData, world);
    }

    public List<IGamePluginService> getGamePluginServices() { return gamePluginServices; }
    public List<IEntityProcessingService> getEntityProcessingServices() { return entityProcessingServiceList; }
    public List<IPostEntityProcessingService> getPostEntityProcessingServices() { return postEntityProcessingServices; }
}
