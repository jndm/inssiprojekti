package com.jndm.game.resources;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.jndm.game.utils.Constants;

public class MovingPlatform {
	
	private float x, y, w, h;
	private float minX, maxX, minY, maxY;
	private float speedX, speedY;
	private Body body;
	private Array<Sprite> sprites;
	private int spritesToDraw = 0;
	
	public MovingPlatform(TiledMapTileSet tiles, Body body, float x, float y, float w, float h, 
			float speedX, float speedY, float minX, float maxX, float minY, float maxY) {
		
		this.body = body;
		
		// These values should be already scaled down by PPM
		this.speedX = speedX;
		this.speedY = speedY;
		this.minX = minX; 
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		
		// Determine how many sprites need to be drawn on this object
		spritesToDraw = (int) (w / Constants.PPM);
		
		// Scale values by PPM
		this.x = x / Constants.PPM;
		this.y = y / Constants.PPM; 
		this.w = w / Constants.PPM;
		this.h = h / Constants.PPM;
		
		createSprites(tiles);
	}
	
	private void createSprites(TiledMapTileSet tiles) {
		sprites = new Array<Sprite>();
		
		// Get all tiles that can be used for platform tile
		Array<TextureRegion> tileTextures = new Array<TextureRegion>();
		Iterator<TiledMapTile> tileIterator = tiles.iterator();
		while(tileIterator.hasNext()) {
			TiledMapTile tile = tileIterator.next();
			tileTextures.add(tile.getTextureRegion());
		}
		
		for(int i = 0; i < spritesToDraw; i++) {
			Sprite sprite = new Sprite(tileTextures.random());
			sprite.setBounds(x + (i * sprite.getRegionWidth() / Constants.PPM), 
					y, 
					sprite.getRegionWidth() / Constants.PPM, 
					sprite.getRegionHeight() / Constants.PPM);
			
			sprites.add(sprite);
		}
	}

	public void render(SpriteBatch sb) {
		for(Sprite sprite : sprites) {
			sprite.draw(sb);
		}
	}

	public void update(float delta) {
		move(delta);
	}

	private void move(float delta) {
		
		if(body.getPosition().x < minX) {
			speedX = -speedX;
		} else if(body.getPosition().x + w > maxX) {
			speedX = -speedX;
		}
		
		if(body.getPosition().y < minY) {
			speedY = -speedY;
		} else if(body.getPosition().y + h > maxY) {
			speedY = -speedY;
		}
		
		body.setLinearVelocity(speedX, speedY);
		
		// Move sprites
		for(int i = 0; i < sprites.size; i++) {
			Sprite s = sprites.get(i);
			s.setPosition(body.getPosition().x + (i *  s.getRegionWidth() / Constants.PPM), 
					body.getPosition().y);
		}
			
	}
}
