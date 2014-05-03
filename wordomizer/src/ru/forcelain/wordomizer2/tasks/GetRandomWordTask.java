package ru.forcelain.wordomizer2.tasks;

import java.util.Random;

import ru.forcelain.wordomizer2.db.DbHelper;
import ru.forcelain.wordomizer2.model.Word;
import ru.forcelain.wordomizer2.utils.PrefUtils;
import android.content.Context;
import android.os.AsyncTask;

public class GetRandomWordTask extends AsyncTask<Void, Void, Word> {
	
	public interface WordCallback {
		public void onWordReceived(Word word);
	}
	
	private Context context;
	private Random random;
	private WordCallback getRandomWordCallback;

	public GetRandomWordTask(Context context, WordCallback getRandomWordCallback){
		this.context = context;
		this.getRandomWordCallback = getRandomWordCallback;
		random = new Random();
	}
	
	@Override
	protected Word doInBackground(Void... params) {
		
		DbHelper dbHelper = new DbHelper(context);
		Word word;
		boolean rusWordsEnabled = PrefUtils.isRusWordsEnabled(context);
		boolean engWordsEnabled = PrefUtils.isEngWordsEnabled(context);
		int unguessedWordsCount = 0;
		if (rusWordsEnabled){
			int unguessedWordsCountRus = dbHelper.getUnguessedWordsCount(DbHelper.TABLE_WORDS);
			unguessedWordsCount += unguessedWordsCountRus;
		}
		if (engWordsEnabled){
			int unguessedWordsCountEng = dbHelper.getUnguessedWordsCount(DbHelper.TABLE_WORDS_ENG);
			unguessedWordsCount += unguessedWordsCountEng;
		}
		
		boolean hasUngessedWords = unguessedWordsCount > 0;
		if (hasUngessedWords){
			
			String table;
			int wordsCount;
			if (rusWordsEnabled && engWordsEnabled){
				if (random.nextBoolean()){
					table = DbHelper.TABLE_WORDS;
					wordsCount = dbHelper.getWordsCount();
				} else {
					table = DbHelper.TABLE_WORDS_ENG;
					wordsCount = dbHelper.getEngWordsCount();
				}
			} else if (engWordsEnabled){
				table = DbHelper.TABLE_WORDS_ENG;
				wordsCount = dbHelper.getEngWordsCount();
			} else {
				table = DbHelper.TABLE_WORDS;
				wordsCount = dbHelper.getWordsCount();
			}
						
			do {
				int randromId = random.nextInt(wordsCount)+1;
				word = dbHelper.getWord(randromId, table);
			} while (word.guessed);
			
			word.viewed = true;
			dbHelper.updateWord(word, table);
			return word;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Word result) {
		getRandomWordCallback.onWordReceived(result);
	}

}
