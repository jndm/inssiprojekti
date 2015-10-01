package com.jndm.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.jndm.game.screens.MainMenu;
import com.jndm.game.screens.Play;
import com.jndm.game.utils.Constants;

public class MyGame extends Game {
	public SpriteBatch sb;
	public ShapeRenderer shapeRenderer;
	public OrthographicCamera cam;
	public OrthographicCamera hudCam;
	public ExtendViewport gameViewport;
	public ExtendViewport uiViewport;
	
	public AssetManager assetManager;

	public void create() {
		
		assetManager = new AssetManager();
		
		sb = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		cam = new OrthographicCamera();
		gameViewport = new ExtendViewport(Constants.VIRTUAL_WIDTH / 25, Constants.VIRTUAL_HEIGHT / 25, cam);
		
		hudCam = new OrthographicCamera();
		uiViewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), hudCam);
		
		//this.setScreen(new MainMenu(this));
		this.setScreen(new Play(this));
	}
	
	public void dispose() {
		sb.dispose();
		shapeRenderer.dispose();
        assetManager.dispose();
    }
}
