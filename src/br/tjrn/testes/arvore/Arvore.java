package br.tjrn.testes.arvore;

import java.util.List;

import org.primefaces.model.DefaultTreeNode;

public class Arvore {
	
	List<No> nos;
	
	org.primefaces.model.TreeNode root;
	
	public Arvore() {
		No n1 = new No("Raiz");
		No n2 = new No("Filho 1");
		No n3 = new No("Filho 2");
		No n4 = new No("Filho 3");
		No n5 = new No("Filho 4");
		No n6 = new No("Filho 5");
		No n7 = new No("Filho 6");
		No n8 = new No("Filho 7");
		No n9 = new No("Filho 8");
		No n10 = new No("Filho 9");
		No n11 = new No("Filho 10");
		No n12 = new No("Filho 11");
		No n13 = new No("Filho 12");
		No n14 = new No("Filho 13");
		No n15 = new No("Filho 14");
		
		n1.setPai(null);
		n2.setPai(n1);
		n3.setPai(n2);
		n4.setPai(n2);
		n5.setPai(n3);
		n6.setPai(n3);
		n7.setPai(n3);
		n8.setPai(n15);
		n9.setPai(n4);
		n10.setPai(n5);
		n11.setPai(n7);
		n12.setPai(n9);
		n13.setPai(n8);
		n14.setPai(n10);
		n15.setPai(n12);
		
	}
	
	public void montarArvore() {
		root = new DefaultTreeNode();
		
		
		
	}
	

}
