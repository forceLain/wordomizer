package ru.forcelain.wordomizer2.tasks;

import ru.forcelain.wordomizer2.db.DbHelper;
import ru.forcelain.wordomizer2.model.Statistics;
import android.content.Context;
import android.os.AsyncTask;

public class GetStatisticsTask extends AsyncTask<Void, Void, Statistics> {
	
	public interface StatisticsCallBack{
		void onStatisticsReceived(Statistics statistics);
	}
	
	private Context context;
	private StatisticsCallBack statisticsCallBack;

	public GetStatisticsTask(Context context, StatisticsCallBack statisticsCallBack){
		this.statisticsCallBack = statisticsCallBack;
		this.context = context;
	}
	
	@Override
	protected Statistics doInBackground(Void... params) {
		DbHelper dbHelper = new DbHelper(context);
		Statistics statistics = new Statistics();
		statistics.totalWordsCount = dbHelper.getWordsCount();
		statistics.totalEngWordsCount = dbHelper.getEngWordsCount();
		statistics.guessedWordsCount = dbHelper.getGuessedWordsCount(DbHelper.TABLE_WORDS);
		statistics.guessedEngWordsCount = dbHelper.getGuessedWordsCount(DbHelper.TABLE_WORDS_ENG);
		statistics.viewedWordsCount = dbHelper.getViewedWordsCount(DbHelper.TABLE_WORDS);
		statistics.viewedEngWordsCount = dbHelper.getViewedWordsCount(DbHelper.TABLE_WORDS_ENG);
		return statistics;
	}
	
	@Override
	protected void onPostExecute(Statistics result) {
		statisticsCallBack.onStatisticsReceived(result);
	}

}
