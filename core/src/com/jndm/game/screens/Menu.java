package com.jndm.game.screens;

import sun.security.krb5.internal.PAData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.jndm.game.MyGame;
import com.jndm.game.gui.LevelSelection;
import com.jndm.game.utils.Constants;
import com.jndm.game.utils.Utils;

public class Menu implements Screen {
	
	private MyGame game;
	private Stage stage;
	private Skin skin;
	private Table mastertable;
	private TextureAtlas atlas;
	
	private Table currentTab;
	private Table levelselectTab;
	private Table titleTab;
	
	// Main sizes
	private float HEADERBUTTON_WIDTH;
	private float HEADERBUTTON_HEIGHT;
	private float MAINBUTTON_WIDTH;
	private float MAINBUTTON_HEIGHT;
	
	//Font sizes
	private float TITLE_FONT_SIZE;
	
	//Level sizes
	public float LEVELBUTTON_WIDTH;
	public float LEVELBUTTON_HEIGHT;
	public float ARROW_BUTTON_WIDTH;
	public float ARROW_BUTTON_HEIGHT;
	public float OPTIONS_BUTTON_SIZE;
	
	private void createConstants() {

		//Main
		HEADERBUTTON_WIDTH = stage.getWidth() * 0.06f;
		HEADERBUTTON_HEIGHT = HEADERBUTTON_WIDTH;
		
		MAINBUTTON_WIDTH = stage.getWidth() * 0.10f;
		MAINBUTTON_HEIGHT = MAINBUTTON_WIDTH;	
		
		TITLE_FONT_SIZE = stage.getWidth() * 0.1f;
		
		//Level selection
		LEVELBUTTON_WIDTH = stage.getWidth() * 0.21f;  
		LEVELBUTTON_HEIGHT = stage.getHeight() * 0.202f;
		
		ARROW_BUTTON_WIDTH  = stage.getWidth() * 0.1f;  
		ARROW_BUTTON_HEIGHT = stage.getHeight() * 0.08f;
		
		OPTIONS_BUTTON_SIZE = stage.getWidth() * 0.07f;
	}
	
	public Menu(MyGame game) {
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
		skin.add("titlefont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, TITLE_FONT_SIZE, Constants.DARK_BLUE));
		skin.add("font", Utils.createFont(Constants.FONT_KENFACTOR_PATH, LEVELBUTTON_WIDTH * 0.35f, Constants.WHITE));
		skin.add("levelbuttonfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, LEVELBUTTON_WIDTH * 0.50f, Constants.WHITE));
		skin.add("levelbuttonsmallfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, LEVELBUTTON_WIDTH * 0.10f, Constants.WHITE));
		skin.addRegions(atlas);
		skin.load(Gdx.files.internal("ui/menu_skin.json"));
		
		mastertable = new Table(skin);
		mastertable.setBackground("background");
		mastertable.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		
		mastertable.row();
		
		addHeaderButtonsToMasterTable();
		addMainStackToMasterTable();
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
		atlas.dispose();
		skin.dispose();
		stage.dispose();	
	}
	
	private void addHeaderButtonsToMasterTable() {
		Table leftHeaderTable = new Table();
		leftHeaderTable.add(new Button(skin, "optionsbutton"))
			.width(HEADERBUTTON_WIDTH)
			.height(HEADERBUTTON_HEIGHT)
			.pad(stage.getWidth() * 0.005f, stage.getWidth() * 0.005f, stage.getHeight() * 0.08f, stage.getWidth() * 0.005f);
		leftHeaderTable.add(new Button(skin, "optionsbutton"))
			.width(HEADERBUTTON_WIDTH)
			.height(HEADERBUTTON_HEIGHT)
			.pad(stage.getWidth() * 0.005f, 0, stage.getHeight() * 0.08f, 0);
		mastertable.add(leftHeaderTable).left();
		mastertable.row();
	}
	
	private void addMainStackToMasterTable() {
		Stack stack = new Stack();
		
		// Create title tab
		titleTab = new Table();
		titleTab.add(new Label("Platformer", skin, "title"));
		stack.add(titleTab);
		
		// Create level selection tab
		levelselectTab = new com.jndm.game.gui.LevelSelection(game, this, stage, skin);
		levelselectTab.setVisible(false);
		stack.add(levelselectTab);
		
		currentTab = titleTab;
		mastertable.add(stack).fill().expand();
		mastertable.row();
	}
	
	private void addMainButtonsToMasterTable() {
		Table mainButtonTable = new Table();
		
		ImageButton playButton = new ImageButton(skin, "playbutton");
		playButton.getImageCell().maxSize(MAINBUTTON_WIDTH * 0.4f, MAINBUTTON_HEIGHT * 0.4f);
		playButton.getImageCell().expand().fill();
		playButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(currentTab instanceof LevelSelection) {
					currentTab.setVisible(false);
					titleTab.setVisible(true);
					currentTab = titleTab;
				} else {
					currentTab.setVisible(false);
					levelselectTab.setVisible(true);
					currentTab = levelselectTab;
				}
			}
		});
		
		ImageButton achievementButton = new ImageButton(skin, "achievementbutton");
		achievementButton.getImageCell().maxSize(MAINBUTTON_WIDTH * 0.4f, MAINBUTTON_HEIGHT * 0.4f);
		achievementButton.getImageCell().expand().fill();
		achievementButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO: Achievements tab
			}
		});
		
		ImageButton leaderBoardButton = new ImageButton(skin, "leaderboardbutton");
		leaderBoardButton.getImageCell().maxSize(MAINBUTTON_WIDTH * 0.4f, MAINBUTTON_HEIGHT * 0.4f);
		leaderBoardButton.getImageCell().expand().fill();
		leaderBoardButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO: Leaderboard tab
			}
		});
		
		ImageButton exitButton = new ImageButton(skin, "exitbutton");
		exitButton.getImageCell().maxSize(MAINBUTTON_WIDTH * 0.4f, MAINBUTTON_HEIGHT * 0.4f);
		exitButton.getImageCell().expand().fill();
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		mainButtonTable.add(playButton)
			.width(MAINBUTTON_WIDTH)
			.height(MAINBUTTON_HEIGHT)
			.padBottom(stage.getWidth() * 0.01f)
			.padRight(stage.getWidth() * 0.005f);
		mainButtonTable.add(achievementButton)
			.width(MAINBUTTON_WIDTH)
			.height(MAINBUTTON_HEIGHT)
			.padBottom(stage.getWidth() * 0.001f)
			.padRight(stage.getWidth() * 0.005f);
		mainButtonTable.add(leaderBoardButton)
			.width(MAINBUTTON_WIDTH)
			.height(MAINBUTTON_HEIGHT)
			.padBottom(stage.getWidth() * 0.001f)
			.padRight(stage.getWidth() * 0.005f);
		mainButtonTable.add(exitButton)
			.width(MAINBUTTON_WIDTH)
			.height(MAINBUTTON_HEIGHT)
			.padBottom(stage.getWidth() * 0.001f);
		
		mastertable.add(mainButtonTable);
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
