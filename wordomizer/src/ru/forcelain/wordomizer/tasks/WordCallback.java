package ru.forcelain.wordomizer.tasks;

import ru.forcelain.wordomizer.model.Word;

public interface WordCallback {
	public void onWordReceived(Word word);
}
