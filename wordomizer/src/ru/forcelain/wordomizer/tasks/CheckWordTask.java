package ru.forcelain.wordomizer.tasks;

import ru.forcelain.wordomizer.db.DbHelper;
import ru.forcelain.wordomizer.model.Word;
import android.content.Context;
import android.os.AsyncTask;

public class CheckWordTask extends AsyncTask<String, Void, Word> {

	private Context context;
	private String wordToFind;
	private WordCallback checkWordCallback;

	public CheckWordTask(Context context, WordCallback checkWordCallback){
		this.context = context;
		this.checkWordCallback = checkWordCallback;
	}
	
	@Override
	protected Word doInBackground(String... params) {
		wordToFind = params[0];
		DbHelper dbHelper = new DbHelper(context);
		Word word = dbHelper.getWord(wordToFind);
		if (word != null){
			word.guessed = true;
			dbHelper.updateWord(word);
		}
		return word;
	}
	
	@Override
	protected void onPostExecute(Word result) {
		checkWordCallback.onWordReceived(result);
	}

}
