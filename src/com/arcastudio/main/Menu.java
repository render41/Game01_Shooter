package com.arcastudio.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.arcastudio.world.World;

public class Menu {

	public String[] options = { "novo jogo", "carregar jogo", "sair" };
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	public boolean up, down, enter;
	public static boolean pause = false;

	public static boolean saveExist = false;
	public static boolean saveGame = false;

	public void tick() {
		File file = new File("save.txt");

		if (file.exists()) {
			saveExist = true;
		} else {
			saveExist = false;
		}

		if (up) {
			up = false;
			currentOption--;
			if (currentOption < 0) {
				currentOption = maxOption;
			}
		}

		if (down) {
			down = false;
			currentOption++;
			if (currentOption > maxOption) {
				currentOption = 0;
			}
		}

		if (enter) {
			// Sound.music.loop();
			enter = false;
			if (options[currentOption] == "novo jogo" || options[currentOption] == "continuar") {
				Game.gameState = "NORMAL";
				pause = false;
				file = new File("save.txt");
				file.delete();
			}

			else if (options[currentOption] == "carregar jogo") {
				file = new File("save.txt");
				if (file.exists()) {
					// Carrega o encode colocado no Game.java
					String saver = loadGame(10);
					applySave(saver);
				}
			}

			else if (options[currentOption] == "sair") {
				System.exit(1);
			}
		}
	}

	public static void applySave(String str) {
		String[] spl = str.split("/");
		for (int i = 0; i < spl.length; i++) {
			String[] spl2 = spl[i].split(":");
			switch (spl2[0]) {
				case "level":
					World.restartGame("level" + spl2[1] + ".png");
					Game.gameState = "NORMAL";
					pause = false;
					break;
			}
		}
	}

	// Load Game
	public static String loadGame(int encode) {
		String line = "";
		File file = new File("save.txt");
		// Se existir, ele foi salvo
		if (file.exists()) {
			try {
				// Linha individual que inicia vazia
				String singleLine = null;
				// Ler o arquivo
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
				try {
					// Ler linha por linha do arquivo
					while ((singleLine = reader.readLine()) != null) {
						String[] trans = singleLine.split(":");
						char[] val = trans[1].toCharArray();
						trans[1] = "";
						for (int i = 0; i < val.length; i++) {
							val[i] -= encode;
							trans[1] += val[i];
						}
						line += trans[0];
						line += ":";
						line += trans[1];
						line += "/";
					}
				} catch (IOException e) {
				}
			} catch (FileNotFoundException e) {
			}
		}
		// se não existir ele retorna vazio
		return line;
	}

	// Save Game
	// 1: O nome que deseja salvar
	// 2: Valor do primeiro parâmetro
	// 3: criptografia
	public static void saveGame(String[] val1, int[] val2, int encode) {
		BufferedWriter write = null;
		try {
			// Criação de um arquivo para salvar
			write = new BufferedWriter(new FileWriter("save.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Loop para percorrer as strings e contruir o arquivo de save game
		for (int i = 0; i < val1.length; i++) {

			String current = val1[i];
			current += ":";
			char[] value = Integer.toString(val2[i]).toCharArray();
			// Loop para o value
			for (int n = 0; n < value.length; n++) {
				// Sem isso o arquivo não fica criptografado
				value[n] += encode;
				current += value[n];
			}

			try {
				write.write(current);
				if (i < value.length - 1) {
					// Gerar linhas
					write.newLine();
				}
			} catch (IOException e) {
			}
		}
		try {
			// Depois que escrever o arquivo, o sistema fechar
			write.flush();
			write.close();
		} catch (IOException e) {
		}
	}

	public void render(Graphics g) {
		// Background do Menu
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0, 0, 0, 190));
		g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);

		// Título do Menu
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 36));
		g.drawString("> Game 01 <", (Game.WIDTH * Game.SCALE) / 2 - 105, (Game.HEIGHT * Game.SCALE) / 2 - 150);

		// Opções do Menu
		g.setFont(new Font("arial", Font.BOLD, 24));
		if (!pause) {
			g.drawString("Novo Jogo", (Game.WIDTH * Game.SCALE) / 2 - 105, 200);
		} else {
			g.drawString("Continuar Jogo", (Game.WIDTH * Game.SCALE) / 2 - 105, 200);
		}

		g.drawString("Carregar Jogo", (Game.WIDTH * Game.SCALE) / 2 - 105, 250);
		g.drawString("Sair do Jogo", (Game.WIDTH * Game.SCALE) / 2 - 105, 300);

		// Direção do Menu
		if (options[currentOption] == "novo jogo") {
			g.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 150, 200);
		} else if (options[currentOption] == "carregar jogo") {
			g.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 150, 250);
		} else if (options[currentOption] == "sair") {
			g.drawString(">", (Game.WIDTH * Game.SCALE) / 2 - 150, 300);
		}
	}
}
