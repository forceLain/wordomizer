package ru.forcelain.wordomizer2.activity;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import ru.forcelain.wordomizer2.R;
import ru.forcelain.wordomizer2.animation.SimpleAnimationListener;
import ru.forcelain.wordomizer2.model.Statistics;
import ru.forcelain.wordomizer2.model.Word;
import ru.forcelain.wordomizer2.model.WordsCountStruct;
import ru.forcelain.wordomizer2.playservice.AccomplishmentsOutbox;
import ru.forcelain.wordomizer2.tasks.GetRandomWordTask;
import ru.forcelain.wordomizer2.tasks.GetRandomWordTask.WordCallback;
import ru.forcelain.wordomizer2.tasks.GetStatisticsTask;
import ru.forcelain.wordomizer2.tasks.GetStatisticsTask.StatisticsCallBack;
import ru.forcelain.wordomizer2.tasks.UpdateWordTask;
import ru.forcelain.wordomizer2.tasks.UpdateWordTask.UpdateWordCallback;
import ru.forcelain.wordomizer2.utils.PrefUtils;
import ru.forcelain.wordomizer2.utils.Utils;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizError;

public class GameActivity extends BaseGameActivity implements OnClickListener, StatisticsCallBack{
	
	public static final String TAG = GameActivity.class.getSimpleName();
	public static final int SUCCESS = 1;
	public static final int FAIL = 0;
	protected static final int END = 3;
	protected static final int DELAY = 500;
	protected static final int ACHIEVEMENT_SEQUENCE = 10;
	protected static final int ACHIEVEMENT_DOUBLE_GUESS_DELAY = 5000;
	
	private DrawerLayout drawerLayout;
	private View leftDrawer;
	private LinearLayout userWordHolder;
	private LinearLayout randomedWordHolder;
	private View fadingLayer;
	private View shuffle;
	private View next;
	private View suggestion;
	private View menu;
	private View menuProgress;
	private View menuContent;
	private View showAchievements;
	private View showLeaderboard;
	private View showPrefs;
	private View rateApp;
	private View controlls;
	private View progressBar;
	private View topLayer;
	private TextView totalWords;
	private TextView guessedWords;
	private TextView viewedWords;
	private TextView hint;
	private TextView login;
	private GetRandomWordTask getRandomWordTask;
	private UpdateWordTask updateWordTask;
	private GetStatisticsTask getStatisticsTask;
	private Word sourceWord;
	private int currentPosition;
	private int inSequenceCounter = 0;
	private long roundStartTime;
	private long roundEndTime;
	private boolean firstRound;
	private int[] suggestedPositions;
	
