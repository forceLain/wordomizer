package ru.forcelain.wordomizer2.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PrefUtils {

	private static final String RUS_WORDS_ENABLED = "rus_words_enabled";
	private static final String ENG_WORDS_ENABLED = "eng_words_enabled";
	private static final String SHOW_PATCH_NOTES = "show_patch_notes";
	private static final String SHOW_TUTORIAL = "show_tutorial";
	private static final String SHOW_AD = "pref_show_ad";

	public static boolean isRusWordsEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(RUS_WORDS_ENABLED, true);
	}

	public static boolean isEngWordsEnabled(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(ENG_WORDS_ENABLED, true);
	}

	public static void setEngWordsEnabled(boolean enabled, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(ENG_WORDS_ENABLED, enabled);
		editor.commit();
	}
	
	public static void setRusWordsEnabled(boolean enabled, Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(RUS_WORDS_ENABLED, enabled);
		editor.commit();
	}
	
	public static boolean showPatchNotes(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(SHOW_PATCH_NOTES, true);
	}
	
	public static void setShowPatchNotes(boolean showPatchNotes, Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(SHOW_PATCH_NOTES, showPatchNotes);
		editor.commit();
	}
	
	public static boolean showTutorial(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(SHOW_TUTORIAL, true);
	}
	
	public static void setShowTutorial(boolean showPatchNotes, Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(SHOW_TUTORIAL, showPatchNotes);
		editor.commit();
	}
	
	public static boolean showAd(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(SHOW_AD, true);
	}

}
