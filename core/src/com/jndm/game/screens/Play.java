package com.jndm.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jndm.game.MyGame;
import com.jndm.game.gui.Hud;
import com.jndm.game.handlers.MyContactListener;
import com.jndm.game.handlers.MyGestureListener;
import com.jndm.game.resources.GameWorld;
import com.jndm.game.resources.Player;
import com.jndm.game.saving.Level;
import com.jndm.game.utils.Constants;

public class Play implements Screen {
	
	private MyGame game;
	private GameWorld gameWorld;
	private World world;
	private Level level;
	private Player player;
	private Hud hud;
	private boolean levelFinished = false;
	
	private Vector3 dragStartPoint;
	private boolean dragStartPointSet = false;
	private Vector2 jumpImpulse;
	private Array<Vector2> trajectoryPoints;

	private BitmapFont font = new BitmapFont();
	
	public Play(MyGame game, Level level) {
		this.game = game;
		this.level = level;
	}

	@Override
	public void show() {
		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(new MyContactListener());
		
		try {
			gameWorld = new GameWorld(game, world, level);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		player = new Player(game, world, this);
		hud = new Hud(game, this);
	
		InputProcessor gameInputProcessor = new GestureDetector(createGestureListener());	
		InputProcessor hudInputProcessor = hud.getStage();
		Gdx.input.setInputProcessor(new InputMultiplexer(hudInputProcessor, gameInputProcessor));
	}
	
	private MyGestureListener createGestureListener() {
		return new MyGestureListener() {
			@Override
			public boolean tap(float x, float y, int pointer, int button) {
				if(!player.isOnGround() && (player.isOnLeftWall() || player.isOnRightWall())) {	//Use tapping wallsliding
					Vector3 touchpos = new Vector3(x, y, 0);
					System.out.println("tap");
					if(touchpos.x >= game.gameViewport.getScreenWidth() / 2) {
						player.turnRight();
					} else {
						player.turnLeft();
					}
				}
				return true;
			}
			
			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				if(player.isOnGround() || (!player.isOnLeftWall() && !player.isOnRightWall())) {	// Use touchdown if not wallsliding
					Vector3 touchpos = new Vector3(x, y, 0);
					System.out.println("touchdown");
					if(touchpos.x >= game.gameViewport.getScreenWidth() / 2) {
						player.turnRight();
					} else {
						player.turnLeft();
					}
				}
				return true;
			}
			
			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				if(player.canJump()) {	// If not already jumping start jump sequence
					if(!dragStartPointSet) {
						dragStartPoint = new Vector3(x, y, 0);
						game.cam.unproject(dragStartPoint);
						dragStartPointSet = true;
						player.stop();
					}
					Vector3 currentDragPoint = new Vector3(x, y, 0);
					game.cam.unproject(currentDragPoint);
					calculateJumpImpulse(currentDragPoint);
					player.setAdjustingJump(true);
				}
				return false;
			}

			@Override
			public boolean panStop(float x, float y, int pointer, int button) {
				if(player.isAdjustingJump()) {
					Vector3 dragReleasePoint = new Vector3(x, y, 0);
					game.cam.unproject(dragReleasePoint);
					player.jump(jumpImpulse);
					player.setAdjustingJump(false);
					dragStartPointSet = false;
				}
				return false;
			}
		};
	}

	protected void calculateJumpImpulse(Vector3 currentDragPoint) {
		Vector2 impulse = new Vector2(dragStartPoint.x - currentDragPoint.x, dragStartPoint.y - currentDragPoint.y);
		impulse = impulse.scl(1.5f);	// Scale with 5 so no need to drag too far
		
		// Clamp impulse x
		if(!player.isOnGround() && player.isOnRightWall()) {	// If player is on wall
			player.turnLeft();									// let player only jump on opposite direction
			if(impulse.x > -0.5f) {								
				impulse.x = -0.5f;
			}  else if (impulse.x < -Constants.MAXJUMPVELOCITY.x) {
				impulse.x = -Constants.MAXJUMPVELOCITY.x;
			}
		} else if(!player.isOnGround() && player.isOnLeftWall()) {
			player.turnRight();
			if(impulse.x < 0.5f) {
				impulse.x = 0.5f;
			} else if(impulse.x > Constants.MAXJUMPVELOCITY.x) {
				impulse.x = Constants.MAXJUMPVELOCITY.x;	
			}
		} else {
			if(impulse.angle() > 90 && impulse.angle() < 270) { 
				player.turnLeft(); 
			}  else { 
				player.turnRight(); 
			}
			
			if(impulse.x < -Constants.MAXJUMPVELOCITY.x) {		// If on ground
				impulse.x = -Constants.MAXJUMPVELOCITY.x;		// let player jump where ever he wants
			} else if(impulse.x > Constants.MAXJUMPVELOCITY.x) {
				impulse.x = Constants.MAXJUMPVELOCITY.x;	
			}
		}
		
		// Clamp impulse y
		if(impulse.y > Constants.MAXJUMPVELOCITY.y) {
			impulse.y = Constants.MAXJUMPVELOCITY.y;
		} else if (impulse.y < -Constants.MAXJUMPVELOCITY.y) {
			impulse.y = -Constants.MAXJUMPVELOCITY.y;
		}
		
		calculateProjectileTrajectory(impulse);
		jumpImpulse = impulse;
		
	}

	protected void calculateProjectileTrajectory(Vector2 impulse) {
		trajectoryPoints = new Array<Vector2>();	
		
		// fx(t) = x + vx * t
		// fy(t) = y + vy * t + 1/2 * g * t^2
		float time = 1/10f;
		for(int i=0; i<Constants.MAXTRAJECTORYPOINTCOUNT; i++) {
			float x = player.getBody().getPosition().x + (impulse.x * time);
			float y = player.getBody().getPosition().y + (impulse.y * time) - (0.5f * 9.81f * time * time); 
			trajectoryPoints.add(new Vector2(x, y));
			time += 1/10f;
		}
		
	}

	@Override
	public void render(float delta) {	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.sb.setProjectionMatrix(game.gameViewport.getCamera().combined);
		game.shapeRenderer.setProjectionMatrix(game.gameViewport.getCamera().combined);
		game.gameViewport.apply();
		game.sb.setColor(Color.WHITE); // Stage doesn't clean up the SpriteBatch's color when it's done (stage is sharing the same sb)
		
		gameWorld.render();
		player.render(delta);
		
		if(player.isAdjustingJump()) {
			game.shapeRenderer.setColor(Color.RED);
			game.shapeRenderer.begin(ShapeType.Filled);
			for(Vector2 tp : trajectoryPoints) {
				game.shapeRenderer.circle(tp.x, tp.y, 0.1f, 20);
			}
			game.shapeRenderer.end();
		}
		
		//Always draw hud
		game.sb.setProjectionMatrix(game.hudCam.combined);
		game.uiViewport.apply();
		
		hud.render();
		
		if(game.DEBUG) {
			game.sb.begin();
				font.draw(game.sb, "onGround: "+ (player.isOnGround() ? "true" : "false"), 10, game.uiViewport.getWorldHeight() * 0.92f);
				font.draw(game.sb, "onRightWall: "+ (player.isOnRightWall() ? "true" : "false"), 10, game.uiViewport.getWorldHeight() * 0.88f);
				font.draw(game.sb, "onLeftWall: "+ (player.isOnLeftWall() ? "true" : "false"), 10, game.uiViewport.getWorldHeight() * 0.84f);
				font.draw(game.sb, "isJumping: "+ (player.isJumping() ? "true" : "false"), 10, game.uiViewport.getWorldHeight() * 0.80f);
				font.draw(game.sb, "SpeedX: "+ player.getBody().getLinearVelocity().x + " SpeedY: "+ player.getBody().getLinearVelocity().y, 10, game.uiViewport.getWorldHeight() * 0.76f);
				font.draw(game.sb, "x: "+ player.getBody().getPosition().x + " y: "+ player.getBody().getPosition().x, 10, game.uiViewport.getWorldHeight() * 0.72f);
				font.draw(game.sb, "FPS: "+Gdx.graphics.getFramesPerSecond(), game.uiViewport.getWorldWidth() * 0.8f, game.uiViewport.getWorldHeight() * 0.95f);
			game.sb.end();
		}
		
		update(delta);	
	}
	
	private void update(float delta) {
		if(!player.hasWon() && !player.hasLost()) {
			player.update(delta);
			world.step(delta, 8, 3);
			gameWorld.update(delta);	// have to call this after world step since its updating sprite positions
			updateCamera();
			hud.updateTimer(delta);
		} else if(player.hasWon() && !levelFinished){
			winLevel();
		} else if(player.hasLost() && !levelFinished) {
			loseLevel();
		}
	}
	
	private void loseLevel() {
		if(player.getBloodEffect().isComplete()) {
			hud.showEndingStatusDialog(false);
			levelFinished = true;
		}
	}

	private void winLevel() {
		saveGame();
		hud.showEndingStatusDialog(true);
		levelFinished = true;
	}

	private void saveGame() {
		boolean valueChanged = false;
		if(level.getPb().equals("00:00.00") || hud.getTimeString().compareTo(level.getPb()) < 0) {
			level.setPb(hud.getTimeString());
			valueChanged = true;
		}
		
		if(!level.isPassed()) {
			level.setPassed(true);
			if(level.getNumber() < 2) {	// for prototype number 5, otherwise Constant.MAXLEVEL
				Level nextLevel = game.saveManager.loadDataValue("level"+(level.getNumber()+1), Level.class);
				nextLevel.setAvailable(true);
			}
			valueChanged = true;
		}
		
		if(valueChanged) {
			game.saveManager.saveDataValue(level.getName(), level);
		}
	}

	private void updateCamera() {
		Vector2 playerpos = player.getBody().getPosition();
		
		float camw = game.cam.viewportWidth;
		float camh = game.cam.viewportHeight;
		
		// Make sure player won't see outside of a level
		if(playerpos.x - camw/2 <= 0) {
			game.cam.position.x = camw/2;
		} else if(playerpos.x + camw/2 >= gameWorld.getLevelWidth()) {
			game.cam.position.x = gameWorld.getLevelWidth() - camw/2;
		} else {
			game.cam.position.x = playerpos.x;
		}
		
		if(playerpos.y - camh/2 <= 0) {
			game.cam.position.y = camh/2;
		} else if(playerpos.y + camh/2 >= gameWorld.getLevelHeight()) {
			game.cam.position.y = gameWorld.getLevelHeight() - camh/2;
		} else {
			game.cam.position.y = playerpos.y;
		}
	
		game.cam.update();
	}

	@Override
	public void resize(int width, int height) {
		game.gameViewport.update(width, height, true);
		game.uiViewport.update(width, height, true);
		updateCamera();
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
		System.out.println("Disposed");
		world.dispose();
		gameWorld.dispose();
		font.dispose();
		hud.dispose();
	}

	public Player getPlayer() {
		return player;
	}

	public Level getLevel() {
		return level;
	}
	
	public GameWorld getGameWorld() {
		return gameWorld;
	}
}
