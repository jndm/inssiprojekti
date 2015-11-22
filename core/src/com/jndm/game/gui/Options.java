package com.jndm.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.jndm.game.MyGame;
import com.jndm.game.screens.Menu;
import com.jndm.game.utils.Constants;

public class Options extends Table {
	
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
	
	public Options(MyGame game, Menu menu, Stage stage, Skin skin) {
		this.stage = stage;
		this.skin = skin;
		this.game = game;
		this.menu = menu;
		
		mastertable = new Table(skin);

		stage.addActor(mastertable);
		Gdx.input.setInputProcessor(stage);
		
		/* THIS IS A STUPID HACK TO DRAW ALL BUTTONS AS VISIBLE FIRST SO THE ACTIONS WILL WORK WITH DISABLED BUTTONS, NO IDEA WHY...*/
		stage.draw();
		for(int i=1; i<levelButtonsTabs.size; i++) {
			levelButtonsTabs.get(i).setVisible(false);
		}
		
		this.add(mastertable);
	}

}
