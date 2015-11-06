package com.jndm.game.resources;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.jndm.game.MyGame;
import com.jndm.game.saving.Level;
import com.jndm.game.utils.Box2DMapObjectParserHelper;
import com.jndm.game.utils.Constants;

public class GameWorld {
	private MyGame game;
	private World world;
	
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
		
		if(game.DEBUG) {
			box2dRenderer = new Box2DDebugRenderer();
		}
		
	}

	public void render() {
		mapRenderer.setView(game.cam);
		mapRenderer.render();
		
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
