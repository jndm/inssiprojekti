package com.jndm.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	
	// GAME SETTINGS
	public static final float VIRTUAL_WIDTH = 480;
	public static final float VIRTUAL_HEIGHT = 320;
	public static final float PPM = 32;
	public static final int MAXLEVELS = 40;
	// END OF GAME SETTINGS
	
	// COMMON
	public static final float MAXTRAJECTORYPOINTCOUNT = 12;
	public static final Color WHITE = new Color(1f, 1f, 1f, 1f);
	public static final Color DARK_BLUE = new Color(0f, 0.55f, 0.8f, 1f);
	// END OF COMMON
	
	// PLAYER CONSTANS
	public static final float DEFAULTVELOCITY = 7f;
	public static final Vector2 MAXJUMPVELOCITY = new Vector2(5f, 8f);
	//END OF PLAYER CONSTANTS
	
	//BOX2D
	public static final String BOX2D_OBJECT = "object";
	public static final String BOX2D_KINEMATIC_BODY_TYPE = "KinematicBody";
	
	public static final String BOX2D_GOAL_USERDATA = "GOAL";
	public static final String BOX2D_PLATFORM_USERDATA = "PLATFORM";
	public static final String BOX2D_SPIKE_USERDATA = "SPIKE";
	
	//categories
	public static final short PLAYER_CATEGORY = 0x001;
	public static final short PLATFORM_CATEGORY  = 0x002;
	public static final short SPIKE_CATEGORY  = 0x004;
	public static final short GOAL_CATEGORY  = 0x008;
	
	//masks
	public static final short PLAYER_MASK = PLATFORM_CATEGORY | SPIKE_CATEGORY | GOAL_CATEGORY;
	public static final short PLATFORM_MASK = PLAYER_CATEGORY;
	public static final short GOAL_MASK = PLAYER_CATEGORY;
	public static final short SPIKE_MASK = PLAYER_CATEGORY;
	//END OF BOX2D
	
	// ASSETS
	// UI
	public static final String FONT_KENFACTOR_PATH = "fonts/kenvector_future.ttf";
	public static final String UI_ATLAS_PATH = "ui/ui_pack.pack";
	public static final String MAIN_MENU_SKIN_PATH = "ui/mainmenu_skin.json";
	public static final String LEVELSELECT_SKIN_PATH = "ui/levelselect_skin.json";
	public static final String HUD_SKIN_PATH = "ui/hud_skin.json";
	
	// PLAYER
	public static final String PLAYER_ATLAS_PATH = "character/character_pack.pack";
	
	// END OF ASSETS
	
}
