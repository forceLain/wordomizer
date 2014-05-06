package ru.forcelain.wordomizer2.utils;

public class Utils {
	
	private Utils(){
		
	}
	
	public static boolean contains(int[] array, int value){
		for (int i = 0; i < array.length; i++){
			if (array[i] == value){
				return true;
			}
		}
		return false;
	}
}
