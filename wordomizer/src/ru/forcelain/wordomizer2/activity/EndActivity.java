package ru.forcelain.wordomizer2.activity;

import ru.forcelain.wordomizer2.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.example.games.basegameutils.BaseGameActivity;

public class EndActivity extends BaseGameActivity implements OnClickListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end);
		
		findViewById(R.id.achievements).setOnClickListener(this);
		findViewById(R.id.leaders).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.achievements:
			showAchivements();
			break;
		case R.id.leaders:
			showLeaderboard();
			break;
		}
	}
	
	private void showLeaderboard() {
		if (isSignedIn()) {
            startActivityForResult(getGamesClient().getLeaderboardIntent(getString(R.string.leaderboard_top)), 0);
        } else {
            showAlert(getString(R.string.leaderboards_not_available));
        }
	}

	private void showAchivements() {
		if (isSignedIn()) {
            startActivityForResult(getGamesClient().getAchievementsIntent(), 0);
        } else {
            showAlert(getString(R.string.achievements_not_available));
        }
	}

	@Override
	public void onSignInFailed() {		
	}

	@Override
	public void onSignInSucceeded() {		
	}
}
