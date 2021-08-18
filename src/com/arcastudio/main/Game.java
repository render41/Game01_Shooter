package com.arcastudio.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

//Importação dos Packages
import com.arcastudio.entities.BulletShoot;
import com.arcastudio.entities.Enemy;
import com.arcastudio.entities.Entity;
import com.arcastudio.entities.Player;
import com.arcastudio.graficos.Spritesheet;
import com.arcastudio.graficos.UI;
import com.arcastudio.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;
	// Variables
	// Janela e Run Game
	public static JFrame frame;
	private boolean isRunning = true;
	private Thread thread;
	public static final int WIDTH = 240, HEIGHT = 160, SCALE = 3;

	// Imagens e Gráficos
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	private BufferedImage image;
	private Graphics g;

	// Entities
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bulletShoot;
	public static Spritesheet spritesheet;
	public static World world;
	public static Player player;
	public static Random random;

	// User Interface
	public UI ui;

	// Game State
	public static String gameState = "MENU";
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	private boolean restartGame = false;

	// Menu
	public Menu menu;

	/***/

	public boolean saveGame = false;

	// Font
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("Silver.ttf");
	public Font newFont;

	// Mouse position
	public int mx, my;

	// Manipular Pixels
	public int[] pixels;

	// Efeitos de Luz
	public BufferedImage lightmap;
	public int[] lightMapPixels;

	// Minimapa
	public static BufferedImage minimap;
	public static int[] minimapPixels;

	// Construtor
	public Game() {
		random = new Random();
		// Para que os eventos de teclado e mouse funcionem
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		// this.setPreferredSize(new
		// Dimension(Toolkit.getDefaultToolkit().getScreenSize()));
		this.setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		initFrame();
		// Inicializando Objetos
		ui = new UI();
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		// Configurar luz
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		lightMapPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightMapPixels, 0, lightmap.getWidth());

		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bulletShoot = new ArrayList<BulletShoot>();

		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		world = new World("/level1.png");

		minimap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapPixels = ((DataBufferInt) minimap.getRaster().getDataBuffer()).getData();

		/*
		 * // Usando a font try { newFont = Font.createFont(Font.TRUETYPE_FONT,
		 * stream).deriveFont(16f); } catch (FontFormatException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 */
		menu = new Menu();
	}

	// Criação da Janela
	public void initFrame() {
		frame = new JFrame("New Window");
		frame.add(this);
		// frame.setUndecorated(true);
		frame.setResizable(false);// Usuário não irá ajustar janela
		frame.pack();
		frame.setLocationRelativeTo(null);// Janela inicializa no centro
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Fechar o programa por completo
		frame.setVisible(true);// Dizer que estará visível
	}

	// Threads
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}

	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Método Principal
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}

	// Ticks do Jogo
	public void tick() {
		if (gameState == "NORMAL") {
			// xx++;
			// yy++;
			if (this.saveGame) {
				this.saveGame = false;
				// Construindo o que será salvo
				String[] opt1 = { "level" };
				int[] opt2 = { this.CUR_LEVEL };
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo Salvo");
			}
			this.restartGame = false;
			for (int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}

			for (int i = 0; i < bulletShoot.size(); i++) {
				bulletShoot.get(i).tick();
			}

			if (enemies.size() <= 0) {
				// System.out.println("Next Level");
				CUR_LEVEL++;
				if (CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "GAME_OVER") {
			framesGameOver++;
			if (framesGameOver == 35) {
				framesGameOver = 0;
				if (showMessageGameOver) {
					showMessageGameOver = false;
				} else {
					showMessageGameOver = true;
				}
			}

			if (restartGame) {
				restartGame = false;
				gameState = "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level" + CUR_LEVEL + ".png";
				World.restartGame(newWorld);
			}
		} else if (gameState == "MENU") {
			this.menu.tick();
		}
	}

	/*
	 * public void drawRectangleExample(int xoff, int yoff) { for (int xx = 0; xx <
	 * 32; xx++) { for (int yy = 0; yy < 32; yy++) { int xOff = xx + xoff; int yOff
	 * = yy + yoff; if (xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT) {
	 * continue; } pixels[xOff + (yOff * WIDTH)] = 0xFF0000; } } }
	 * 
	 * public void applyLight() { for (int xx = 0; xx < Game.WIDTH; xx++) { for (int
	 * yy = 0; yy < Game.HEIGHT; yy++) { if(lightMapPixels[xx + (yy * Game.WIDTH)]
	 * == 0xFFFFFFFF) { pixels[xx + (yy * Game.WIDTH)] = 0; } } } }
	 */

	// O que será mostrado em tela
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();// Sequência de buffer para otimizar a renderização, lidando com
														// performace gráfica
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}

		g = image.getGraphics();// Renderizar imagens na tela
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);

		/* Render do jogo */
		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for (int i = 0; i < bulletShoot.size(); i++) {
			bulletShoot.get(i).render(g);
		}

		ui.render(g);
		/***/
		// applyLight();
		g.dispose();// Limpar dados de imagem não usados
		g = bs.getDrawGraphics();
		// g.drawImage(image, 0, 0,
		// Toolkit.getDefaultToolkit().getScreenSize().width,Toolkit.getDefaultToolkit().getScreenSize().height,
		// null);
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.setFont(new Font("arial", Font.BOLD, 17));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo, 600, 40);
		g.setFont(newFont);
		// Game Over Configs
		if (gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0, 0, 0, 100));
			g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
			g.setFont(new Font("arial", Font.BOLD, 36));
			g.setColor(Color.white);
			g.drawString("Game Over", (WIDTH * SCALE) / 2 - 100, (HEIGHT * SCALE) / 2 - 40);
			if (showMessageGameOver) {
				g.setFont(new Font("arial", Font.BOLD, 20));
				g.drawString("> Press Enter to Restart the Game <", (WIDTH * SCALE) / 2 - 170, (HEIGHT * SCALE) / 2);
			}

		} else if (gameState == "MENU") {
			menu.render(g);
		}
		// Graphics2D g2 = (Graphics2D) g;
		// double angleMouse = Math.atan2(200 + 25 - my, 200 + 25-mx);
		// g2.rotate(angleMouse, 200 + 25, 200 + 25);
		// g.setColor(Color.red);
		// g.fillRect(200, 200, 50, 50);

		World.renderMiniMap();
		g.drawImage(minimap, 600, 350, World.WIDTH * 3, World.HEIGHT * 3, null);

		bs.show();
	}

	public void applyLight() {
		for (int xx = 0; xx < WIDTH; xx++) {
			for (int yy = 0; yy < HEIGHT; yy++) {
				if (lightMapPixels[xx + yy * WIDTH] == 0xFF000000) {
					int pixel = Pixel.getLightBlend(pixels[xx + yy * WIDTH], 0x808080, 0);
					pixels[xx + yy * WIDTH] = pixel;
				}
			}
		}
	}

	// Controle de FPS
	public void run() {
		// Variables
		long lastTime = System.nanoTime();// Usa o tempo atual do computador em nano segundos, bem mais preciso
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;// Calculo exato de Ticks
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		// Ruuner Game
		while (isRunning == true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;

			if (delta >= 1) {
				tick();
				render();
				frames++;
				delta--;
			}

			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("FPS: " + frames);
				frames = 0;
				timer += 1000;
			}
		}

		stop(); // Garante que todas as Threads relacionadas ao computador foram terminadas,
				// para garantir performance.

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		// Esquerda e Direita
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}

		// Cima e Baixo
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			if (gameState == "MENU") {
				this.menu.up = true;
			}

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			if (gameState == "MENU") {
				this.menu.down = true;
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}

		if (e.getKeyCode() == KeyEvent.VK_G) {
			if (gameState == "NORMAL") {
				this.saveGame = true;
			}
		}

	}

	public void keyReleased(KeyEvent e) {
		// Esquerda e Direita
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;

		} else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}

		// Cima e Baixo
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;

		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if (gameState == "MENU") {
				this.menu.enter = true;
			}
		}

	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / 3);
		player.my = (e.getY() / 3);
	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

}
