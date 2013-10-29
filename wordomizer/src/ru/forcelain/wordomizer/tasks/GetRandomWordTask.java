package ru.forcelain.wordomizer.tasks;

import java.util.Random;

import ru.forcelain.wordomizer.db.DbHelper;
import ru.forcelain.wordomizer.model.Word;
import android.content.Context;
import android.os.AsyncTask;

public class GetRandomWordTask extends AsyncTask<Void, Void, Word> {
	
	public interface GetRandomWordCallback {
		public void onWordReceived(Word word);
	}
	
	private Context context;
	private Random randrom;
	private GetRandomWordCallback getRandomWordCallback;

	public GetRandomWordTask(Context context, GetRandomWordCallback getRandomWordCallback){
		this.context = context;
		this.getRandomWordCallback = getRandomWordCallback;
		randrom = new Random();		
	}
	
	@Override
	protected Word doInBackground(Void... params) {
		
		DbHelper dbHelper = new DbHelper(context);
		Word word;
		int wordsCount = dbHelper.getWordsCount();
		do {
			int randromId = randrom.nextInt(wordsCount);
			word = dbHelper.getWord(randromId);
		} while (word.guessed);
		
		return word;
	}
	
	@Override
	protected void onPostExecute(Word result) {
		getRandomWordCallback.onWordReceived(result);
	}

}
