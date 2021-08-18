package com.arcastudio.world;

public class Node {
	// Guardar posi��es para ter mais precis�o
	public Vector2i tile;
	// Cada node que for instanciar, tem um parente do pr�prio Node.
	public Node parent;
	public double fCost, gCost, hCost;
	
	// Retornar posi��es que o inimigo percorre at� o jogador
	public Node(Vector2i tile, Node parent, double gCost, double hCost) {
		this.tile = tile;
		this.parent = parent;
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = gCost + hCost;
	}
}
