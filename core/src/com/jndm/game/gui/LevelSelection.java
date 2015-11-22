package com.jndm.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.jndm.game.MyGame;
import com.jndm.game.saving.Level;
import com.jndm.game.screens.Menu;
import com.jndm.game.screens.Play;
import com.jndm.game.utils.Constants;

public class LevelSelection extends Table {
	
	private MyGame game;
	private Menu menu;
	private Stage stage;
	private Skin skin;
	private int currentTab = 0;
	private Array<Table> levelButtonsTabs;
	private Table mastertable;
	
	private final int showMaxLevels = 12;
	private final int showMaxPerRow = 4;
	private final int maxTabs = (int) Math.ceil(((double) Constants.MAXLEVELS) / showMaxLevels);
	
	public LevelSelection(MyGame game, Menu menu, Stage stage, Skin skin) {
		this.stage = stage;
		this.skin = skin;
		this.game = game;
		this.menu = menu;
		
		mastertable = new Table(skin);
		
		addLevelButtonTabsToMasterTable();

		stage.addActor(mastertable);
		Gdx.input.setInputProcessor(stage);
		
		/* THIS IS A STUPID HACK TO DRAW ALL BUTTONS AS VISIBLE FIRST SO THE ACTIONS WILL WORK WITH DISABLED BUTTONS, NO IDEA WHY...*/
		stage.draw();
		for(int i=1; i<levelButtonsTabs.size; i++) {
			levelButtonsTabs.get(i).setVisible(false);
		}
		
		this.add(mastertable);
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

		mastertable.add(stack).colspan(2);
		mastertable.row();
		
		mastertable.add(previousTabButton).width(menu.ARROW_BUTTON_WIDTH).height(menu.ARROW_BUTTON_HEIGHT).pad(stage.getHeight() * 0.01f, stage.getHeight() * 0.05f, 0, 0).left();
		mastertable.add(nextTabButton).width(menu.ARROW_BUTTON_WIDTH).height(menu.ARROW_BUTTON_HEIGHT).pad(stage.getHeight() * 0.01f, 0, 0, stage.getHeight() * 0.05f).right();
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
					.size(menu.LEVELBUTTON_WIDTH, menu.LEVELBUTTON_HEIGHT)
					.pad(0, menu.LEVELBUTTON_WIDTH * 0.07f, menu.LEVELBUTTON_HEIGHT *  0.05f, menu.LEVELBUTTON_WIDTH *  0.07f);
						
			} else { // fill with empty cells if maxlevel % showmaxlevels != 0
				levelButtonTable.add()
					.size(menu.LEVELBUTTON_WIDTH, menu.LEVELBUTTON_HEIGHT)
					.pad(0, menu.LEVELBUTTON_WIDTH * 0.05f, menu.LEVELBUTTON_HEIGHT *  0.05f, menu.LEVELBUTTON_WIDTH *  0.05f);
				
			}
			
			if(i != 0 && i % showMaxPerRow == 0) { // If last of the row add row change
				levelButtonTable.row();
			}
		}

		return levelButtonTable;
	}
	
}
