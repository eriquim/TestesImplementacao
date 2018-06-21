package br.tjrn.testes.arvore;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class No {
	
	No pai;
	
	String nome;
	
	public No(String nome) {
		this.nome = nome;
	}
	
}
