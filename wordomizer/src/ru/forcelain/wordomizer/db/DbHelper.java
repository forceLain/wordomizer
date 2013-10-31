package ru.forcelain.wordomizer.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.forcelain.wordomizer.model.Word;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String TAG = DbHelper.class.getSimpleName();

	public static final String DATABASE_NAME = "WordomizerData";
	private static final int DATABASE_VERSION = 200;

	private static final String TABLE_WORDS = "words";
	private static final String WORDS_ID = "_id";
	private static final String WORDS_WORD = "word";
	private static final String WORDS_GUESSED = "guessed";
	private static final String WORDS_VIEWED = "viewed";

	private static int cachedWordsCount;
	
	private Context ctx;

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String Q = "CREATE TABLE IF NOT EXISTS " + TABLE_WORDS + "("
				+ WORDS_ID + " integer primary key autoincrement, "
				+ WORDS_WORD + " TEXT UNIQUE, "
				+ WORDS_GUESSED + " INTEGER, "
				+ WORDS_VIEWED + " INTEGER);";
		db.execSQL(Q);

		insertWords(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_WORDS);
		onCreate(db);
	}

	private void insertWords(SQLiteDatabase db) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					ctx.getAssets().open("words.txt"), "UTF-8"));
			String line = reader.readLine();
			while (line != null) {
				final ContentValues values = new ContentValues();
				values.put(WORDS_WORD, line);
				values.put(WORDS_GUESSED, 0);
				values.put(WORDS_VIEWED, 0);
				db.replace(TABLE_WORDS, null, values);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	public Word getWord(int id){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(TABLE_WORDS, null, WORDS_ID+"="+id, null, null, null, null, "1");
		cursor.moveToFirst();
		Word word = cursorToWord(cursor);
		cursor.close();
		db.close();
		return word;
	}
	
	public Word getWord(String wordToFind) {
		Word word = null;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(TABLE_WORDS, null, WORDS_WORD+"=?", new String[]{wordToFind}, null, null, null, "1");
		if (cursor.moveToFirst()){
			word = cursorToWord(cursor);
		}
		cursor.close();
		db.close();
		return word;
	}
	
	public void updateWord(Word word) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = wordToValues(word);
		db.replace(TABLE_WORDS, null, values);
		db.close();
	}
	
	public int getWordsCount(){
		if (cachedWordsCount == 0){
			int count = 0;
			SQLiteDatabase db = getWritableDatabase();
			Cursor cursor = db.rawQuery("select count(*) from "+TABLE_WORDS, null);
			if (cursor.moveToFirst()){
				count = cursor.getInt(0);
			}
			cursor.close();
			db.close();
			cachedWordsCount = count;
		}
		
		return cachedWordsCount;
	}
	
	public int getUnguessedWordsCount(){
		int count = 0;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "+TABLE_WORDS+" where "+WORDS_GUESSED+"= 0", null);
		if (cursor.moveToFirst()){
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
	public int getGuessedWordsCount(){
		int count = 0;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "+TABLE_WORDS+" where "+WORDS_GUESSED+"= 1", null);
		if (cursor.moveToFirst()){
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
	public int getViewedWordsCount(){
		int count = 0;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "+TABLE_WORDS+" where "+WORDS_VIEWED+"= 1", null);
		if (cursor.moveToFirst()){
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
	private Word cursorToWord(Cursor cursor){
		Word word = new Word();
		word.id = findInt(cursor, WORDS_ID);
		word.word = findString(cursor, WORDS_WORD);
		word.guessed = findBool(cursor, WORDS_GUESSED);
		word.viewed = findBool(cursor, WORDS_VIEWED);
		return word;
	}
	
	private ContentValues wordToValues(Word word){
		ContentValues values = new ContentValues();
		values.put(WORDS_WORD, word.word);
		values.put(WORDS_GUESSED, word.guessed);
		values.put(WORDS_ID, word.id);
		values.put(WORDS_VIEWED, word.viewed);
		return values;
	}
	
	private static String findString(Cursor cursor, String tableName){
		return cursor.getString(cursor.getColumnIndex(tableName));
	}
	
	private static boolean findBool(Cursor cursor, String tableName) {
		return (cursor.getInt(cursor.getColumnIndex(tableName)) > 0);
	}
	
	private static int findInt(Cursor cursor, String tableName) {
		return cursor.getInt(cursor.getColumnIndex(tableName));
	}
}
