package com.example.aplicacion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class lwz {
	private String texto;
	private List<Integer> codificacion;
	private List<String> diccionario;
	
	public lwz (String nombreArchivo) {
		codificacion = new ArrayList<Integer>();
		diccionario  = new ArrayList<String>(); 
		try {
			BinaryIn In = new BinaryIn(nombreArchivo);
			texto = In.readString();
		} catch (FileNotFoundException e) {e.printStackTrace();}
		
		
		//hay que meter comas, puntos y demás... Y MAYUSCULAS
	/*	for(char c = 'a'; c <= 'z'; c++ ) {
			diccionario.add(String.valueOf(c));
		}*/
		diccionario.add("a");
		diccionario.add("b");
		diccionario.add("c");
		diccionario.add("d");
		diccionario.add("r");
		
	}
	
	
	public void Comprimir() {
		char[] array = texto.toCharArray();
		String auxiliar;
		int contadorParaElTexto = 0, maximoTamano = 2;
		
		while(contadorParaElTexto+maximoTamano<array.length) {
			auxiliar = take(maximoTamano, contadorParaElTexto, array);
			boolean seguir = true;
			int tamano = maximoTamano;
			//Ya tenemos la siguiente subCadena. Ahora buscaremos si está en el diccionario.
			while(seguir) {
				System.out.println(auxiliar);/////////////
				if(!diccionario.contains(auxiliar)) {
					tamano--;
					diccionario.add(auxiliar);
					auxiliar = auxiliar.substring(0, auxiliar.length()-1);
					
				} else {
					seguir = false;
					
					codificacion.add(diccionario.indexOf(auxiliar)+1);//El +1 es para que nos guarde un numero empezando por 1
					contadorParaElTexto += tamano;
					if(auxiliar.length()>=maximoTamano)
						maximoTamano++;
					
				}
			}
			System.out.println("    Sale del bucle.");/////////////
			
			
			
		}
		
		for(int i = 0; i < codificacion.size(); i++) {
			System.out.println(codificacion.get(i));
		}
		//Hay que hacer un if contadorPar...<array.length-1 por si nos dejamos alguno
	}
	
	/**
	 * Coge i caracteres de un array desde principio
	 * @param i
	 * @return
	 */
	private String take(int i, int principio, char[] array) {
		char[] res = new char[i];
		System.arraycopy(array, principio, res, 0, i);
		return String.copyValueOf(res);
	}
	
	
	
	public static void main(String[] std) {
		lwz l = new lwz("prueba.txt");
		l.Comprimir();
	}
	

}
