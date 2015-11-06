package com.jndm.game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jndm.game.MyGame;
import com.jndm.game.saving.Level;
import com.jndm.game.screens.LevelSelection;
import com.jndm.game.screens.Play;
import com.jndm.game.utils.Constants;
import com.jndm.game.utils.Utils;

public class Hud {
	
	private Play play;
	private MyGame game;
	
	private Stage stage;
	private Skin skin;
	private TextureAtlas atlas;
	private Label timerLabel;
	
	//Timer
	private float gameTime = 0;
	private String timeString = "00:00:00";
	
	public Hud(MyGame game, Play play) {
		this.play = play;
		this.game = game;
			
		atlas = game.assetManager.get(Constants.UI_ATLAS_PATH);
		
		stage = new Stage(game.uiViewport, game.sb);
		
		skin = new Skin();
		skin.add("timerfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, stage.getHeight() * 0.1f * 0.5f, Constants.DARK_BLUE));
		skin.add("bigfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, stage.getHeight() * 0.1f * 0.55f, Constants.WHITE));
		skin.add("normalfont", Utils.createFont(Constants.FONT_KENFACTOR_PATH, stage.getHeight() * 0.1f * 0.37f, Constants.WHITE));
		skin.addRegions(atlas);
		skin.load(Gdx.files.internal(Constants.HUD_SKIN_PATH));
		
		// Init root table
		Table mastertable = new Table(skin);
		mastertable.setBounds(0, 0, stage.getWidth(), stage.getHeight());

		// Add timerlabel to root table
		timerLabel = new Label("00:00:00", skin, "timerlabel");
		mastertable.add(timerLabel).left().padLeft(stage.getWidth() * 0.01f);
		
		// Create options button and dialog. Add to root table.
//		Button optionsButton = createOptionsButton();
//		mastertable.add(optionsButton).size(stage.getWidth() * 0.07f).right();

		mastertable.row();
		mastertable.add().colspan(2).expand().fill();	// Add empty cell to fill up rest of the stage
		
		stage.addActor(mastertable);
	}
	
	public void render() {
		stage.act(); 
		stage.draw();
	}

	public void updateTimer(float delta) {
		gameTime += delta;
	
		int minutes = (int) (gameTime / 60);
		int seconds = (int) (gameTime % 60);
		int milliseconds = (int) ((gameTime % 60 - seconds) * 1000);
		
		String mstext = String.format("%03d", milliseconds);
		String stext = String.format("%02d", seconds);
		String mintext = String.format("%02d", minutes);
		
		timeString = mintext+":"+stext+"."+mstext;
		
		timerLabel.setText(timeString);
	}
	

	public void dispose() {
		stage.dispose();
		skin.dispose();
	}

	public String getTimeString() {
		return timeString;
	}

	public Stage getStage() {
		return stage;
	}

	public void showEndingStatusDialog(boolean won) {
		Dialog endStatusDialog = new Dialog("", skin, "options");
		Table contentTable = endStatusDialog.getContentTable();
		Table buttonTable = endStatusDialog.getButtonTable();
		
		if(won) {
			contentTable.add(new Label("Congratulations!\n", skin, "finishtitle")).pad(0, 10, -20, 10);
			contentTable.row();
			contentTable.add(new Label("Time: "+timeString, skin, "finishtext")).left().padLeft(60);
			contentTable.row();
			contentTable.add(new Label("Best time: "+play.getLevel().getPb(), skin, "finishtext")).left().pad(0, 60, 15, 0);
		} else {
			contentTable.add(new Label("You are dead!\n", skin, "finishtitle")).pad(0, 10, -20, 10);
			contentTable.row();
			contentTable.add(new Label("Try again!", skin, "finishtext")).left().padLeft(10);
		}
		
		TextButton restartButton = new TextButton("R", skin, "optionsbutton");
		restartButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new Play(game, play.getLevel()));
			}
		});
		
		TextButton returnButton = new TextButton("<<", skin, "optionsbutton");
		returnButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new LevelSelection(game));
			}
		});
		
		TextButton nextButton = new TextButton(">>", skin, "optionsbutton");
		final Level nextLevel = (Level) game.saveManager.loadDataValue("level"+ (play.getLevel().getNumber()+1), Level.class);
		nextButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new Play(game, nextLevel));
			}
		});
		
		if(!nextLevel.isAvailable()) {		// Disable button if lost level and next level not yet available
			nextButton.setDisabled(true);
		}
		
		//Table buttonTable = new Table();
		buttonTable.add(returnButton)
			.width(stage.getWidth() * 0.1f)
			.height(stage.getHeight() * 0.1f);
	
		buttonTable.add(restartButton)
			.width(stage.getWidth() * 0.1f)
			.height(stage.getHeight() * 0.1f)
			.pad(0, stage.getWidth() * 0.01f, 0, stage.getWidth() * 0.01f);
	
		buttonTable.add(nextButton)
			.width(stage.getWidth() * 0.1f)
			.height(stage.getHeight() * 0.1f);
		
		endStatusDialog.show(stage);
	}
	/*	
	private Button createOptionsButton() {
		Button optionsButton = new Button(skin, "optionsbutton");
		optionsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				play.setPlayState(PlayState.PAUSED);
				new HudOptionsDialog("", skin, "options").show(stage);
			}
		});
		
		return optionsButton;
	}
	
	public class HudOptionsDialog extends Dialog {

		public HudOptionsDialog(String title, Skin skin, String windowStyleName) {
			super(title, skin, windowStyleName);
			addSoundCheckBox();
			addButtons();
			
			setResizable(false);
		}

		private void addButtons() {
			TextButton resumeButton = new TextButton("Resume", skin, "optionsbutton");
			resumeButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					play.setPlayState(PlayState.PLAY);
					hide();
				}
			});
			
			TextButton restartButton = new TextButton("Restart", skin, "optionsbutton");
			restartButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.setScreen(new Play(game, play.getLevel()));
				}
			});
			
			TextButton quitButton = new TextButton("Quit", skin, "optionsbutton");
			quitButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.setScreen(new LevelSelection(game, Constants.BLUE_UI_ATLAS));
				}
			});
			
			text(new Label("Options", skin, "optionstitle")).pad(0, stage.getWidth() * 0.01f, 0, stage.getWidth() * 0.01f);
			row();
			add(resumeButton)
				.width(stage.getWidth() * 0.3f)
				.height(stage.getHeight() * 0.1f)
				.pad(0, stage.getWidth() * 0.01f, 0, stage.getWidth() * 0.01f);
			row();
			
			add(restartButton)
				.width(stage.getWidth() * 0.3f)
				.height(stage.getHeight() * 0.1f)
				.pad(0, stage.getWidth() * 0.01f, 0, stage.getWidth() * 0.01f);
			row();
			
			add(quitButton)
				.width(stage.getWidth() * 0.3f)
				.height(stage.getHeight() * 0.1f)
				.pad(0, stage.getWidth() * 0.01f, stage.getHeight() * 0.03f, stage.getWidth() * 0.01f);
		}

		private void addSoundCheckBox() {
			final CheckBox soundCheckbox = new CheckBox("", skin);
			soundCheckbox.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if(soundCheckbox.isChecked()) {
						game.options.setSoundOn(true);
					} else {
						game.options.setSoundOn(false);
					}
				}
			});
			
			soundCheckbox.setChecked(game.options.isSoundOn());
			
			Table checkBoxTable = new Table(); // To add a little padding on checkbox text
			checkBoxTable.add(soundCheckbox).pad(0, stage.getWidth() * 0.008f, 0, stage.getWidth() * 0.008f);
			checkBoxTable.add(new Label("Sound", skin, "label"));
			
			row();
			add(checkBoxTable).left();
		}
	}
	*/
}

