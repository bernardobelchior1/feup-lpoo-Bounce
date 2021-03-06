package feup.lpoo.bounce.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

import feup.lpoo.bounce.Bounce;

/**
 * Class used to load the TiledMap passed as argument to a World.
 *
 * Created by Bernardo on 01-06-2016.
 */
public class LevelLoader {
    //Map layers definition
    private final static int WALL_LAYER = 2;
    private final static int BALL_LAYER = 3;
    private final static int SPIKE_LAYER = 4;
    private final static int RINGS_LAYER = 5;
    private final static int GEMS_LAYER = 6;
    private final static int BARBED_WIRE_LAYER = 7;
    private final static int MONSTER_LAYER = 8;
    private final static int INVERTED_SPIKE_LAYER = 9;

    private Body ball;
    private ArrayList<Body> rings;
    private ArrayList<Body> gems;
    private ArrayList<Monster> monsters;

    private int mapWidth;
    private int mapHeight;

    /**
     * Loads the map into the world
     * @param map Map
     * @param world World
     */
    public void load(TiledMap map, World world) {
        mapWidth = map.getProperties().get("width", Integer.class);
        mapHeight = map.getProperties().get("height", Integer.class);

        rings = new ArrayList<Body>();
        gems = new ArrayList<Body>();
        monsters = new ArrayList<Monster>();

        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(96/BounceGame.PIXELS_PER_METER, (mapHeight/2-32/BounceGame.PIXELS_PER_METER));

        try {
            Ellipse circle = ((EllipseMapObject) map.getLayers().get(BALL_LAYER).getObjects().get(0)).getEllipse();
            bodyDef.position.set((circle.x + 32)/BounceGame.PIXELS_PER_METER, (circle.y + 32)/BounceGame.PIXELS_PER_METER);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        CircleShape ballShape = new CircleShape();
        ballShape.setRadius(32/BounceGame.PIXELS_PER_METER);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = ballShape;
        fixtureDef.restitution = 0.2f;
        fixtureDef.density = 1;

        ball = world.createBody(bodyDef);
        ball.createFixture(fixtureDef);
        ball.setUserData(Bounce.EntityType.BALL);
        ballShape.dispose();

        try {
            for(MapObject object : map.getLayers().get(WALL_LAYER).getObjects()) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(rectangle.getX()/BounceGame.PIXELS_PER_METER+rectangle.getWidth()/2/BounceGame.PIXELS_PER_METER, rectangle.getY()/BounceGame.PIXELS_PER_METER+rectangle.getHeight()/2/BounceGame.PIXELS_PER_METER);

                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth()/2/BounceGame.PIXELS_PER_METER, rectangle.getHeight()/2/BounceGame.PIXELS_PER_METER);

                Body body = world.createBody(bodyDef);
                body.createFixture(polygonShape, 1);
                body.setUserData(Bounce.EntityType.WALL);
                polygonShape.dispose();
            }
        } catch(IndexOutOfBoundsException e) {
            Gdx.app.log("LevelLoader", "No walls layer in map. Exiting...");
            System.exit(1);
        }

        try {
            for(MapObject object : map.getLayers().get(SPIKE_LAYER).getObjects()) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(rectangle.getX()/BounceGame.PIXELS_PER_METER+rectangle.getWidth()/2/BounceGame.PIXELS_PER_METER, rectangle.getY()/BounceGame.PIXELS_PER_METER+rectangle.getHeight()/2/BounceGame.PIXELS_PER_METER);

                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth()/2/BounceGame.PIXELS_PER_METER, rectangle.getHeight()/2/BounceGame.PIXELS_PER_METER);

