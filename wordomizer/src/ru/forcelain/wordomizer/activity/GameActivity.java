package ru.forcelain.wordomizer.activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ru.forcelain.wordomizer.R;
import ru.forcelain.wordomizer.animation.SimpleAnimationListener;
import ru.forcelain.wordomizer.model.Statistics;
import ru.forcelain.wordomizer.model.Word;
import ru.forcelain.wordomizer.tasks.GetRandomWordTask;
import ru.forcelain.wordomizer.tasks.GetStatisticsTask;
import ru.forcelain.wordomizer.tasks.GetStatisticsTask.StatisticsCallBack;
import ru.forcelain.wordomizer.tasks.WordCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameActivity extends FragmentActivity implements OnClickListener, StatisticsCallBack{
	
	public static final String TAG = GameActivity.class.getSimpleName();
	public static final int SUCCESS = 1;
	public static final int FAIL = 0;
	protected static final int DELAY = 500;
	
	private DrawerLayout drawerLayout;
	private View leftDrawer;
	private LinearLayout userWordHolder;
	private LinearLayout randomedWordHolder;
	private View fadingLayer;
	private View shuffle;
	private View next;
	private View menu;
	private TextView totalWords;
	private TextView guessedWords;
	private TextView viewedWords;
	private TextView hint;
	private GetRandomWordTask getRandomWordTask;
	private GetStatisticsTask getStatisticsTask;
	private Word sourceWord;
	private int currentPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerLayout.setDrawerListener(drawerListener);
		leftDrawer = findViewById(R.id.left_drawer);
		menu = findViewById(R.id.menu);
		menu.setOnClickListener(this);
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
		
		newWord(true);
	}

	private void newWord(boolean immediate) {
		setControlsEnabled(false);
		currentPosition = 0;
		if (immediate){
			hide();
		} else {
			fadeOut();			
		}
	}
	
	private void hide(){
		AlphaAnimation alphaUp = new AlphaAnimation(0, 0);
        alphaUp.setFillAfter(true);
        fadingLayer.startAnimation(alphaUp);
		getRandomWordTask = new GetRandomWordTask(GameActivity.this, getRandomWordCallback);
		getRandomWordTask.execute();
	}

	private void fadeOut() {
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

	private void clearButtons() {
		userWordHolder.removeAllViews();
		randomedWordHolder.removeAllViews();
	}

	private void addChar(char c) {
		Button userButton = (Button) userWordHolder.getChildAt(currentPosition);
		userButton.setText(Character.toString(c));
		currentPosition++;
		if (currentPosition == sourceWord.word.length()){
			checkWin();
		}
	}
	
	private void checkWin() {
		String userWord = construcWord();
		if (userWord.equals(sourceWord.word)){
			uiHandler.sendEmptyMessageDelayed(SUCCESS, DELAY);
		} else {
			uiHandler.sendEmptyMessageDelayed(FAIL, DELAY);
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
		if (startPos > currentPosition){
			return;
		}
		for (int i = startPos; i < currentPosition; i++){
			Button userButton = (Button) userWordHolder.getChildAt(i);
			enableRandomedButton(userButton.getText().toString().charAt(0));
			userButton.setText("");
		}
		currentPosition = startPos;
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
	
	private WordCallback getRandomWordCallback = new WordCallback() {		
		@Override
		public void onWordReceived(Word word) {
			sourceWord = word;
			hint.setText(sourceWord.hint);
			clearButtons();
			populateButtons();
			fadeIn();
		}
	};
	
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
	}
	
	private Handler uiHandler = new Handler(new Handler.Callback(){

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				newWord(false);
				break;
			case FAIL:
				clearUserButtons(0);
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
			newWord(false);
			break;
		case R.id.menu:
			toggleDrawer();
			break;
		}
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
	
	private SimpleDrawerListener drawerListener = new SimpleDrawerListener() {
		@Override
		public void onDrawerOpened(View drawerView) {
			getStatisticsTask = new GetStatisticsTask(GameActivity.this, GameActivity.this);
			getStatisticsTask.execute();
		};
	};

	@Override
	public void onStatisticsReceived(Statistics statistics) {
		totalWords.setText(getString(R.string.total_words)+" "+statistics.totalWordsCount);
		guessedWords.setText(getString(R.string.guessed_words)+" "+statistics.guessedWordsCount);
		viewedWords.setText(getString(R.string.viewed_words)+" "+statistics.viewedWordsCount);
	}
}