	AccomplishmentsOutbox outbox = new AccomplishmentsOutbox();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		enableDebugLog(true, TAG);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerListener(drawerListener);
		leftDrawer = findViewById(R.id.left_drawer);
		menu = findViewById(R.id.menu);
		menu.setOnClickListener(this);
		menuProgress = findViewById(R.id.left_drawer_progress);
		menuContent = findViewById(R.id.left_drawer_content);
		menuProgress.setVisibility(View.VISIBLE);
		menuContent.setVisibility(View.GONE);
		totalWords = (TextView) findViewById(R.id.total_words);
		guessedWords = (TextView) findViewById(R.id.guessed_words);
		viewedWords = (TextView) findViewById(R.id.views_words);
		hint = (TextView) findViewById(R.id.hint_text);
		fadingLayer = findViewById(R.id.fading_layer);
		userWordHolder = (LinearLayout) findViewById(R.id.user_word_holder);
		randomedWordHolder = (LinearLayout) findViewById(R.id.randomed_word_holder);
		shuffle = findViewById(R.id.shuffle);
		shuffle.setOnClickListener(this);
		next = findViewById(R.id.next);
		next.setOnClickListener(this);
		suggestion = findViewById(R.id.show_suggestion);
		suggestion.setOnClickListener(this);
		login = (TextView) findViewById(R.id.login);
		login.setOnClickListener(this);
		showAchievements = findViewById(R.id.show_achievements);
		showAchievements.setOnClickListener(this);
		showLeaderboard = findViewById(R.id.show_leaderboard);
		showLeaderboard.setOnClickListener(this);
		showPrefs = findViewById(R.id.show_prefs);
		showPrefs.setOnClickListener(this);
		rateApp = findViewById(R.id.rate);
		rateApp.setOnClickListener(this);
		controlls = findViewById(R.id.controlls);
		outbox.loadLocal(this);
		firstRound = (savedInstanceState == null);
		progressBar = findViewById(R.id.progressBar);
		topLayer = findViewById(R.id.top_layer);
		AdBuddiz.setPublisherKey("3d27d75e-81ba-4480-aa99-04e734e35dda");
	    AdBuddiz.cacheAds(this);
	    AdBuddiz.setDelegate(delegate);
	    if (PrefUtils.showPatchNotes(this)){
	    	PrefUtils.setShowPatchNotes(false, this);
	    	showPatchNotes();
	    	
	    }
	    if (PrefUtils.showTutorial(this)){
	    	PrefUtils.setShowTutorial(false, this);
	    	showTutorial();
	    }
		newWord();			
	}

	private void showTutorial() {
		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.tutorial_dialog, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder	.setView(view)
				.setPositiveButton("OK", null);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showPatchNotes() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.patch_title)
				.setMessage(R.string.patch_message)
				.setCancelable(false)
				.setPositiveButton("OK", null);
		AlertDialog alert = builder.create();
		alert.show();
		
	}

	private void newWord() {
		setControlsEnabled(false);
		currentPosition = 0;
		suggestedPositions = null;
		fadeOut();			
	}

	private void fadeOut() {
		if (firstRound){
			AlphaAnimation alphaUp = new AlphaAnimation(0, 0);
	        alphaUp.setFillAfter(true);
	        fadingLayer.startAnimation(alphaUp);
			getRandomWordTask = new GetRandomWordTask(GameActivity.this, getRandomWordCallback);
			getRandomWordTask.execute();
			topLayer.setVisibility(View.GONE);
			fadingLayer.setVisibility(View.GONE);
			controlls.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
			Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
			fadeOut.setFillAfter(true);
			fadeOut.setAnimationListener(new SimpleAnimationListener(){
				@Override
				public void onAnimationEnd(Animation animation) {
					getRandomWordTask = new GetRandomWordTask(GameActivity.this, getRandomWordCallback);
					getRandomWordTask.execute();
				}
			});
			fadingLayer.startAnimation(fadeOut);
		}
	}

	private void setControlsEnabled(boolean enabled) {
		shuffle.setEnabled(enabled);
		next.setEnabled(enabled);
		userWordHolder.setEnabled(enabled);
		randomedWordHolder.setEnabled(enabled);
	}
	
	private void populateButtons() {
		String word = sourceWord.getRandomizedWord();
		int len = word.length();
		for (int i = 0; i < len; i++) {
		    char c = word.charAt(i);
		    userWordHolder.addView(createUserButton(i));
		    randomedWordHolder.addView(createRandomedButton(c));
		}
	}

	private View createUserButton(int position) {
		Button b = (Button) getLayoutInflater().inflate(R.layout.char_button, userWordHolder, false);
		b.setOnClickListener(new UserButtonClick(position));
		return b;
	}

	private View createRandomedButton(char c) {
		Button b = (Button) getLayoutInflater().inflate(R.layout.char_button, randomedWordHolder, false);
		b.setText(Character.toString(c));
		b.setOnClickListener(new RandomButtonClick(c));
		return b;
	}

	private void removeButtons() {
		userWordHolder.removeAllViews();
		randomedWordHolder.removeAllViews();
	}

	private void addChar(char c) {
		Button userButton = (Button) userWordHolder.getChildAt(currentPosition);
		userButton.setText(Character.toString(c));
		currentPosition++;
		for (int i = currentPosition; i < sourceWord.word.length(); i++){
			Button button = (Button) userWordHolder.getChildAt(i);
			if (button.getText().toString().length() > 0){
				currentPosition = i+1;
			} else {
				break;
			}
		}
		if (currentPosition == sourceWord.word.length()){
			checkWin();
		}
	}
	
	private void addCharAtPosition(char c, int position) {
		Button userButton = (Button) userWordHolder.getChildAt(position);
		userButton.setText(Character.toString(c));
	}
	
	private void checkWin() {
		String userWord = construcWord();
		if (userWord.equals(sourceWord.word)){
			colorUiResult(true);
			inSequenceCounter++;
			roundEndTime = System.currentTimeMillis();
			sourceWord.guessed = true;
			updateWordTask = new UpdateWordTask(this, updateWordCallback);
			updateWordTask.execute(sourceWord);
		} else {
			colorUiResult(false);
			inSequenceCounter = 0;
			uiHandler.sendEmptyMessageDelayed(FAIL, DELAY);
		}
	}

	private void colorUiResult(boolean success) {
		int count = userWordHolder.getChildCount();
		for (int i = 0; i < count; i++){
			Button userButton = (Button) userWordHolder.getChildAt(i);
			if (success){
				userButton.setBackgroundResource(R.drawable.game_button_success);				
			} else {
				userButton.setBackgroundResource(R.drawable.game_button_fail);				
			}
		}
	}

	private String construcWord() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sourceWord.word.length(); i++){
			Button userButton = (Button) userWordHolder.getChildAt(i);
			sb.append(userButton.getText().toString());
		}
		return sb.toString();
	}

	private void enableRandomedButton(char ch) {
		int childCount = randomedWordHolder.getChildCount();
		for (int i = 0; i < childCount; i++){
			Button randomedButton = (Button) randomedWordHolder.getChildAt(i);
			if (randomedButton.getText().toString().charAt(0) == ch){
				if (!randomedButton.isEnabled()){
					randomedButton.setEnabled(true);
					return;						
				}
			}
		}
	}
	
	private void clearUserButtons(int startPos) {
		for (int i = startPos; i < sourceWord.word.length(); i++){
			Button userButton = (Button) userWordHolder.getChildAt(i);
			String text = userButton.getText().toString();
			if (text.length() > 0){
				enableRandomedButton(userButton.getText().toString().charAt(0));				
				userButton.setText("");
			}
			userButton.setBackgroundResource(R.drawable.game_button);
		}
		currentPosition = startPos;
	}	
	
	private void clearButtonsForced() {
		for (int i = 0; i < sourceWord.word.length(); i++){
			Button button = (Button) userWordHolder.getChildAt(i);
			button.setText("");
			button.setBackgroundResource(R.drawable.game_button);
			button = (Button) randomedWordHolder.getChildAt(i);
			button.setEnabled(true);
		}
		currentPosition = 0;
	}	
	
	class RandomButtonClick implements View.OnClickListener{
		
		private char ch;
		
		public RandomButtonClick(char ch){
			this.ch = ch;
		}

		@Override
		public void onClick(View v) {
			v.setEnabled(false);
			addChar(ch);
		}
	}
	
	class UserButtonClick implements View.OnClickListener{
		
		private int position;
		
		public UserButtonClick(int position){
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			clearUserButtons(position);
		}	
	}
	
	private GetRandomWordTask.WordCallback getRandomWordCallback = new WordCallback() {		
		@Override
		public void onWordReceived(Word word) {
			if (word != null){
				sourceWord = word;
				hint.setText(sourceWord.hint);
				removeButtons();
				populateButtons();
				fadeIn();
				roundStartTime = System.currentTimeMillis();
			} else {
				showEnd();
			}
		}
	};
	
	private UpdateWordTask.UpdateWordCallback updateWordCallback = new UpdateWordCallback() {

		@Override
		public void onWordUpdated(WordsCountStruct wordsCountStruct) {
			int total = wordsCountStruct.total + wordsCountStruct.totalEng;
			int guessed = wordsCountStruct.guesssed + wordsCountStruct.guesssedEng;
			outbox.score = guessed;
			checkForAchievements(guessed, total);
	        pushAccomplishments();
	        boolean rusWordsEnabled = PrefUtils.isRusWordsEnabled(GameActivity.this);
	        boolean engWordsEnabled = PrefUtils.isEngWordsEnabled(GameActivity.this);
	        if (rusWordsEnabled && engWordsEnabled){
	        	if (total == guessed){
	        		uiHandler.sendEmptyMessageDelayed(END, DELAY);
	        	} else {
	        		uiHandler.sendEmptyMessageDelayed(SUCCESS, DELAY);	
	        	}
	        } else if (engWordsEnabled){
	        	if (wordsCountStruct.guesssedEng == wordsCountStruct.totalEng){
	        		uiHandler.sendEmptyMessageDelayed(END, DELAY);	
	        	} else {
	        		uiHandler.sendEmptyMessageDelayed(SUCCESS, DELAY);	
	        	}
	        } else {
	        	if (wordsCountStruct.guesssed == wordsCountStruct.total){
	        		uiHandler.sendEmptyMessageDelayed(END, DELAY);	
	        	} else {
	        		uiHandler.sendEmptyMessageDelayed(SUCCESS, DELAY);	
	        	}
	        }
		}
	};
	
	private void checkForAchievements(int guessedWordsCount, int totalWordsCount){
		Log.d(TAG, "guessedWordsCount "+guessedWordsCount+" totalWordsCount "+totalWordsCount);
		Log.d(TAG, "inSequenceCounter "+inSequenceCounter);
		Log.d(TAG, "roundEndTime "+roundEndTime+" roundStartTime "+roundStartTime+" round: "+(roundEndTime - roundStartTime));
		if (guessedWordsCount == 10){
			outbox.g10Achievement = true;
		}
		
		if (guessedWordsCount == 100){
			outbox.g100Achievement = true;
		}
		
		if (guessedWordsCount == 1000){
			outbox.g1000Achievement = true;
		}
		
		if (inSequenceCounter == ACHIEVEMENT_SEQUENCE){
			outbox.in10sequence = true;
		}
		
		if (totalWordsCount == guessedWordsCount){
			outbox.allWords = true;
		}
		
		if (roundEndTime - roundStartTime < ACHIEVEMENT_DOUBLE_GUESS_DELAY){
			outbox.doubleWord = true;
		}
	}
	
	private void pushAccomplishments() {
		if (!isSignedIn()) {
            // can't push to the cloud, so save locally
			outbox.saveLocal(this);
            return;
        }
		
		if (outbox.g10Achievement){
			getGamesClient().unlockAchievement(getString(R.string.achievement_10));
			outbox.g10Achievement = false;
		}
		
		if (outbox.g100Achievement){
			getGamesClient().unlockAchievement(getString(R.string.achievement_100));
			outbox.g100Achievement = false;
		}
		
		if (outbox.g1000Achievement){
			getGamesClient().unlockAchievement(getString(R.string.achievement_1000));
			outbox.g1000Achievement = false;
		}
		
		if (outbox.allWords){
			getGamesClient().unlockAchievement(getString(R.string.achievement_all_words));
			outbox.allWords = false;
		}
		
		if (outbox.in10sequence){
			getGamesClient().unlockAchievement(getString(R.string.achievement_10_in_sequence));
			outbox.in10sequence = false;
		}
		
		if (outbox.doubleWord){
			getGamesClient().unlockAchievement(getString(R.string.achievement_double));
			outbox.doubleWord = false;
		}
		
		if (outbox.score > 0){
			getGamesClient().submitScore(getString(R.string.leaderboard_top), outbox.score);
			outbox.score = -1;
		}
		
		outbox.saveLocal(this);
	}

	private void fadeIn() {
		Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		fadeIn.setFillAfter(true);
		fadeIn.setAnimationListener(new SimpleAnimationListener(){
			@Override
			public void onAnimationEnd(Animation animation) {
				setControlsEnabled(true);
			}
		});
		fadingLayer.startAnimation(fadeIn);
		if (firstRound){			
			topLayer.setVisibility(View.VISIBLE);
			fadingLayer.setVisibility(View.VISIBLE);
			controlls.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			firstRound = false;
		}
	}
	
	private Handler uiHandler = new Handler(new Handler.Callback(){

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				newWord();
				break;
			case FAIL:
				clearUserButtons(0);
				break;
			case END:
				showEnd();
				break;
			}
			return false;
		}
		
	});

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shuffle:
			shuffle();
			break;
		case R.id.next:
			inSequenceCounter = 0;
			newWord();
			break;
		case R.id.menu:
			toggleDrawer();
			break;
		case R.id.login:
			login();
			break;
		case R.id.show_achievements:
			showAchivements();
			break;
		case R.id.show_leaderboard:
			showLeaderboard();
			break;
		case R.id.rate:
			askForRate();
			break;
		case R.id.show_prefs:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		case R.id.show_suggestion:
			if (AdBuddiz.isReadyToShowAd(this) && PrefUtils.showAd(this) && suggestedPositions == null){
				AdBuddiz.showAd(this);				
			} else {
				showSuggestion();				
			}
			break;
		}
	}
	
	private void showSuggestion() {
		clearButtonsForced();
		int count = sourceWord.word.length()/2;
		int len = sourceWord.word.length();
		
		if (suggestedPositions == null){
			Random random = new Random();
			suggestedPositions = new int[count];
			Arrays.fill(suggestedPositions, -1);
			for (int i = 0; i < suggestedPositions.length; i++){
				int r;
				do {
					r = random.nextInt(len);
				} while (Utils.contains(suggestedPositions, r));
				suggestedPositions[i] = r;
			}
		}
		
		for (int i = 0; i < count; i++){	
			char c = sourceWord.word.charAt(suggestedPositions[i]);
			addCharAtPosition(c, suggestedPositions[i]);
			for (int j = 0; j < len; j++){
				Button button = (Button) randomedWordHolder.getChildAt(j);
				if (button.getText().toString().charAt(0) == c && button.isEnabled()){
					button.setEnabled(false);
					break;
				}
			}
		}
		for (int i = 0; i < len; i++){
			Button button = (Button) userWordHolder.getChildAt(i);
			if (button.getText().length() == 0){
				break;
			} else {
				currentPosition = i+1;
			}
		}
		
	}

	private void askForRate() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setMessage(R.string.ask_rate);
		b.setNegativeButton(R.string.cancel, null);
		b.setPositiveButton(R.string.rate_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				openMarket();
			}
		});
		b.create().show();
		
	}

	private void openMarket() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
		}
	}

	private void showEnd() {
		startActivity(new Intent(this, EndActivity.class));
		finish();
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

	private void login() {
		if (isSignedIn()){
			askLogOut();
		} else {
			beginUserInitiatedSignIn();
		}
	}

	private void askLogOut() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setMessage(R.string.ask_logout);
		b.setNegativeButton(R.string.cancel, null);
		b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				signOut();
				login.setText(R.string.login);
			}
		});
		b.create().show();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {
        case KeyEvent.KEYCODE_MENU:
        	toggleDrawer();
            return true;
    }
		return super.onKeyDown(keyCode, event);
	}

	private void toggleDrawer() {
		if (drawerLayout.isDrawerOpen(leftDrawer)){
    		drawerLayout.closeDrawer(leftDrawer);       
    	} else {
    		drawerLayout.openDrawer(leftDrawer);	
    	}
	}

	private void shuffle() {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		shake.setAnimationListener(new SimpleAnimationListener(){
			@Override
			public void onAnimationEnd(Animation animation) {
				List<View> buttonsList = new LinkedList<View>();
				int childCount = randomedWordHolder.getChildCount();
				for (int i = 0; i < childCount; i++){
					buttonsList.add(randomedWordHolder.getChildAt(i));
				}
				Collections.shuffle(buttonsList);
				randomedWordHolder.removeAllViews();
				for (View view : buttonsList) {
					randomedWordHolder.addView(view);
				}
			}
		});
		randomedWordHolder.startAnimation(shake);
	}
	
	private AdBuddizDelegate delegate = new AdBuddizDelegate() {
		
		@Override
		public void didShowAd() {	
		}
		
		@Override
		public void didHideAd() {
			showSuggestion();
		}
		
		@Override
		public void didFailToShowAd(AdBuddizError arg0) {
			showSuggestion();
		}
		
		@Override
		public void didClick() {}
		
		@Override
		public void didCacheAd() {}
	};
	
	private SimpleDrawerListener drawerListener = new SimpleDrawerListener() {
		@Override
		public void onDrawerOpened(View drawerView) {
			getStatisticsTask = new GetStatisticsTask(GameActivity.this, GameActivity.this);
			getStatisticsTask.execute();
		};
		
		@Override
		public void onDrawerClosed(View drawerView) {
			menuProgress.setVisibility(View.VISIBLE);
			menuContent.setVisibility(View.GONE);
		};
	};

	@Override
	public void onStatisticsReceived(Statistics statistics) {
		String rus = getString(R.string.rus);
		String eng = getString(R.string.eng);
		totalWords.setText(getString(R.string.total_words)+" "+statistics.totalWordsCount+" "+rus+"; "+statistics.totalEngWordsCount+" "+eng);
		guessedWords.setText(getString(R.string.guessed_words)+" "+statistics.guessedWordsCount+" "+rus+"; "+statistics.guessedEngWordsCount+" "+eng);
		viewedWords.setText(getString(R.string.viewed_words)+" "+statistics.viewedWordsCount+" "+rus+"; "+statistics.viewedEngWordsCount+" "+eng);
		menuProgress.setVisibility(View.GONE);
		menuContent.setVisibility(View.VISIBLE);
	}

	@Override
	public void onSignInFailed() {
		login.setText(getString(R.string.login));
	}

	@Override
	public void onSignInSucceeded() {
		Player p = getGamesClient().getCurrentPlayer();
        String displayName;
        if (p == null) {
            Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
		login.setText(displayName);
	}
}