                Body body = world.createBody(bodyDef);
                body.createFixture(polygonShape, 1);
                body.setUserData(Bounce.EntityType.SPIKE);
                polygonShape.dispose();
            }
        } catch (IndexOutOfBoundsException e) {
            Gdx.app.log("LevelLoader", "No spikes layer in map.");
        }


        try {
            for(MapObject object : map.getLayers().get(RINGS_LAYER).getObjects()) {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();

                bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.KinematicBody;
                bodyDef.position.set(ellipse.x/BounceGame.PIXELS_PER_METER + ellipse.width/2/BounceGame.PIXELS_PER_METER, ellipse.y/BounceGame.PIXELS_PER_METER + ellipse.height/2/BounceGame.PIXELS_PER_METER);

                CircleShape ellipseShape = new CircleShape();

                float radius;
                if(ellipse.height < ellipse.width)
                    radius = ellipse.height/2/BounceGame.PIXELS_PER_METER;
                else
                    radius = ellipse.width/2/BounceGame.PIXELS_PER_METER;

                ellipseShape.setRadius(radius);

                fixtureDef = new FixtureDef();
                fixtureDef.shape = ellipseShape;
                fixtureDef.density = 1;
                fixtureDef.isSensor = true;

                Body body = world.createBody(bodyDef);
                body.createFixture(fixtureDef);
                body.setUserData(Bounce.EntityType.RING);
                rings.add(body);
                ellipseShape.dispose();
            }
        } catch (IndexOutOfBoundsException e) {
            Gdx.app.log("LevelLoader", "No rings layer in map.");
        }

        try {
            for(MapObject object : map.getLayers().get(GEMS_LAYER).getObjects()) {
                Ellipse ellipse = ((EllipseMapObject) object).getEllipse();

                bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.KinematicBody;
                bodyDef.position.set((ellipse.x + ellipse.width/2)/BounceGame.PIXELS_PER_METER, (ellipse.y + ellipse.height/2)/BounceGame.PIXELS_PER_METER);

                float radius;
                if(ellipse.height < ellipse.width)
                    radius = ellipse.height/2/BounceGame.PIXELS_PER_METER;
                else
                    radius = ellipse.width/2/BounceGame.PIXELS_PER_METER;

                CircleShape ellipseShape = new CircleShape();
                ellipseShape.setRadius(radius);

                fixtureDef = new FixtureDef();
                fixtureDef.shape = ellipseShape;
                fixtureDef.density = 1;
                fixtureDef.isSensor = true;

                Body body = world.createBody(bodyDef);
                body.createFixture(fixtureDef);
                body.setUserData(Bounce.EntityType.GEM);
                gems.add(body);
                ellipseShape.dispose();
            }
        } catch (IndexOutOfBoundsException e) {
            Gdx.app.log("LevelLoader", "No gems layer in map.");
        }

        try {
            for(MapObject object : map.getLayers().get(BARBED_WIRE_LAYER).getObjects()) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((rectangle.getX()+rectangle.getWidth()/2)/BounceGame.PIXELS_PER_METER, (rectangle.getY()+rectangle.getHeight()/2)/BounceGame.PIXELS_PER_METER);

                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth()/2/BounceGame.PIXELS_PER_METER, rectangle.getHeight()/2/BounceGame.PIXELS_PER_METER);

                Body body = world.createBody(bodyDef);
                body.createFixture(polygonShape, 1);
                body.setUserData(Bounce.EntityType.BARBED_WIRE);
                polygonShape.dispose();
            }
        } catch (IndexOutOfBoundsException e) {
            Gdx.app.log("LevelLoader", "No barbed wire layer in map.");
        }

        try {
            for(MapObject object : map.getLayers().get(MONSTER_LAYER).getObjects()) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.KinematicBody;
                bodyDef.position.set((rectangle.getX()+rectangle.getWidth()/2)/BounceGame.PIXELS_PER_METER, (rectangle.getY()+rectangle.getHeight()/2)/BounceGame.PIXELS_PER_METER);

                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth()/2/BounceGame.PIXELS_PER_METER, rectangle.getHeight()/2/BounceGame.PIXELS_PER_METER);

                Body body = world.createBody(bodyDef);
                body.createFixture(polygonShape, 1);
                body.setUserData(Bounce.EntityType.MONSTER);

                int movementHeight;

                try {
                    movementHeight = Integer.parseInt(object.getProperties().get("movementHeight", String.class));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    movementHeight = 0;
                }

                int movementWidth;

                try {
                    movementWidth = Integer.parseInt(object.getProperties().get("movementWidth", String.class));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    movementWidth = 0;
                }

                monsters.add(new Monster(body, movementWidth/BounceGame.PIXELS_PER_METER, movementHeight/BounceGame.PIXELS_PER_METER));

                polygonShape.dispose();
            }
        } catch (IndexOutOfBoundsException e) {
            Gdx.app.log("LevelLoader", "No monster layer in map.");
        }

        try {
            for(MapObject object : map.getLayers().get(INVERTED_SPIKE_LAYER).getObjects()) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

                bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(rectangle.getX()/BounceGame.PIXELS_PER_METER+rectangle.getWidth()/2/BounceGame.PIXELS_PER_METER, rectangle.getY()/BounceGame.PIXELS_PER_METER+rectangle.getHeight()/2/BounceGame.PIXELS_PER_METER);

                PolygonShape polygonShape = new PolygonShape();
                polygonShape.setAsBox(rectangle.getWidth()/2/BounceGame.PIXELS_PER_METER, rectangle.getHeight()/2/BounceGame.PIXELS_PER_METER);

                Body body = world.createBody(bodyDef);
                body.createFixture(polygonShape, 1);
                body.setUserData(Bounce.EntityType.INVERTED_SPIKE);
                polygonShape.dispose();
            }
        } catch (IndexOutOfBoundsException e) {
            Gdx.app.log("LevelLoader", "No inverted spikes layer in map.");
        }
    }

    /**
     * Gets the ball
     * @return Ball
     */
    public Body getBall() {
        return ball;
    }

    /**
     * Gets the rings
     * @return Rings
     */
    public ArrayList<Body> getRings() {
        return rings;
    }

    /**
     * Gets the gems
     * @return Gems
     */
    public ArrayList<Body> getGems() {
        return gems;
    }

    /**
     * Gets the monsters
     * @return Monsters
     */
    public ArrayList<Monster> getMonsters() {
        return monsters;
    }

    /**
     * Gets the map height
     * @return mapHeight
     */
    public int getMapHeight() {
        return mapHeight;
    }

    /**
     * Gets the map width
     * @return mapWidth
     */
    public int getMapWidth() {
        return mapWidth;
    }
}
