package ru.forcelain.wordomizer.activity;

import ru.forcelain.wordomizer.R;
import ru.forcelain.wordomizer.adapter.ButtonsGridAdapter;
import ru.forcelain.wordomizer.fragments.MenuFragment;
import ru.forcelain.wordomizer.model.Word;
import ru.forcelain.wordomizer.tasks.GetRandomWordTask;
import ru.forcelain.wordomizer.tasks.GetRandomWordTask.GetRandomWordCallback;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class GameActivity extends FragmentActivity implements GetRandomWordCallback{
	
	public static final String TAG = GameActivity.class.getSimpleName();
	
	private SlidingMenu menu;
	private LinearLayout userWordHolder;
	private LinearLayout randomedWordHolder;
	private GridView buttonsGrid;
	private ButtonsGridAdapter adapter;
	private MenuFragment menuFragment;
	private GetRandomWordTask getRandomWordTask;
	private Word sourceWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		userWordHolder = (LinearLayout) findViewById(R.id.user_word_holder);
		randomedWordHolder = (LinearLayout) findViewById(R.id.randomed_word_holder);
		buttonsGrid = (GridView) findViewById(R.id.buttons_grid);
				
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setShadowDrawable(R.drawable.menu_shadow);
		menu.setFadeEnabled(true);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.menu_frame);

		menuFragment = new MenuFragment();
		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.menu_frame, menuFragment).commit();
		
		getRandomWordTask = new GetRandomWordTask(this, this);
		getRandomWordTask.execute();
		
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

	/*
	private void populateButtons() {
		String word = sourceWord.getRandomizedWord();
		int len = word.length();
		for (int i = 0; i < len; i++) {
		    char c = word.charAt(i);
		    randomedWordHolder.addView(createRandomedButton(c));
		}
	}
	*/
	
	private void populateButtons() {
		String word = sourceWord.getRandomizedWord();
		adapter = new ButtonsGridAdapter(this, word);
		buttonsGrid.setAdapter(adapter);
	}

	private View createRandomedButton(char c) {
		Button b = (Button) getLayoutInflater().inflate(R.layout.char_button, randomedWordHolder, false);
		b.setText(Character.toString(c));
		return b;
	}

	private void clearButtons() {
		userWordHolder.removeAllViews();
		randomedWordHolder.removeAllViews();
	}
}
