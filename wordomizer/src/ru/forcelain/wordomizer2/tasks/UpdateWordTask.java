package ru.forcelain.wordomizer2.tasks;

import ru.forcelain.wordomizer2.db.DbHelper;
import ru.forcelain.wordomizer2.model.Word;
import ru.forcelain.wordomizer2.model.WordsCountStruct;
import android.content.Context;
import android.os.AsyncTask;

public class UpdateWordTask extends AsyncTask<Word, Void, WordsCountStruct> {
	
	public interface UpdateWordCallback {
		public void onWordUpdated(WordsCountStruct wordsCountStruct);
	}
	
	private Context context;
	private UpdateWordCallback updateWordCallback;

	public UpdateWordTask(Context context, UpdateWordCallback updateWordCallback){
		this.context = context;
		this.updateWordCallback = updateWordCallback;
	}
	
	@Override
	protected WordsCountStruct doInBackground(Word... params) {
		Word word = params[0];
		DbHelper dbHelper = new DbHelper(context);
		dbHelper.updateWord(word, word.parentTable);
		WordsCountStruct wordsCountStruct = new WordsCountStruct();
		wordsCountStruct.guesssed = dbHelper.getGuessedWordsCount(DbHelper.TABLE_WORDS);
		wordsCountStruct.guesssedEng = dbHelper.getGuessedWordsCount(DbHelper.TABLE_WORDS_ENG);
		wordsCountStruct.total = dbHelper.getWordsCount();
		wordsCountStruct.totalEng = dbHelper.getEngWordsCount();
		return wordsCountStruct;
	}
	
	@Override
	protected void onPostExecute(WordsCountStruct result) {
		updateWordCallback.onWordUpdated(result);
	}

}
