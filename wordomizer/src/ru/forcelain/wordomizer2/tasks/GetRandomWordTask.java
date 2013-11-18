package ru.forcelain.wordomizer2.tasks;

import java.util.Random;

import ru.forcelain.wordomizer2.db.DbHelper;
import ru.forcelain.wordomizer2.model.Word;
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
		boolean hasUngessedWords = dbHelper.getUnguessedWordsCount() > 0;
		if (hasUngessedWords){
			int wordsCount = dbHelper.getWordsCount();
			do {
				int randromId = random.nextInt(wordsCount)+1;
				word = dbHelper.getWord(randromId);
			} while (word.guessed);
			
			word.viewed = true;
			dbHelper.updateWord(word);
			return word;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Word result) {
		getRandomWordCallback.onWordReceived(result);
	}

}
