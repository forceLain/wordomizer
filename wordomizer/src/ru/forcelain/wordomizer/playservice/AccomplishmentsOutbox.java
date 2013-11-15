package ru.forcelain.wordomizer.playservice;

import ru.forcelain.wordomizer2.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AccomplishmentsOutbox {
	
	public static final String TAG = AccomplishmentsOutbox.class.getSimpleName();

	public boolean g10Achievement = false;
	public boolean g100Achievement = false;
	public boolean g1000Achievement = false;
    public int score = -1;
	public boolean in10sequence;
	public boolean allWords;   
	public boolean doubleWord;   

    public void saveLocal(Context ctx) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Editor editor = pref.edit();
        editor.putBoolean(TAG +"."+ ctx.getString(R.string.achievement_10), g10Achievement);
        editor.putBoolean(TAG +"."+ ctx.getString(R.string.achievement_100), g100Achievement);
        editor.putBoolean(TAG +"."+ ctx.getString(R.string.achievement_1000), g1000Achievement);
        editor.putBoolean(TAG +"."+ ctx.getString(R.string.achievement_all_words), allWords);
        editor.putBoolean(TAG +"."+ ctx.getString(R.string.achievement_10_in_sequence), in10sequence);
        editor.putBoolean(TAG +"."+ ctx.getString(R.string.achievement_double), doubleWord);
        editor.putInt(TAG+".score", score);
        editor.commit();
    }

    public void loadLocal(Context ctx) {
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    	g10Achievement = pref.getBoolean(TAG +"."+ ctx.getString(R.string.achievement_10), false);
    	g100Achievement = pref.getBoolean(TAG +"."+ ctx.getString(R.string.achievement_100), false);
    	g1000Achievement = pref.getBoolean(TAG +"."+ ctx.getString(R.string.achievement_1000), false);
    	allWords = pref.getBoolean(TAG +"."+ ctx.getString(R.string.achievement_all_words), false);
    	in10sequence = pref.getBoolean(TAG +"."+ ctx.getString(R.string.achievement_10_in_sequence), false);
    	doubleWord = pref.getBoolean(TAG +"."+ ctx.getString(R.string.achievement_double), false);
    	score = pref.getInt(TAG+".score", -1);
    }

}
