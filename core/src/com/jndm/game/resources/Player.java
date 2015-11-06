package com.jndm.game.resources;

import net.dermetfan.gdx.graphics.g2d.AnimatedBox2DSprite;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.jndm.game.MyGame;
import com.jndm.game.screens.Play;
import com.jndm.game.utils.Constants;
import com.jndm.game.utils.Utils;

public class Player {
	
	private MyGame game;
	private Play play;
	private TextureAtlas atlas;
	private Body body;
	private final float PLAYER_WIDTH = 1f, PLAYER_HEIGHT = 1f;
		
	private boolean onGround = false;
	private boolean onRightWall = false;
	private boolean onLeftWall = false;
	
	private boolean movingLeft = false;
	private boolean movingRight = true;
	
	private boolean jumping = false;
	private boolean adjustingJump = false;
	private long jumpStartTime;
	private final long JUMPDELAY = 400;
	
	private Array<AnimatedBox2DSprite> animations;
	private AnimatedBox2DSprite adjustingJumpAnimation;
	private AnimatedBox2DSprite runAnimation;
	private AnimatedBox2DSprite jumpAnimation;
	private AnimatedBox2DSprite fallAnimation;
	
	private ParticleEffect bloodEffect;
	
	private boolean win = false;
	private boolean alive = true;
	
	public Player(MyGame game, World world, Play play) {		
		this.game = game;
		this.play = play;
		
		if(!game.assetManager.isLoaded(Constants.PLAYER_ATLAS_PATH)) {
			game.assetManager.load(Constants.PLAYER_ATLAS_PATH, TextureAtlas.class);
			game.assetManager.finishLoading();
		}
		
		atlas = game.assetManager.get(Constants.PLAYER_ATLAS_PATH);
		
		createAnimations();
		createBox2dData(world);
		
		bloodEffect = new ParticleEffect();
		bloodEffect.load(Gdx.files.internal("effects/blood.p"), Gdx.files.internal("effects"));
		bloodEffect.start();
		
		movingRight = true; // set movement to right
	}
	
	private void createAnimations() {
		animations = new Array<AnimatedBox2DSprite>();
		
		runAnimation = new AnimatedBox2DSprite(new AnimatedSprite(Utils.createAnimation(atlas, "run", 2, 1/5f)));
		runAnimation.setSize(runAnimation.getWidth() / Constants.PPM, runAnimation.getHeight() / Constants.PPM);
		runAnimation.getAnimation().setPlayMode(PlayMode.LOOP);
		
		adjustingJumpAnimation = new AnimatedBox2DSprite(new AnimatedSprite(Utils.createAnimation(atlas, "adjustingjump", 1, 1/10f)));
		adjustingJumpAnimation.setSize(adjustingJumpAnimation.getWidth() / Constants.PPM, adjustingJumpAnimation.getHeight() / Constants.PPM);
		
		jumpAnimation = new AnimatedBox2DSprite(new AnimatedSprite(Utils.createAnimation(atlas, "jump", 2, 1/10f)));
		jumpAnimation.setSize(jumpAnimation.getWidth() / Constants.PPM, jumpAnimation.getHeight() / Constants.PPM);
		
		fallAnimation = new AnimatedBox2DSprite(new AnimatedSprite(Utils.createAnimation(atlas, "fall", 1, 1/10f)));
		fallAnimation.setSize(fallAnimation.getWidth() / Constants.PPM, fallAnimation.getHeight() / Constants.PPM);
		
		// Add all animations to array for simplier flipping
		animations.add(runAnimation);
		animations.add(adjustingJumpAnimation);
		animations.add(jumpAnimation);
		animations.add(fallAnimation);
	}
	
	public void render(float delta) {
		resetAnimations();
		flipAnimations();	
		
		game.sb.begin();
		if(alive) {
			if(adjustingJump) {
				adjustingJumpAnimation.draw(game.sb, body);
			} 
			else if(jumping && body.getLinearVelocity().y >= 0) {
				jumpAnimation.draw(game.sb, body);
			} 
			else if(!onGround && body.getLinearVelocity().y < 0) {
				fallAnimation.draw(game.sb, body);
			} 
			else {
				runAnimation.draw(game.sb, body);
			}
		} else {
			bloodEffect.setPosition(body.getPosition().x, body.getPosition().y);
			bloodEffect.draw(game.sb, delta);
		}
		game.sb.end();
	}

	private void flipAnimations() {
		for(AnimatedBox2DSprite anim : animations) {
			if((movingRight && anim.isFlipX()) || (movingLeft && !anim.isFlipX())) {
				anim.flipFrames(true, false);
				anim.update();
			}
		}
	}

	private void resetAnimations() {
		if(!adjustingJump && adjustingJumpAnimation.isAnimationFinished()) {
			adjustingJumpAnimation.setTime(0);
		}
		
		if(!jumping && jumpAnimation.isAnimationFinished()) {
			jumpAnimation.setTime(0);
		}
		
		if(body.getLinearVelocity().y >= 0 && fallAnimation.isAnimationFinished()) {
			fallAnimation.setTime(0);
		}
	}

	public void update(float delta) {
		
		if(!jumping && !adjustingJump) {
			move(delta);
		} else if(adjustingJump) {
			stop();
		} else {
			checkIfJumpIsDone(delta);
		}
		
		checkIfOutOfTheWorld();
	
	}

