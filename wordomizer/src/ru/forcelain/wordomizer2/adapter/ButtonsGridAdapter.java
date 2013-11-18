package ru.forcelain.wordomizer2.adapter;

import ru.forcelain.wordomizer2.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class ButtonsGridAdapter extends BaseAdapter {
	
	private String word;
	private LayoutInflater layoutInflater;

	public ButtonsGridAdapter(Context context, String word){
		this.word = word;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return word.length();
	}

	@Override
	public Character getItem(int position) {
		return word.charAt(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.char_button, parent, false);
		}
		
		Button button = (Button) convertView;
		button.setText(Character.toString(getItem(position)));
		
		return button;
	}

}
