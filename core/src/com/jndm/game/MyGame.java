package com.jndm.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jndm.game.saving.Level;
import com.jndm.game.saving.SaveManager;
import com.jndm.game.screens.Menu;
import com.jndm.game.utils.Constants;

public class MyGame extends Game {
	public SpriteBatch sb;
	public ShapeRenderer shapeRenderer;
	public OrthographicCamera cam;
	public OrthographicCamera hudCam;
	public ExtendViewport gameViewport;
	public ExtendViewport uiViewport;
	public SaveManager saveManager;
	
	public final boolean DEBUG = false;
	
	public AssetManager assetManager;

	public void create() {
		
		assetManager = new AssetManager();
		
		sb = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		cam = new OrthographicCamera();
		gameViewport = new ExtendViewport(Constants.VIRTUAL_WIDTH / 25, Constants.VIRTUAL_HEIGHT / 25, cam);
		
		hudCam = new OrthographicCamera();
		uiViewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), hudCam);
		
		saveManager = new SaveManager();
		/*if(saveManager.getAllData().size == 0) {	 // if first time launching the game generate level data
			generateLevelData();
		}*/
		
		this.setScreen(new Menu(this));
	}
	
	private void generateLevelData() {
		for(int i=0; i < Constants.MAXLEVELS; i++) {
			Level level = null;
			if(i == 0) {
				level = new Level("level"+(i+1), i+1, "00:00.00", false, true);	//set first level available
			} else {
				level = new Level("level"+(i+1), i+1, "00:00.00", false, false);
			}
			saveManager.saveDataValue("level"+(i+1), level);
		}
	}
	
	public void dispose() {
		sb.dispose();
		shapeRenderer.dispose();
        assetManager.dispose();
    }
}
