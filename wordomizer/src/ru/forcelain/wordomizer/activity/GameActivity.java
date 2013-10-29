package ru.forcelain.wordomizer.activity;

import ru.forcelain.wordomizer.R;
import ru.forcelain.wordomizer.model.Word;
import ru.forcelain.wordomizer.tasks.GetRandomWordTask;
import ru.forcelain.wordomizer.tasks.GetRandomWordTask.GetRandomWordCallback;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameActivity extends FragmentActivity implements GetRandomWordCallback, OnClickListener{
	
	public static final String TAG = GameActivity.class.getSimpleName();
	
	private LinearLayout userWordHolder;
	private LinearLayout randomedWordHolder;
	private GetRandomWordTask getRandomWordTask;
	private Word sourceWord;
	private int currentPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		userWordHolder = (LinearLayout) findViewById(R.id.user_word_holder);
		randomedWordHolder = (LinearLayout) findViewById(R.id.randomed_word_holder);
		getRandomWordTask = new GetRandomWordTask(this, this);
		getRandomWordTask.execute();
		currentPosition = 0;		
	}

	@Override
	public void onWordReceived(Word word) {
		sourceWord = word;
		updateButtons();
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
		    userWordHolder.addView(createUserButton());
		    randomedWordHolder.addView(createRandomedButton(c));
		}
	}

	private View createUserButton() {
		Button b = (Button) getLayoutInflater().inflate(R.layout.char_button, userWordHolder, false);
		return b;
	}

	private View createRandomedButton(char c) {
		Button b = (Button) getLayoutInflater().inflate(R.layout.char_button, randomedWordHolder, false);
		b.setText(Character.toString(c));
		b.setOnClickListener(this);
		b.setTag(c);
		return b;
	}

	private void clearButtons() {
		userWordHolder.removeAllViews();
		randomedWordHolder.removeAllViews();
	}

	@Override
	public void onClick(View v) {
		Object tag = v.getTag();
		if (tag != null && tag instanceof Character){
			char c = (Character) tag;
			addChar(c);
			v.setEnabled(false);
		}
		
	}

	private void addChar(char c) {
		Button userButton = (Button) userWordHolder.getChildAt(currentPosition);
		userButton.setText(Character.toString(c));
		currentPosition++;
	}
}
