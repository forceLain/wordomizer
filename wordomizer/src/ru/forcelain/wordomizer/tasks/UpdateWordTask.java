package ru.forcelain.wordomizer.tasks;

import ru.forcelain.wordomizer.db.DbHelper;
import ru.forcelain.wordomizer.model.Word;
import android.content.Context;
import android.os.AsyncTask;

public class UpdateWordTask extends AsyncTask<Word, Void, Void> {
	
	public interface UpdateWordCallback {
		public void onWordUpdated();
	}

	private Context context;
	private UpdateWordCallback updateWordCallback;

	public UpdateWordTask(Context context, UpdateWordCallback updateWordCallback){
		this.context = context;
		this.updateWordCallback = updateWordCallback;
	}
	
	@Override
	protected Void doInBackground(Word... params) {
		Word word = params[0];
		DbHelper dbHelper = new DbHelper(context);
		dbHelper.updateWord(word);
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		updateWordCallback.onWordUpdated();
	}

}
