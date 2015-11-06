package com.jndm.game.saving;

public class Level {
	private String name;
	private int number;
	private Boolean passed;
	private String pb;
	private Boolean available;
	
	public Level() {}
	
	public Level(String name, int number, String pb, Boolean passed, Boolean available) {
		this.pb = pb;
		this.passed = passed;
		this.name = name;
		this.number = number;
		this.setAvailable(available);
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Boolean isAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}
}
