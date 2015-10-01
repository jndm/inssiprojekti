package com.jndm.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.jndm.game.MyGame;
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
	private float gameRunningTime = 0;
	
	private Vector3 dragStartPoint;

	private final boolean DEBUG = true;
	private BitmapFont font = new BitmapFont();
	
	public Play(MyGame game) {
		this.game = game;
	}

	@Override
	public void show() {
		
		world = new World(new Vector2(0, -9.81f), true);
		world.setContactListener(new MyContactListener());
		
		try {
			gameWorld = new GameWorld(this, game, world, level);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		player = new Player(world);
		
		Gdx.input.setInputProcessor(new GestureDetector(createGestureListener()));
	}
	
	private MyGestureListener createGestureListener() {
		return new MyGestureListener() {
			@Override
			public boolean tap(float x, float y, int pointer, int button) {
				Vector3 touchpos = new Vector3(x, y, 0);
				if(touchpos.x >= game.gameViewport.getScreenWidth() / 2) {
					player.turnRight();
				} else {
					player.turnLeft();
				}
				return true;
			}
			
			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				if(!player.isJumping()) {	// If not already jumping start jump sequence
					if(!player.isAdjustingJump()) {
						dragStartPoint = new Vector3(x, y, 0);
						game.cam.unproject(dragStartPoint);
					}
					player.setAdjustingJump(true);
					}
				return false;
			}

			@Override
			public boolean panStop(float x, float y, int pointer, int button) {
				if(player.isAdjustingJump()) {
					Vector3 dragReleasePoint = new Vector3(x, y, 0);
					game.cam.unproject(dragReleasePoint);
					player.jump(dragStartPoint, dragReleasePoint);
					player.setAdjustingJump(false);
				}
				return false;
			}
		};
	}

	@Override
	public void render(float delta) {	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		game.sb.setProjectionMatrix(game.gameViewport.getCamera().combined);
		game.gameViewport.apply();
		gameWorld.render();
		
		//Always draw hud
		game.sb.setProjectionMatrix(game.hudCam.combined);
		game.uiViewport.apply();
		
		if(DEBUG) {
			game.sb.begin();
				font.draw(game.sb, "onGround: "+ (player.isOnGround() ? "true" : "false"), 10, game.uiViewport.getWorldHeight() * 0.95f);
				font.draw(game.sb, "onRightWall: "+ (player.isOnRightWall() ? "true" : "false"), 10, game.uiViewport.getWorldHeight() * 0.91f);
				font.draw(game.sb, "onLeftWall: "+ (player.isOnLeftWall() ? "true" : "false"), 10, game.uiViewport.getWorldHeight() * 0.87f);
				font.draw(game.sb, "isJumping: "+ (player.isJumping() ? "true" : "false"), 10, game.uiViewport.getWorldHeight() * 0.83f);
				font.draw(game.sb, "SpeedX: "+ player.getBody().getLinearVelocity().x + " SpeedY: "+ player.getBody().getLinearVelocity().y, 10, game.uiViewport.getWorldHeight() * 0.79f);
				font.draw(game.sb, "FPS: "+Gdx.graphics.getFramesPerSecond(), game.uiViewport.getWorldWidth() * 0.8f, game.uiViewport.getWorldHeight() * 0.95f);
			game.sb.end();
		}
		
		update(delta);	
	}
	
	private void update(float delta) {
		gameRunningTime  += delta;	
		player.update(delta);
		world.step(1/60f, 8, 3);
		updateCamera();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		world.dispose();
		gameWorld.dispose();
		font.dispose();
	}
}
