package com.jndm.game.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.jndm.game.resources.Player;

public class MyContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		
		// Ground sensor
		if(fa.getUserData() != null && fa.getUserData().equals("groundSensor")) {
			Player player = (Player) fa.getBody().getUserData();
			player.setOnGround(true);
		} else if(fb.getUserData() != null && fb.getUserData().equals("groundSensor")) {
			Player player = (Player) fb.getBody().getUserData();
			player.setOnGround(true);
		}
		
		// Right wall sensor
		if(fa.getUserData() != null && fa.getUserData().equals("rightSensor")) {
			Player player = (Player) fa.getBody().getUserData();
			player.setOnRightWall(true);
		} else if(fb.getUserData() != null && fb.getUserData().equals("rightSensor")) {
			Player player = (Player) fb.getBody().getUserData();
			player.setOnRightWall(true);
		}
		
		// Left wall sensor
		if(fa.getUserData() != null && fa.getUserData().equals("leftSensor")) {
			Player player = (Player) fa.getBody().getUserData();
			player.setOnLeftWall(true);
		} else if(fb.getUserData() != null && fb.getUserData().equals("leftSensor")) {
			Player player = (Player) fb.getBody().getUserData();
			player.setOnLeftWall(true);
		}
		
	}

	@Override
	public void endContact(Contact contact) {	
		
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();
		
		// Ground sensor
		if(fa.getUserData() != null && fa.getUserData().equals("groundSensor")) {
			Player player = (Player) fa.getBody().getUserData();
			player.setOnGround(false);
		} else if(fb.getUserData() != null && fb.getUserData().equals("groundSensor")) {
			Player player = (Player) fb.getBody().getUserData();
			player.setOnGround(false);
		}
		
		// Right wall sensor
		if(fa.getUserData() != null && fa.getUserData().equals("rightSensor")) {
			Player player = (Player) fa.getBody().getUserData();
			player.setOnRightWall(false);
		} else if(fb.getUserData() != null && fb.getUserData().equals("rightSensor")) {
			Player player = (Player) fb.getBody().getUserData();
			player.setOnRightWall(false);
		}
		
		// Left wall sensor
		if(fa.getUserData() != null && fa.getUserData().equals("leftSensor")) {
			Player player = (Player) fa.getBody().getUserData();
			player.setOnLeftWall(false);
		} else if(fb.getUserData() != null && fb.getUserData().equals("leftSensor")) {
			Player player = (Player) fb.getBody().getUserData();
			player.setOnLeftWall(false);
		}
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
