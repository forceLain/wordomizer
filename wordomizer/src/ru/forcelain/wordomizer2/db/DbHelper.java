package ru.forcelain.wordomizer2.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.forcelain.wordomizer2.model.Word;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	private static final String FILE_NAME = "defs.txt";
	private static final String FILE_NAME_ENG = "defs_eng.txt";

	private static final String TAG = DbHelper.class.getSimpleName();

	public static final String DATABASE_NAME = "WordomizerData";
	private static final int DATABASE_VERSION = 201;

	public static final String TABLE_WORDS = "words";
	public static final String TABLE_WORDS_ENG = "words_eng";
	private static final String WORDS_ID = "_id";
	private static final String WORDS_WORD = "word";
	private static final String WORDS_HINT = "hint";
	private static final String WORDS_GUESSED = "guessed";
	private static final String WORDS_VIEWED = "viewed";

	private static int cachedWordsCount;
	private static int cachedEngWordsCount;
	
	private Context ctx;

	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String Q = "CREATE TABLE IF NOT EXISTS " + TABLE_WORDS + "("
				+ WORDS_ID + " integer primary key autoincrement, "
				+ WORDS_WORD + " TEXT UNIQUE, "
				+ WORDS_HINT + " TEXT, "
				+ WORDS_GUESSED + " INTEGER, "
				+ WORDS_VIEWED + " INTEGER);";
		db.execSQL(Q);

		insertWords(db, FILE_NAME, TABLE_WORDS);
		
		Q = "CREATE TABLE IF NOT EXISTS " + TABLE_WORDS_ENG + "("
				+ WORDS_ID + " integer primary key autoincrement, "
				+ WORDS_WORD + " TEXT UNIQUE, "
				+ WORDS_HINT + " TEXT, "
				+ WORDS_GUESSED + " INTEGER, "
				+ WORDS_VIEWED + " INTEGER);";
		db.execSQL(Q);

		insertWords(db, FILE_NAME_ENG, TABLE_WORDS_ENG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion == 201){
			String Q = "CREATE TABLE IF NOT EXISTS " + TABLE_WORDS_ENG + "("
					+ WORDS_ID + " integer primary key autoincrement, "
					+ WORDS_WORD + " TEXT UNIQUE, "
					+ WORDS_HINT + " TEXT, "
					+ WORDS_GUESSED + " INTEGER, "
					+ WORDS_VIEWED + " INTEGER);";
			db.execSQL(Q);

			insertWords(db, FILE_NAME_ENG, TABLE_WORDS_ENG);
		}
	}

	private void insertWords(SQLiteDatabase db, String fileName, String tableName) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					ctx.getAssets().open(fileName), "UTF-8"));
			String line = reader.readLine();
			db.beginTransaction();
			while (line != null) {
				String[] pair = line.split("\\|");
				final ContentValues values = new ContentValues();
				values.put(WORDS_WORD, pair[0]);
				values.put(WORDS_HINT, pair[1]);
				values.put(WORDS_GUESSED, 0);
				values.put(WORDS_VIEWED, 0);
				db.replace(tableName, null, values);
				line = reader.readLine();
			}
			reader.close();
			db.setTransactionSuccessful();
			db.endTransaction();
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
	
	public Word getWord(int id, String table){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(table, null, WORDS_ID+"="+id, null, null, null, null, "1");
		cursor.moveToFirst();
		Word word = cursorToWord(cursor);
		word.parentTable = table;
		cursor.close();
		db.close();
		return word;
	}
	
	public Word getWord(String wordToFind, String table) {
		Word word = null;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.query(table, null, WORDS_WORD+"=?", new String[]{wordToFind}, null, null, null, "1");
		if (cursor.moveToFirst()){
			word = cursorToWord(cursor);
		}
		word.parentTable = table;
		cursor.close();
		db.close();
		return word;
	}
	
	public void updateWord(Word word, String table) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = wordToValues(word);
		db.replace(table, null, values);
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
	
	public int getEngWordsCount(){
		if (cachedEngWordsCount == 0){
			int count = 0;
			SQLiteDatabase db = getWritableDatabase();
			Cursor cursor = db.rawQuery("select count(*) from "+TABLE_WORDS_ENG, null);
			if (cursor.moveToFirst()){
				count = cursor.getInt(0);
			}
			cursor.close();
			db.close();
			cachedEngWordsCount = count;
		}
		
		return cachedEngWordsCount;
	}
	
	public int getUnguessedWordsCount(String tableName){
		int count = 0;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "+tableName+" where "+WORDS_GUESSED+"= 0", null);
		if (cursor.moveToFirst()){
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
	public int getGuessedWordsCount(String tableName){
		int count = 0;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "+tableName+" where "+WORDS_GUESSED+"= 1", null);
		if (cursor.moveToFirst()){
			count = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	
	public int getViewedWordsCount(String tableName){
		int count = 0;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select count(*) from "+tableName+" where "+WORDS_VIEWED+"= 1", null);
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
		word.hint = findString(cursor, WORDS_HINT);
		word.guessed = findBool(cursor, WORDS_GUESSED);
		word.viewed = findBool(cursor, WORDS_VIEWED);
		return word;
	}
	
	private ContentValues wordToValues(Word word){
		ContentValues values = new ContentValues();
		values.put(WORDS_WORD, word.word);
		values.put(WORDS_HINT, word.hint);
		values.put(WORDS_GUESSED, word.guessed);
		values.put(WORDS_ID, word.id);
		values.put(WORDS_VIEWED, word.viewed);
		return values;
	}
	
	private static String findString(Cursor cursor, String tableName){
		return cursor.getString(cursor.getColumnIndexOrThrow(tableName));
	}
	
	private static boolean findBool(Cursor cursor, String tableName) {
		return (cursor.getInt(cursor.getColumnIndexOrThrow(tableName)) > 0);
	}
	
	private static int findInt(Cursor cursor, String tableName) {
		return cursor.getInt(cursor.getColumnIndexOrThrow(tableName));
	}
}
