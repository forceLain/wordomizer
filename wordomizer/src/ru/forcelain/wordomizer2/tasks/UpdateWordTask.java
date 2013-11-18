package ru.forcelain.wordomizer2.tasks;

import ru.forcelain.wordomizer2.db.DbHelper;
import ru.forcelain.wordomizer2.model.Word;
import android.content.Context;
import android.os.AsyncTask;

public class UpdateWordTask extends AsyncTask<Word, Void, WordsCountStruct> {
	
	public interface UpdateWordCallback {
		public void onWordUpdated(int guessedWordsCount, int totalWordsCount);
	}

	private Context context;
	private UpdateWordCallback updateWordCallback;

	public UpdateWordTask(Context context, UpdateWordCallback updateWordCallback){
		this.context = context;
		this.updateWordCallback = updateWordCallback;
	}
	
	@Override
	protected ru.forcelain.wordomizer2.tasks.WordsCountStruct doInBackground(Word... params) {
		Word word = params[0];
		DbHelper dbHelper = new DbHelper(context);
		dbHelper.updateWord(word);
		WordsCountStruct wordsCountStruct = new WordsCountStruct();
		wordsCountStruct.guesssed = dbHelper.getGuessedWordsCount();
		wordsCountStruct.total = dbHelper.getWordsCount();
		return wordsCountStruct;
	}
	
	@Override
	protected void onPostExecute(WordsCountStruct result) {
		updateWordCallback.onWordUpdated(result.guesssed, result.total);
	}

}

class WordsCountStruct{
	int guesssed;
	int total;
}
