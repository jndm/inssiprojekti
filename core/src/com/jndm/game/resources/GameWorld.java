package com.jndm.game.resources;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.jndm.game.MyGame;
import com.jndm.game.saving.Level;
import com.jndm.game.screens.Play;
import com.jndm.game.utils.Box2DMapObjectParserHelper;
import com.jndm.game.utils.Constants;

public class GameWorld {
	private MyGame game;
	private Play play;
	private World world;
	private Level level;
	
	private Box2DDebugRenderer box2dRenderer;
	private OrthogonalTiledMapRenderer mapRenderer;
	
	public GameWorld(Play play, MyGame game, World world, Level level) {	
		this.game = game;
		this.world = world;
		this.play = play;
		this.level = level;
		
		TiledMap map = new TmxMapLoader().load("levels/testing/test.tmx");
		Box2DMapObjectParserHelper parser = new Box2DMapObjectParserHelper(1 / Constants.PPM);
		try {
			parser.load(world, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mapRenderer = new OrthogonalTiledMapRenderer(map, parser.getUnitScale(), game.sb);
		box2dRenderer = new Box2DDebugRenderer();
		
	}

	public void render() {
		mapRenderer.setView(game.cam);
		mapRenderer.render();
		box2dRenderer.render(world, game.cam.combined);
	}
	
	public void dispose() {
		mapRenderer.dispose();
		box2dRenderer.dispose();
	}

	public float getLevelWidth() {
		return mapRenderer.getMap().getProperties().get("width", Integer.class);
	}

	public float getLevelHeight() {
		return mapRenderer.getMap().getProperties().get("height", Integer.class);
	}
	

}
