package ru.forcelain.wordomizer.activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ru.forcelain.wordomizer.R;
import ru.forcelain.wordomizer.model.Statistics;
import ru.forcelain.wordomizer.model.Word;
import ru.forcelain.wordomizer.tasks.CheckWordTask;
import ru.forcelain.wordomizer.tasks.GetRandomWordTask;
import ru.forcelain.wordomizer.tasks.GetStatisticsTask;
import ru.forcelain.wordomizer.tasks.GetStatisticsTask.StatisticsCallBack;
import ru.forcelain.wordomizer.tasks.WordCallback;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
	private View shuffle;
	private View next;
	private View search;
	private View menu;
	private TextView totalWords;
	private TextView guessedWords;
	private TextView viewedWords;
	private TextView lastWord;
	private GetRandomWordTask getRandomWordTask;
	private CheckWordTask checkWordTask;
	private GetStatisticsTask getStatisticsTask;
	private Word sourceWord;
	private Word previousWord;
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
		lastWord = (TextView) findViewById(R.id.last_word);
		lastWord.setOnClickListener(this);
		lastWord.setVisibility(View.GONE);
		userWordHolder = (LinearLayout) findViewById(R.id.user_word_holder);
		randomedWordHolder = (LinearLayout) findViewById(R.id.randomed_word_holder);
		shuffle = findViewById(R.id.shuffle);
		shuffle.setOnClickListener(this);
		next = findViewById(R.id.next);
		next.setOnClickListener(this);
		search = findViewById(R.id.search);
		search.setOnClickListener(this);
		
		newWord();
	}

	private void newWord() {
		setControlsEnabled(false);
		getRandomWordTask = new GetRandomWordTask(this, getRandomWordCallback);
		getRandomWordTask.execute();
		currentPosition = 0;		
	}

	private void setControlsEnabled(boolean enabled) {
		shuffle.setEnabled(enabled);
		next.setEnabled(enabled);
		search.setEnabled(enabled);
		userWordHolder.setEnabled(enabled);
		randomedWordHolder.setEnabled(enabled);
	}

	private void updateButtons() {
		clearButtons();
		populateButtons();
		setControlsEnabled(true);
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
		checkWordTask = new CheckWordTask(this, checkWordCallback);
		checkWordTask.execute(construcWord());
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
			if (previousWord != null){
				lastWord.setText(getString(R.string.last_word)+" "+previousWord.word);		
				lastWord.setVisibility(View.VISIBLE);
			}
			updateButtons();
		}
	};
	
	private WordCallback checkWordCallback = new WordCallback() {
		
		@Override
		public void onWordReceived(Word word) {
			if (word == null){
				uiHandler.sendEmptyMessageDelayed(FAIL, DELAY);
			} else {
				uiHandler.sendEmptyMessageDelayed(SUCCESS, DELAY);
			}
		}
	};
	
	private Handler uiHandler = new Handler(new Handler.Callback(){

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				previousWord = sourceWord;
				newWord();
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
			newWord();
			break;
		case R.id.search:
			showHint(sourceWord);
			break;
		case R.id.last_word:
			if (previousWord != null){
				showHint(previousWord);				
			}
			break;
		case R.id.menu:
			toggleDrawer();
			break;
		}
	}
	
	private void showHint(Word word) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(word.hint);
        builder.setPositiveButton("OK", null);
        builder.create().show();
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
