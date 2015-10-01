package com.jndm.game.utils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

public class Box2DMapObjectParserHelper extends Box2DMapObjectParser{
	
	public Box2DMapObjectParserHelper(float unitScale) {
		super(unitScale);
	}
	
	public void load(World world, TiledMap map) throws Exception {
		
		//Check if atleast one wall object exists in map and add categoryBits
		if(map.getLayers().get("Platform") != null && map.getLayers().get("Platform").getObjects().getCount() != 0) {
			map.getLayers().get("Platform").getProperties().put("type", Constants.BOX2D_OBJECT);
			//map.getLayers().get("Platform").getProperties().put("categoryBits", Constants.WORLD_CATEGORY);
			//map.getLayers().get("Platform").getProperties().put("userData", Constants.BOX2D_WALL_USERDATA);
		} else {
			throw new Exception("Error: Map have no platforms (The object layer of platforms has to be named as 'Platform')");
		}
		
		if(map.getLayers().get("Spike") != null && map.getLayers().get("Spike").getObjects().getCount() != 0) {
			map.getLayers().get("Spike").getProperties().put("type", Constants.BOX2D_OBJECT);
			//map.getLayers().get("Spike").getProperties().put("categoryBits", Constants.WORLD_CATEGORY);
			//map.getLayers().get("Spike").getProperties().put("userData", Constants.BOX2D_WALL_USERDATA);
		} 
		
		/*
		//Check if spawnpoint exists
		if(map.getLayers().get("Points") == null || map.getLayers().get("Points").getObjects().get("Spawn") == null) {
			throw new Exception("Error: Map have no spawnpoint (Check that you have object layer called Points and element called 'Spawn')");
		}
		
		//Check if goal exists in map and add categoryBits
		if(map.getLayers().get("Points").getObjects().get("Goal") != null) {
			map.getLayers().get("Points").getObjects().get("Goal").getProperties().put("type", Constants.BOX2D_OBJECT);
			map.getLayers().get("Points").getObjects().get("Goal").getProperties().put("categoryBits", Constants.WORLD_CATEGORY);
			map.getLayers().get("Points").getObjects().get("Goal").getProperties().put("userData", Constants.BOX2D_GOAL_USERDATA);
		} else {
			throw new Exception("Error: Map have no goal (Check that you have object layer called Points and element called 'Goal')");
		}
		*/
		super.load(world, map);
	}
}

