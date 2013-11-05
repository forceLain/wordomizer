package ru.forcelain.wordomizer.activity;

import ru.forcelain.wordomizer.R;
import ru.forcelain.wordomizer.db.DbHelper;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

public class SplashActivity extends FragmentActivity {
	
	public static final String TAG = SplashActivity.class.getSimpleName();
	public static final int SPLASH_READY = 0;
	public static final int DELAY = 1000;
	
	private boolean ready;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		ready = false;
		new DatabaseInitTask().execute();
		uiHandler.sendEmptyMessageDelayed(SPLASH_READY, DELAY);	
	}
	
	private class DatabaseInitTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			DbHelper dbHelper = new DbHelper(SplashActivity.this);
			dbHelper.getWordsCount();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			ready = true;
			uiHandler.removeMessages(SPLASH_READY);
			uiHandler.sendEmptyMessageDelayed(SPLASH_READY, DELAY);	
		}
	}
	
	private Handler uiHandler = new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			if (ready){
				startActivity(new Intent(SplashActivity.this, GameActivity.class));
				finish();
			}
			return true;
		}
	});
}
