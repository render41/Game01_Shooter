package com.arcastudio.entities;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.arcastudio.main.Game;
import com.arcastudio.world.AStar;
import com.arcastudio.world.Camera;
import com.arcastudio.world.Vector2i;
import com.arcastudio.world.World;

public class Enemy extends Entity {

	private int speed = 1;

	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 1;
	private BufferedImage[] sprites;
	private int life = 3;
	private boolean isDamage = false;
	private int damageFrames = 10, damageCurrent = 0;

	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);

		// Frames de Animação
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112, 16, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(112 + 16, 16, 16, 16);
	}

	public void tick() {
		/*
		 * if (this.calculateDistance(this.getX(), this.getY(), Game.player.getX(),
		 * Game.player.getY()) < 70) { if (this.isColliddingWithPlayer() == false) {
		 * 
		 * if (x < Game.player.getX() && World.isFree(x + speed, this.getY()) &&
		 * !isCollidding(x + speed, this.getY())) { x += speed; } else if (x >
		 * Game.player.getX() && World.isFree(x - speed, this.getY()) && !isCollidding(x
		 * - speed, this.getY())) { x -= speed; }
		 * 
		 * if (y < Game.player.getY() && World.isFree(this.getX(), y + speed) &&
		 * !isCollidding(this.getX(), y + speed)) { y += speed; } else if (y >
		 * Game.player.getY() && World.isFree(this.getX(), y - speed) &&
		 * !isCollidding(this.getX(), y - speed)) { y -= speed; } } else { // Estamos
		 * colidindo if (Game.random.nextInt(100) < 10) { Game.player.life -=
		 * Game.random.nextInt(3); Game.player.isDamage = true; } } }
		 */
		// Mascaras de colisão
		maskx = 6;
		masky = 6;
		mwidth = 8;
		mheight = 8;
		// Camada
		depth = 0;
		if (!isColliddingWithPlayer()) {
			if (path == null || path.size() == 0) {
				Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
				Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));
				path = AStar.findPath(Game.world, start, end);
			}
		} else {
			if (new Random().nextInt(100) < 5) {
				Game.player.life -= Game.random.nextInt(3);
				Game.player.isDamage = true;
			}
		}

		if (new Random().nextInt(100) < 50) {
			followPath(path);
		}
		if (x % 16 == 0 && y % 16 == 0) {
			if (new Random().nextInt(100) < 5) {
				if (path == null || path.size() == 0) {
					Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
					Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y / 16));
					path = AStar.findPath(Game.world, start, end);
				}
			}
		}

		// Animation
		frames++;
		if (frames == maxFrames) {
			frames = 0;
			index++;
			if (index > maxIndex) {
				index = 0;
			}
		}

		collidingBullet();
		if (life <= 0) {
			destroySelf();
			return;
		}

		if (isDamage) {
			this.damageCurrent++;
			if (this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamage = false;
			}
		}
	}

	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}

	public void collidingBullet() {
		for (int i = 0; i < Game.bulletShoot.size(); i++) {
			Entity e = Game.bulletShoot.get(i);
			if (e instanceof BulletShoot) {
				if (Entity.isCollidding(this, e)) {
					isDamage = true;
					life--;
					Game.bulletShoot.remove(i);
					return;
				}
			}
		}
	}

	public boolean isColliddingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, mwidth, mheight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);

		return enemyCurrent.intersects(player);
	}

	/*
	 * public boolean isCollidding(int xnext, int ynext) { Rectangle enemyCurrent =
	 * new Rectangle(xnext + maskX, ynext + maskY, maskW, maskH); // Classe que cria
	 * // retangulos fictícios para // testar colisões. for (int i = 0; i <
	 * Game.enemies.size(); i++) { Enemy e = Game.enemies.get(i);
	 * 
	 * if (e == this) { continue; } Rectangle targetEnemy = new Rectangle(e.getX() +
	 * maskX, e.getY() + maskY, maskW, maskH); if
	 * (enemyCurrent.intersects(targetEnemy)) { return true; }
	 * 
	 * }
	 * 
	 * return false; }
	 */

	public void render(Graphics g) {
		if (!isDamage) {
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else {
			g.drawImage(Entity.ENEMY_FEEDBACK, this.getX() - Camera.x, this.getY() - Camera.y, null);
		}

	}
}
