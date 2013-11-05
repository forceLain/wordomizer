package ru.forcelain.wordomizer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Word {
	public int id;
	public String word;
	public String hint;
	public boolean guessed;
	public boolean viewed;
	
	public String getRandomizedWord(){
		int l = word.length();
		String[] realWordArray = new String[l];
		for (int i=0; i<l; i++){
			realWordArray[i] = Character.toString(word.charAt(i));			
		}
		ArrayList<String> mixedWordArrayList = new ArrayList<String>(Arrays.asList(realWordArray));
		String result;
		do {
			Collections.shuffle(mixedWordArrayList);
			StringBuilder sb = new StringBuilder();
			for (String str : mixedWordArrayList){
				sb.append(str);			
			}
			result = sb.toString();
			
		} while (result.equals(word));
		
		return result;
	}
}
