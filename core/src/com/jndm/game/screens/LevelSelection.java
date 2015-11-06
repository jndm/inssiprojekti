package com.jndm.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.jndm.game.MyGame;
import com.jndm.game.saving.Level;
import com.jndm.game.utils.Constants;
import com.jndm.game.utils.Utils;

public class LevelSelection implements Screen {
	
	private MyGame game;
	private Stage stage;
	private Skin skin;
	private Table mastertable;
	private TextureAtlas atlas;
	private int currentTab = 0;
	private Array<Table> levelButtonsTabs;
	
	private final int showMaxLevels = 15;
	private final int maxTabs = (int) Math.ceil(((double) Constants.MAXLEVELS) / showMaxLevels);
	
	private float LEVELBUTTON_WIDTH;
	private float LEVELBUTTON_HEIGHT;
	private float ARROW_BUTTON_WIDTH;
    private float ARROW_BUTTON_HEIGHT;
    private float OPTIONS_BUTTON_SIZE;
    
	private void createConstants() {
		LEVELBUTTON_WIDTH = stage.getWidth() * 0.165f;  
		LEVELBUTTON_HEIGHT = stage.getHeight() * 0.2f;
		
		ARROW_BUTTON_WIDTH  = stage.getWidth() * 0.15f;  
		ARROW_BUTTON_HEIGHT = stage.getHeight() * 0.13f;
		
		OPTIONS_BUTTON_SIZE = stage.getWidth() * 0.07f;
	}
	
	public LevelSelection(MyGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		atlas = game.assetManager.get(Constants.UI_ATLAS_PATH);
		stage = new Stage(game.uiViewport, game.sb);
		
		createConstants();
		
		skin = new Skin();
		skin.add("font", Utils.createFont(Constants.FONT_KENFACTOR_PATH, LEVELBUTTON_WIDTH * 0.35f, Constants.WHITE));
		skin.add("levelbuttonfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, LEVELBUTTON_WIDTH * 0.60f, Constants.WHITE));
		skin.add("levelbuttonsmallfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, LEVELBUTTON_WIDTH * 0.12f, Constants.WHITE));
		skin.addRegions(atlas);
		skin.load(Gdx.files.internal(Constants.LEVELSELECT_SKIN_PATH));
		
		mastertable = new Table(skin);
		mastertable.setBackground("background");
		mastertable.setBounds(0, 0, stage.getWidth(), stage.getHeight());
		
		mastertable.add(new Button(skin, "optionsbutton")).size(OPTIONS_BUTTON_SIZE).right().padBottom(stage.getHeight() * 0.01f);
		mastertable.row();
	
		addLevelButtonTabsToMasterTable();
		mastertable.add().fillY().expandY();

		stage.addActor(mastertable);
		Gdx.input.setInputProcessor(stage);
		
		/* THIS IS A STUPID HACK TO DRAW ALL BUTTONS AS VISIBLE FIRST SO THE ACTIONS WILL WORK WITH DISABLED BUTTONS, NO IDEA WHY...*/
		stage.draw();
		for(int i=1; i<levelButtonsTabs.size; i++) {
			levelButtonsTabs.get(i).setVisible(false);
		}
	}

	private void addLevelButtonTabsToMasterTable() {
		levelButtonsTabs = new Array<Table>();
		
		for(int i=0; i < maxTabs; i++) {
			levelButtonsTabs.add(createLevelSelectButtons((showMaxLevels * i) + 1));
		}
		
		// Add level select tables to stack so they become like tabs
		Stack stack = new Stack();
		for(Table t : levelButtonsTabs) {
			stack.add(t);
		}
		
		// Create buttons tab select buttons
		final Button nextTabButton = new Button(skin, "arrowright");
		final Button previousTabButton = new Button(skin, "arrowleft");
		final TextButton returnButton = new TextButton("Return", skin, "button");
		
		nextTabButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(currentTab < maxTabs - 1 && levelButtonsTabs.get(currentTab).getActions().size == 0) {	// if not last tab and sliding effect is complete
					levelButtonsTabs.get(currentTab).addAction(Actions.sequence(
							Actions.moveBy(-stage.getWidth(), 0, 0.5f), 
							Actions.visible(false)));
					levelButtonsTabs.get(currentTab + 1).addAction(Actions.sequence(
							Actions.moveTo(stage.getWidth(), 0), 
							Actions.visible(true), 
							Actions.moveBy(-stage.getWidth(), 0, 0.5f)));

					previousTabButton.setDisabled(false);
					currentTab += 1;
					if(currentTab == maxTabs) {
						nextTabButton.setDisabled(true);
					}
				}
			}
		});
		
		previousTabButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(currentTab > 0 && levelButtonsTabs.get(currentTab).getActions().size == 0) { // if not first tab and sliding effect is complete
					levelButtonsTabs.get(currentTab).addAction(Actions.sequence(
							Actions.moveBy(stage.getWidth(), 0, 0.5f), 
							Actions.visible(false)));
					levelButtonsTabs.get(currentTab - 1).addAction(Actions.sequence(
							Actions.moveTo(-stage.getWidth(), 0), 
							Actions.visible(true), 
							Actions.moveBy(stage.getWidth(), 0, 0.5f)));
					
					nextTabButton.setDisabled(false);
					currentTab -= 1;
					if(currentTab == 0) {
						previousTabButton.setDisabled(true);
					}
				}
			}
		});
		
		returnButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				stage.addAction(Actions.sequence(Actions.fadeOut(0.3f), Actions.run(new Runnable() {
				    public void run () {
				    	game.setScreen(new MainMenu(game));
				    }
				})));
			}
			
		});
		
		Table bg = new Table();
		bg.add(previousTabButton).width(ARROW_BUTTON_WIDTH).height(ARROW_BUTTON_HEIGHT).padTop(stage.getHeight() * 0.03f);
		bg.add(returnButton).width(stage.getWidth() / 3).height(ARROW_BUTTON_HEIGHT).pad(stage.getHeight() * 0.03f, stage.getWidth() / 12, 0, stage.getWidth() / 12);
		bg.add(nextTabButton).width(ARROW_BUTTON_WIDTH).height(ARROW_BUTTON_HEIGHT).padTop(stage.getHeight() * 0.03f);
		
		mastertable.add(stack);
		mastertable.row();
		
		mastertable.add(bg);
		mastertable.row();
	}

	private Table createLevelSelectButtons(int startingLevel) {
		
		Table levelButtonTable = new Table();
		for(int i = startingLevel; i < startingLevel + showMaxLevels; i++) {
			if(i <= Constants.MAXLEVELS) {
				final Level level = game.saveManager.loadDataValue("level"+i, Level.class);	//Load levelprogress from json
				
				TextButton button = new TextButton(i+"", skin, "levelbutton");
				button.row();
				button.add(new Label(""+level.getPb(), skin, "levelButtonSmallFont")).center();
				
				button.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						game.setScreen(new Play(game, level));
					}
				});
				
				if(!level.isAvailable()) {
					button.setDisabled(true);
				}
				
				levelButtonTable.add(button)
					.size(LEVELBUTTON_WIDTH, LEVELBUTTON_HEIGHT)
					.pad(0, LEVELBUTTON_WIDTH * 0.1f, LEVELBUTTON_HEIGHT * 0.1f, LEVELBUTTON_WIDTH * 0.1f);
						
			} else { // fill with empty cells if maxlevel % showmaxlevels != 0
				levelButtonTable.add()
					.size(LEVELBUTTON_WIDTH, LEVELBUTTON_HEIGHT)
					.pad(0, LEVELBUTTON_WIDTH * 0.1f, LEVELBUTTON_HEIGHT * 0.1f, LEVELBUTTON_WIDTH * 0.1f);
				
			}
			
			if(i != 0 && i % 5 == 0) { // If last of the row add row change
				levelButtonTable.row();
			}
		}

		return levelButtonTable;
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

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
	}
}
