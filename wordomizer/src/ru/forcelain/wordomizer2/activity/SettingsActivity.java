package ru.forcelain.wordomizer2.activity;

import ru.forcelain.wordomizer2.R;
import ru.forcelain.wordomizer2.utils.PrefUtils;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setupSimplePreferencesScreen();
	}

	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		addPreferencesFromResource(R.xml.pref_general);
		final CheckBoxPreference rusPref = (CheckBoxPreference) findPreference("rus_words_enabled");
		final CheckBoxPreference engPref = (CheckBoxPreference) findPreference("eng_words_enabled");
		rusPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (!PrefUtils.isEngWordsEnabled(preference.getContext()) && !PrefUtils.isRusWordsEnabled(preference.getContext())){
					PrefUtils.setRusWordsEnabled(true, preference.getContext());
					rusPref.setChecked(true);
				}
				return false;
			}
		});
		engPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (!PrefUtils.isEngWordsEnabled(preference.getContext()) && !PrefUtils.isRusWordsEnabled(preference.getContext())){
					PrefUtils.setEngWordsEnabled(true, preference.getContext());
					engPref.setChecked(true);
				}
				return false;
			}
		});
	}
}
