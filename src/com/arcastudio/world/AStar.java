package com.arcastudio.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AStar {
	public static double lastTime = System.currentTimeMillis();
	// M�todo com base de compara��es para comparar 2 objetos e usar em alguma
	// posi��o na lista do Arraylist

	// Node: classe criada por n�s que vai guardar posi��es e informa��es do
	// algoritmo
	private static Comparator<Node> nodeSorter = new Comparator<Node>() {
		@Override
		// Dois objetos que ser�o comparados quando rodar o algoritmo
		public int compare(Node n0, Node n1) {
			if (n1.fCost < n0.fCost) {
				return +1;
			}
			if (n1.fCost > n0.fCost) {
				return -1;
			}

			return 0;
		}
	};

	// Otimiza o algoritmo quando funcionar, depois que passar alguns segundos
	// retornar, assim ajustando o calculo de caminhos.
	public boolean clear() {
		if (System.currentTimeMillis() - lastTime >= 1000) {
			return true;
		}
		return false;
	}

	// world: vai pegar os tiles
	// start: ir� pegar a posi��o inicial do inimigo
	// end: � onde o inimigo ir� chegar
	public static List<Node> findPath(World world, Vector2i start, Vector2i end) {
		lastTime = System.currentTimeMillis();
		// openList: Posi��es poss�veis que o inimigo vai percorrer
		List<Node> openList = new ArrayList<Node>();
		// closedList: Posi��es n�o validas que o inimigo n�o vai percorrer
		List<Node> closedList = new ArrayList<Node>();

		Node current = new Node(start, null, 0, getDistance(start, end));
		openList.add(current);
		// Enquanto for maior, ainda tem chances de encontrar o caminho
		while (openList.size() > 0) {
			Collections.sort(openList, nodeSorter);
			// Quando organizar pega o primeiro item da lista
			current = openList.get(0);
			// Quando chega no final
			if (current.tile.equals(end)) {
				List<Node> path = new ArrayList<Node>();
				while (current.parent != null) {
					path.add(current);
					current = current.parent;
				}
				openList.clear();
				closedList.clear();
				return path;
			}
			openList.remove(current);
			closedList.add(current);

			// Detectar o que � espa�o livre ou n�o
			for (int i = 0; i < 9; i++) {
				if (i == 4) {
					continue;
				}
				int x = current.tile.x;
				int y = current.tile.y;
				int xi = (i % 3) - 1;
				int yi = (i / 3) - 1;
				Tile tile = World.tiles[x + xi + ((y + yi) * World.WIDTH)];
				if (tile == null) {
					continue;
				}
				if (tile instanceof WallTile) {
					continue;
				}
				if (i == 0) {
					Tile test = World.tiles[x + xi + 1 + ((y + yi) * World.WIDTH)];
					Tile test2 = World.tiles[x + xi + ((y + yi + 1) * World.WIDTH)];
					if (test instanceof WallTile || test2 instanceof WallTile) {
						continue;
					}
				} else if (i == 2) {
					Tile test = World.tiles[x + xi - 1 + ((y + yi) * World.WIDTH)];
					Tile test2 = World.tiles[x + xi + ((y + yi + 1) * World.WIDTH)];
					if (test instanceof WallTile || test2 instanceof WallTile) {
						continue;
					}
				} else if (i == 6) {
					Tile test = World.tiles[x + xi + ((y + yi - 1) * World.WIDTH)];
					Tile test2 = World.tiles[x + xi + 1 + ((y + yi) * World.WIDTH)];
					if (test instanceof WallTile || test2 instanceof WallTile) {
						continue;
					}
				} else if (i == 8) {
					Tile test = World.tiles[x + xi + ((y + yi - 1) * World.WIDTH)];
					Tile test2 = World.tiles[x + xi - 1 + ((y + yi) * World.WIDTH)];
					if (test instanceof WallTile || test2 instanceof WallTile) {
						continue;
					}
				}

				Vector2i a = new Vector2i(x + xi, y + yi);
				double gCost = current.gCost + getDistance(current.tile, a);
				double hCost = getDistance(a, end);

				Node node = new Node(a, current, gCost, hCost);

				if (vecInList(closedList, a) && gCost >= current.gCost) {
					continue;
				}

				if (!vecInList(openList, a)) {
					openList.add(node);
				} else if (gCost < current.gCost) {
					openList.remove(current);
					openList.add(node);
				}

			}
		}
		closedList.clear();
		return null;
	}

	// Checar se o Node est� em lista
	private static boolean vecInList(List<Node> list, Vector2i vector) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).tile.equals(vector)) {
				return true;
			}
		}
		return false;
	}

	// O primeiro � posi��o inicial e o ultimo � onde quer chegar
	private static double getDistance(Vector2i tile, Vector2i goal) {
		double dx = tile.x - goal.x;
		double dy = tile.y - goal.y;

		return Math.sqrt(dx * dx + dy * dy);
	}
}
