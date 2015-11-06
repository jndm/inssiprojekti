package com.jndm.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jndm.game.MyGame;
import com.jndm.game.utils.Constants;
import com.jndm.game.utils.Utils;

public class MainMenu implements Screen {
	
	private MyGame game;
	private Stage stage;
	private Skin skin;
	private Table mastertable;
	private TextureAtlas atlas;
	
	// Actor sizes
	private float BUTTON_WIDTH;
	private float BUTTON_HEIGHT;
	
	//Font sizes
	private float MAINBUTTON_FONT_SIZE;
	private float TITLE_FONT_SIZE;
	
	private void createConstants() {
		BUTTON_WIDTH = stage.getWidth() * 0.6f;
		BUTTON_HEIGHT = stage.getHeight() * 0.18f;	
		
		MAINBUTTON_FONT_SIZE = BUTTON_WIDTH * 0.12f;             
		TITLE_FONT_SIZE = stage.getWidth() * 0.1f;
	}
	
	public MainMenu(MyGame game) {
		this.game = game;
	} 
	
	@Override
	public void show() {
		if(!game.assetManager.isLoaded(Constants.UI_ATLAS_PATH)) {
			game.assetManager.load(Constants.UI_ATLAS_PATH, TextureAtlas.class);
			game.assetManager.finishLoading();
		}
		
		atlas = game.assetManager.get(Constants.UI_ATLAS_PATH);
		
		stage = new Stage(game.uiViewport, game.sb);
		
		//Gdx.app.log("INFO", "sw: "+stage.getWidth()+" sh: "+stage.getHeight()+"\nvpw:"+game.uiViewport.getWorldWidth()+" vph:"+game.uiViewport.getWorldHeight());
		
		createConstants();
		
		skin = new Skin();
		skin.add("mainbuttonfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, MAINBUTTON_FONT_SIZE, Constants.WHITE));
		skin.add("titlefont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, TITLE_FONT_SIZE, Constants.DARK_BLUE));
		skin.addRegions(atlas);
		skin.load(Gdx.files.internal(Constants.MAIN_MENU_SKIN_PATH));
		
		mastertable = new Table(skin);
		mastertable.setBackground("background");
		mastertable.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		
		mastertable.add(new Label("Platformer", skin, "title")).padBottom(BUTTON_HEIGHT * 0.40f);
		mastertable.row();
		
		addMainButtonsToMasterTable();

		stage.addActor(mastertable);
		Gdx.input.setInputProcessor(stage);	
	}

	@Override
	public void render(float delta) {		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.sb.setProjectionMatrix(stage.getCamera().combined);
		stage.getViewport().apply();
		
		stage.act();
		stage.draw();
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();	
	}
	
	private void addMainButtonsToMasterTable() {
		TextButton playButton = new TextButton("Play", skin, "mainbutton");
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.run(new Runnable() {
				    public void run () {
				    	game.setScreen(new LevelSelection(game));
				    }
				})));
			}
		});
		
		TextButton optionsButton = new TextButton("Options", skin, "mainbutton");
		
		TextButton exitButton = new TextButton("Exit", skin, "mainbutton");
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		mastertable.add(playButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(BUTTON_HEIGHT / 10);
		mastertable.row();
		mastertable.add(optionsButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(BUTTON_HEIGHT / 10);
		mastertable.row();
		mastertable.add(exitButton).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(BUTTON_HEIGHT / 10);
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
