package com.jndm.game.resources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jndm.game.MyGame;
import com.jndm.game.saving.Level;
import com.jndm.game.utils.Box2DMapObjectParserHelper;
import com.jndm.game.utils.Constants;

public class GameWorld {
	private MyGame game;
	private World world;
	private Array<MovingPlatform> movingPlatforms;
	
	private Box2DDebugRenderer box2dRenderer;
	private OrthogonalTiledMapRenderer mapRenderer;
	
	public GameWorld(MyGame game, World world, Level level) {	
		this.game = game;
		this.world = world;
		
		TiledMap map = new TmxMapLoader().load("levels/"+level.getName()+".tmx");
		Box2DMapObjectParserHelper parser = new Box2DMapObjectParserHelper(1 / Constants.PPM);
		try {
			parser.load(world, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mapRenderer = new OrthogonalTiledMapRenderer(map, parser.getUnitScale(), game.sb);
		
		createMovingPlatforms(parser);
		
		if(game.DEBUG) {
			box2dRenderer = new Box2DDebugRenderer();
		}
		
	}

	private void createMovingPlatforms(Box2DMapObjectParserHelper parser) {
		movingPlatforms = new Array<MovingPlatform>();
		
		if(mapRenderer.getMap().getLayers().get("MovingPlatform") != null 
				&& mapRenderer.getMap().getLayers().get("MovingPlatform").getObjects().getCount() != 0) {
			int mplatformCount = mapRenderer.getMap().getLayers().get("MovingPlatform").getObjects().getCount();
		
			for(int i = 0; i < mplatformCount; i++) {
				MapObject mo = mapRenderer.getMap().getLayers().get("MovingPlatform").getObjects().get("mplatform"+i);
				TiledMapTileSet tiles = mapRenderer.getMap().getTileSets().getTileSet("movingPlatform");
				
				Body body = parser.getBodies().get("mplatform"+i);
				float x = mo.getProperties().get("x", Float.class);
				float y = mo.getProperties().get("y", Float.class);
				float w = mo.getProperties().get("width", Float.class);
				float h = mo.getProperties().get("height", Float.class);
				
				float speedX = Float.parseFloat(mo.getProperties().get("speedX", String.class)); 
				float speedY = Float.parseFloat(mo.getProperties().get("speedY", String.class));  
				
				float minX = Float.parseFloat(mo.getProperties().get("minX", String.class));  
				float maxX = Float.parseFloat(mo.getProperties().get("maxX", String.class)); 
				float minY = Float.parseFloat(mo.getProperties().get("minY", String.class)); 
				float maxY = Float.parseFloat(mo.getProperties().get("maxY", String.class));
				
				movingPlatforms.add(new MovingPlatform(tiles, body, x, y, w, h, speedX, speedY, minX, maxX, minY, maxY));
			}
		}
	}
	
	public void update(float delta) {
		for(MovingPlatform mplatform : movingPlatforms) {
			mplatform.update(delta);
		}
	}

	public void render() {
		mapRenderer.setView(game.cam);
		mapRenderer.render();
		
		game.sb.begin();
		for(MovingPlatform mplatform : movingPlatforms) {
			mplatform.render(game.sb);
		}
		game.sb.end();
		
		if(game.DEBUG) {
			box2dRenderer.render(world, game.cam.combined);
		}
	}
	
	public void dispose() {
		mapRenderer.dispose();
		if(game.DEBUG) {
			box2dRenderer.dispose();
		}
	}

	public float getLevelWidth() {
		return mapRenderer.getMap().getProperties().get("width", Integer.class);
	}

	public float getLevelHeight() {
		return mapRenderer.getMap().getProperties().get("height", Integer.class);
	}

	public Vector2 getSpawnpoint() {
		MapProperties spawnpoint = mapRenderer.getMap().getLayers().get("Points").getObjects().get("Spawnpoint").getProperties();
		
		float x = spawnpoint.get("x", Float.class);
		float y = spawnpoint.get("y", Float.class);
		float w = spawnpoint.get("width", Float.class);
		float h = spawnpoint.get("height", Float.class);
		
		return new Vector2((x + w/2) / Constants.PPM , (y + h/2) / Constants.PPM);
	}
	

}
