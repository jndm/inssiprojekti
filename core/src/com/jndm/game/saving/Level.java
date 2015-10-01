package com.jndm.game.saving;

public class Level {
	private String name;
	private Boolean passed;
	private String pb;
	
	public Level() {}
	
	public Level(String name, String pb, Boolean available) {
		this.pb = pb;
		this.passed = available;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isPassed() {
		return passed;
	}

	public void setPassed(Boolean available) {
		this.passed = available;
	}

	public String getPb() {
		return pb;
	}

	public void setPb(String pb) {
		this.pb = pb;
	}
}
