package com.jndm.game.utils;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

public class Box2DMapObjectParserHelper extends Box2DMapObjectParser{
	
	public Box2DMapObjectParserHelper(float unitScale) {
		super(unitScale);
	}
	
	public void load(World world, TiledMap map) throws Exception {
		
		//Set platform box2D data
		if(map.getLayers().get("Platform") != null && map.getLayers().get("Platform").getObjects().getCount() != 0) {
			map.getLayers().get("Platform").getProperties().put("type", Constants.BOX2D_OBJECT);
			map.getLayers().get("Platform").getProperties().put("categoryBits", Constants.PLATFORM_CATEGORY);
			map.getLayers().get("Platform").getProperties().put("maskBits", Constants.PLATFORM_MASK);
			map.getLayers().get("Platform").getProperties().put("userData", Constants.BOX2D_PLATFORM_USERDATA);
		} else {
			throw new Exception("Error: Map have no platforms (The object layer of platforms has to be named as 'Platform')");
		}
		
		//Set spikes' box2D data
		if(map.getLayers().get("Spike") != null && map.getLayers().get("Spike").getObjects().getCount() != 0) {
			map.getLayers().get("Spike").getProperties().put("type", Constants.BOX2D_OBJECT);
			map.getLayers().get("Spike").getProperties().put("categoryBits", Constants.PLATFORM_CATEGORY);
			map.getLayers().get("Spike").getProperties().put("userData", Constants.BOX2D_SPIKE_USERDATA);
			map.getLayers().get("Spike").getProperties().put("maskBits", Constants.SPIKE_MASK);
		}
		
		//Set moving platform's box2D data
		if(map.getLayers().get("MovingPlatform") != null && map.getLayers().get("MovingPlatform").getObjects().getCount() != 0) {
			int i = 0;
			for(MapObject mo : map.getLayers().get("MovingPlatform").getObjects()) {
				mo.setName("mplatform"+i);
				mo.getProperties().put("type", Constants.BOX2D_OBJECT);
				mo.getProperties().put("bodyType", Constants.BOX2D_KINEMATIC_BODY_TYPE);
				mo.getProperties().put("userData", Constants.BOX2D_PLATFORM_USERDATA);
				mo.getProperties().put("categoryBits", Constants.PLATFORM_CATEGORY);
				mo.getProperties().put("maskBits", Constants.PLATFORM_MASK);
				i++;
			}
		}
		
		//Check if spawnpoint exists
		if(map.getLayers().get("Points") == null || map.getLayers().get("Points").getObjects().get("Spawnpoint") == null) {
			throw new Exception("Error: Map have no spawnpoint (Check that you have object layer called Points and element called 'Spawnpoint')");
		}
		
		//Check if goal exists in map and set box2D data
		if(map.getLayers().get("Points").getObjects().get("Goal") != null) {
			map.getLayers().get("Points").getObjects().get("Goal").getProperties().put("type", Constants.BOX2D_OBJECT);
			map.getLayers().get("Points").getObjects().get("Goal").getProperties().put("categoryBits", Constants.GOAL_CATEGORY);
			map.getLayers().get("Points").getObjects().get("Goal").getProperties().put("userData", Constants.BOX2D_GOAL_USERDATA);
			map.getLayers().get("Points").getProperties().put("maskBits", Constants.GOAL_MASK);
		} else {
			throw new Exception("Error: Map have no goal (Check that you have object layer called Points and element called 'Goal')");
		}
		
		super.load(world, map);
	}
}

