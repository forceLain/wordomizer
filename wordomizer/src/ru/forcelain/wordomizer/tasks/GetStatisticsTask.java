package ru.forcelain.wordomizer.tasks;

import ru.forcelain.wordomizer.db.DbHelper;
import ru.forcelain.wordomizer.model.Statistics;
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
		statistics.guessedWordsCount = dbHelper.getGuessedWordsCount();
		statistics.viewedWordsCount = dbHelper.getViewedWordsCount();
		return statistics;
	}
	
	@Override
	protected void onPostExecute(Statistics result) {
		statisticsCallBack.onStatisticsReceived(result);
	}

}