	private void checkIfOutOfTheWorld() {
		if(body.getPosition().x - PLAYER_WIDTH / 2 > play.getGameWorld().getLevelWidth() || //Right side
				body.getPosition().x + PLAYER_WIDTH / 2 < 0 ||								//Left side
				body.getPosition().y + PLAYER_HEIGHT / 2 < 0) {								//Bottom
			alive = false;
		}
	}

	public void stop() {
		body.setLinearVelocity(0f, 0f);
	}

	private void checkIfJumpIsDone(float delta) {
		if((jumpStartTime + JUMPDELAY < System.currentTimeMillis())  
				&& (onGround || onLeftWall || onRightWall)) {
			jumping = false;
		}
	}

	private void move(float delta) {	
		Vector2 curSpeed = body.getLinearVelocity();	
		
		float maxVel = 0;
		
		if(movingRight) {
			maxVel = Constants.DEFAULTVELOCITY;
		} else if(movingLeft) {
			maxVel = -Constants.DEFAULTVELOCITY;
		}
	
		// f = ma -> f = mv/t -> want to change speed instantly so ->  f = m * (max_v - current_v) / t	
		float velChange = maxVel - curSpeed.x;
		float force = body.getMass() * velChange / delta;
		
		body.applyForceToCenter(new Vector2(force, 0), true);
	}
	
	public void jump(Vector2 impulse) {
		float forcex = body.getMass() * impulse.x;	// Compensate mass - applying impulse so disregard time factor
		float forcey = body.getMass() * impulse.y;
		
		body.applyLinearImpulse(new Vector2(forcex, forcey), body.getWorldCenter(), true);
		
		jumping = true;
		jumpStartTime = System.currentTimeMillis();
	}

	public void turnLeft() {
		movingRight = false;
		movingLeft = true;
	}
	
	public void turnRight() {
		movingRight = true;
		movingLeft = false;
	}
	

	public boolean canJump() {
		return !jumping && (onGround || onLeftWall || onRightWall); 
	}
	
	private void createBox2dData(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(play.getGameWorld().getSpawnpoint().x, play.getGameWorld().getSpawnpoint().y);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;		//set shape to right first
		fixtureDef.density = 2f; 
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		fixtureDef.filter.categoryBits = Constants.PLAYER_CATEGORY;
		fixtureDef.filter.maskBits = Constants.PLAYER_MASK;
		
		//Create groundsensor
		PolygonShape groundSensorShape = new PolygonShape();
		groundSensorShape.setAsBox(0.47f, 0.05f, new Vector2(0, -0.5f), 0);
		
		FixtureDef groundSensorFixtureDef = new FixtureDef();
		groundSensorFixtureDef.shape = groundSensorShape;		//set shape to right first
		groundSensorFixtureDef.isSensor = true;
		
		//Create rightSensor
		PolygonShape rightSensorShape = new PolygonShape();
		rightSensorShape.setAsBox(0.05f, 0.47f, new Vector2(0.5f, 0), 0);
		
		FixtureDef rightSensorFixtureDef = new FixtureDef();
		rightSensorFixtureDef.shape = rightSensorShape;		//set shape to right first
		rightSensorFixtureDef.isSensor = true;
		
		//Create leftSensor
		PolygonShape leftSensorShape = new PolygonShape();
		leftSensorShape.setAsBox(0.05f, 0.47f, new Vector2(-0.5f, 0), 0);
		
		FixtureDef leftSensorFixtureDef = new FixtureDef();
		leftSensorFixtureDef.shape = leftSensorShape;		//set shape to right first
		leftSensorFixtureDef.isSensor = true;
		
		//Create body
		body = world.createBody(bodyDef);
		body.setFixedRotation(true);
		body.setUserData(this);
		
		//Create fixtures
		body.createFixture(fixtureDef);
		body.createFixture(groundSensorFixtureDef).setUserData("groundSensor");
		body.createFixture(rightSensorFixtureDef).setUserData("rightSensor");
		body.createFixture(leftSensorFixtureDef).setUserData("leftSensor");
		
		shape.dispose();
		groundSensorShape.dispose();
		rightSensorShape.dispose();
		leftSensorShape.dispose();
	}

	public Body getBody() {
		return body;
	}

	public void setJumping(boolean jump) {
		jumping = jump;
	}
	
	public boolean isJumping() {
		return jumping;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public boolean isOnRightWall() {
		return onRightWall;
	}

	public void setOnRightWall(boolean onRightWall) {
		this.onRightWall = onRightWall;
	}

	public boolean isOnLeftWall() {
		return onLeftWall;
	}

	public void setOnLeftWall(boolean onLeftWall) {
		this.onLeftWall = onLeftWall;
	}

	public boolean isAdjustingJump() {
		return adjustingJump;
	}

	public void setAdjustingJump(boolean adjustingJump) {
		this.adjustingJump = adjustingJump;
	}

	public void setWin(boolean win) {
		this.win  = win;
	}
	
	public boolean hasWon() {
		return win;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	public boolean hasLost() {
		return !alive;
	}

	public ParticleEffect getBloodEffect() {
		return bloodEffect;
	}

}
