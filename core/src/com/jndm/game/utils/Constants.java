package com.jndm.game.utils;

import com.badlogic.gdx.math.Vector2;

public class Constants {
	
	// COMMON
	public static final float VIRTUAL_WIDTH = 480;
	public static final float VIRTUAL_HEIGHT = 320;
	
	public static final float PPM = 32;
	
	public static final float MAXTRAJECTORYPOINTCOUNT = 12;
	// END OF COMMON
	
	// Player constants
	public static final float DEFAULTVELOCITY = 7f;
	public static final Vector2 MAXJUMPVELOCITY = new Vector2(5f, 8f);
	//END OF PLAYER CONSTANTS
	
	//BOX2D
	public static final String BOX2D_OBJECT = "object";
	//END OF BOX2D
}
