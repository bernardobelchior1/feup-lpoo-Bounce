package feup.lpoo.bounce.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import feup.lpoo.bounce.Bounce;
import feup.lpoo.bounce.logic.BounceGame;

/**
 * Created by Bernardo on 30-05-2016.
 */
public class GameScreen implements Screen {
    private Bounce bounce;
    private BounceGame game;

    private OrthographicCamera camera;
    private FitViewport viewport;
    private OrthogonalTiledMapRenderer renderer;
    private GameHUD gameHUD;
    private Box2DDebugRenderer b2dr;
   // private Stage stage;

    private Texture tileset;

    //Texture regions
    private TextureRegion ballTextureRegion;
    private TextureRegion ringTextureRegion;
    private TextureRegion gemTextureRegion;

    private SpriteBatch spriteBatch;

    public GameScreen(Bounce bounce, BounceGame game) {
        this.bounce = bounce;
        this.game = game;

        spriteBatch = new SpriteBatch();
        tileset = new Texture("tileset.png");
        ballTextureRegion = new TextureRegion(tileset, 0, 0, Graphics.TEXTURE_SIZE, Graphics.TEXTURE_SIZE);
        ringTextureRegion = new TextureRegion(tileset, 0, Graphics.TEXTURE_SIZE, Graphics.TEXTURE_SIZE, Graphics.TEXTURE_SIZE);
        gemTextureRegion = new TextureRegion(tileset, Graphics.TEXTURE_SIZE, Graphics.TEXTURE_SIZE, Graphics.TEXTURE_SIZE, Graphics.TEXTURE_SIZE);

        camera = new OrthographicCamera();
        viewport = new FitViewport(game.getMapHeight() *(float)Gdx.graphics.getWidth()/Gdx.graphics.getHeight(), game.getMapHeight(), camera);
        renderer = new OrthogonalTiledMapRenderer(game.getMap(), 1);
        b2dr = new Box2DDebugRenderer();
        gameHUD = new GameHUD(bounce, game);

        camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Sets the camera x coordinate so it follows the ball
        //The below if statements are to make sure that no black
        //bars appear on the right nor on the left.'
        float cameraX = game.getBall().getPosition().x - Graphics.TEXTURE_SIZE/2;

        if(cameraX < viewport.getWorldWidth()/2)
            cameraX = viewport.getWorldWidth()/2;
        else if(cameraX > game.getMapWidth() - viewport.getWorldWidth()/2)
            cameraX = game.getMapWidth() - viewport.getWorldWidth()/2;

        camera.position.set(cameraX, viewport.getWorldHeight()/2, 0);

        //Updates the camera and renders the screen
        camera.update();
        renderer.setView(camera);
        renderer.render();
        b2dr.render(game.getWorld(), camera.combined);

        //Draws the ball on its position
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(ballTextureRegion, game.getBall().getPosition().x - Graphics.TEXTURE_SIZE/2,
                game.getBall().getPosition().y - Graphics.TEXTURE_SIZE/2);

        //Draws all the rings in the correct position
        for(Body ring : game.getRings()) {
            spriteBatch.draw(ringTextureRegion, ring.getPosition().x - Graphics.TEXTURE_SIZE/2,
                    ring.getPosition().y - Graphics.TEXTURE_SIZE/2);
        }

        //Draws all the gems in the correct position
        for(Body gem : game.getGems()) {
            spriteBatch.draw(gemTextureRegion, gem.getPosition().x - Graphics.TEXTURE_SIZE/2,
                    gem.getPosition().y - Graphics.TEXTURE_SIZE/2);
        }

        spriteBatch.end();

        gameHUD.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

