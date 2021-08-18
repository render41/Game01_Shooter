package com.arcastudio.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.arcastudio.main.Game;
import com.arcastudio.world.Camera;

public class BulletShoot extends Entity {

	private double dx;
	private double dy;
	private double spd = 4;

	private int life = 40, currentLife = 0;

	public BulletShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick() {
		x += dx * spd;
		y += dy * spd;
		currentLife++;
		if (currentLife == life) {
			Game.bulletShoot.remove(this);
			return;
		}
	}

	public void render(Graphics g) {
		g.setColor(Color.yellow);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
}
