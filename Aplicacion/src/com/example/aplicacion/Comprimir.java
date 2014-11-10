package com.example.aplicacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;

public class Comprimir {

	private File archivo;
	private File archivoComprimido;
	
	/**
	 * 
	 * @param std
	 * @param aux  0 para Huffman, 1 para...
	 */
	public Comprimir(String std, int aux) {
		
		switch(aux) {
		case 0 : Huffman(std); break;
		default: throw new InputMismatchException("Opción no soportada");
		}
	}
	
	private void Huffman(String std) {
		Huffman h = new Huffman();
		try {
			
			h.compress(std, std+"-compress");
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public static void main(String[] args) {}

}
