package com.jndm.game.resources;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.jndm.game.screens.Play;

public class Player {
	
	private Play play;
	private Body body;
	
	// Movement
	private float defaultVelocity = 7f;
	private Vector2 maxJumpVelocity = new Vector2(14f, 16f);
	
	private boolean onGround = false;
	private boolean onRightWall = false;
	private boolean onLeftWall = false;
	
	private boolean movingLeft = false;
	private boolean movingRight = true;
	
	private boolean jumping = false;
	private boolean adjustingJump = false;
	private long jumpStartTime;
	private final long JUMPDELAY = 100;
	
	//Testing, remove after
	private Vector2 spawnpoint = new Vector2(3f, 4f);
	
	public Player(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(spawnpoint.x, spawnpoint.y);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.5f, 0.5f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;		//set shape to right first
		fixtureDef.density = 2f; 
		fixtureDef.friction = 0f;
		fixtureDef.restitution = 0f;
		//fixtureDef.filter.categoryBits = Constants.PLAYER_CATEGORY;
		//fixtureDef.filter.maskBits = Constants.PLAYER_MASK;
		
		//Create groundsensor
		PolygonShape groundSensorShape = new PolygonShape();
		groundSensorShape.setAsBox(0.48f, 0.05f, new Vector2(0, -0.5f), 0);
		
		FixtureDef groundSensorFixtureDef = new FixtureDef();
		groundSensorFixtureDef.shape = groundSensorShape;		//set shape to right first
		groundSensorFixtureDef.isSensor = true;
		
		//Create rightSensor
		PolygonShape rightSensorShape = new PolygonShape();
		rightSensorShape.setAsBox(0.05f, 0.48f, new Vector2(0.5f, 0), 0);
		
		FixtureDef rightSensorFixtureDef = new FixtureDef();
		rightSensorFixtureDef.shape = rightSensorShape;		//set shape to right first
		rightSensorFixtureDef.isSensor = true;
		
		//Create leftSensor
		PolygonShape leftSensorShape = new PolygonShape();
		leftSensorShape.setAsBox(0.05f, 0.48f, new Vector2(-0.5f, 0), 0);
		
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
		
		movingRight = true; // set movement to right
	}
	
	public void update(float delta) {
		if(!jumping) {
			move(delta);
		} else {
			checkIfJumpIsDone(delta);
		}
	}

	private void checkIfJumpIsDone(float delta) {
		if((jumpStartTime + JUMPDELAY < System.currentTimeMillis())  && (onGround || onLeftWall || onRightWall)) {
			jumping = false;
		}
	}

	private void move(float delta) {	
		Vector2 curSpeed = body.getLinearVelocity();
		
		// If player is adjusting jumping, stop movement
		if(adjustingJump) {
			body.setLinearVelocity(0f, 0f);
			return;
		}		
		
		// If not jumping or adjusting jump, move normally
		float maxVel = 0;
		
		if(movingRight) {
			maxVel = defaultVelocity;
		} else if(movingLeft) {
			maxVel = -defaultVelocity;
		}
	
		// f = ma -> f = mv/t -> want to change speed instantly so ->  f = m * (max_v - current_v) / t	
		float velChange = maxVel - curSpeed.x;
		float force = body.getMass() * velChange / delta;
		
		body.applyForceToCenter(new Vector2(force, 0), true);

	}
	
	public void jump(Vector3 dragSp, Vector3 dragRp) {
		
		// Check direction (left or right)
		Vector2 normal = new Vector2(dragSp.x - dragRp.x, dragSp.y - dragRp.y);
		if(normal.angle() > 90 && normal.angle() < 270) {
			turnLeft();
		} else {
			turnRight();
		}
		
		Vector2 impulse = normal.scl(5);	// Scale with 5 so no need to drag too far
		
		// Clamp impulse x
		if(impulse.x > maxJumpVelocity.x) {
			impulse.x = maxJumpVelocity.x;	
		} else if (impulse.x < -maxJumpVelocity.x) {
			impulse.x = -maxJumpVelocity.x;
		}
		
		// Clamp impulse y
		if(impulse.y > maxJumpVelocity.y) {
			impulse.y = maxJumpVelocity.y;
		} else if (impulse.y < -maxJumpVelocity.y) {
			impulse.y = -maxJumpVelocity.y;
		}
	
//		System.out.println("dragstart x: "+dragSp.x + " y: "+dragSp.y);
//		System.out.println("dragrelease x: "+dragRp.x + " y: "+dragRp.y);
//		System.out.println("angle: "+normal.angle()+" impulse x: "+impulse.x+ " impulse y: "+impulse.y);
		body.setLinearVelocity(0f, 0f);
		body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
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
	
}
