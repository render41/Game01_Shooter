package com.arcastudio.world;

public class Node {
	// Guardar posições para ter mais precisão
	public Vector2i tile;
	// Cada node que for instanciar, tem um parente do próprio Node.
	public Node parent;
	public double fCost, gCost, hCost;
	
	// Retornar posições que o inimigo percorre até o jogador
	public Node(Vector2i tile, Node parent, double gCost, double hCost) {
		this.tile = tile;
		this.parent = parent;
		this.gCost = gCost;
		this.hCost = hCost;
		this.fCost = gCost + hCost;
	}
}
