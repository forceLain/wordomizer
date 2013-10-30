package ru.forcelain.wordomizer.activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ru.forcelain.wordomizer.R;
import ru.forcelain.wordomizer.model.Word;
import ru.forcelain.wordomizer.tasks.CheckWordTask;
import ru.forcelain.wordomizer.tasks.GetRandomWordTask;
import ru.forcelain.wordomizer.tasks.WordCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameActivity extends FragmentActivity implements OnClickListener{
	
	public static final String TAG = GameActivity.class.getSimpleName();
	public static final int SUCCESS = 1;
	public static final int FAIL = 0;
	protected static final int DELAY = 500;
	
	private LinearLayout userWordHolder;
	private LinearLayout randomedWordHolder;
	private View shuffle;
	private View next;
	private View search;
	private GetRandomWordTask getRandomWordTask;
	private CheckWordTask checkWordTask;
	private Word sourceWord;
	private int currentPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
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
		getRandomWordTask = new GetRandomWordTask(this, getRandomWordCallback);
		getRandomWordTask.execute();
		currentPosition = 0;		
	}

	private void updateButtons() {
		clearButtons();
		populateButtons();
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
}
