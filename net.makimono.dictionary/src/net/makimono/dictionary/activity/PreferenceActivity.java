package net.makimono.dictionary.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import net.makimono.dictionary.R;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.model.Language;

public class PreferenceActivity extends android.preference.PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {

	// TODO use EnumSet<Langauage> instead of List<Langauage>
	public static List<Language> getConfiguredLanguages(SharedPreferences preferences) {
		ArrayList<Language> languages = new ArrayList<Language>();

		for (PreferenceEnum languageEnum : PreferenceEnum.getLanguageEnums()) {
			boolean enabled = preferences.getBoolean(languageEnum.key(), true);
			if (enabled) {
				String languageString = languageEnum.key().substring(languageEnum.key().lastIndexOf('_') + 1);
				Language language = Language.valueOf(languageString);
				languages.add(language);
			}
		}

		return languages.isEmpty() ? Collections.singletonList(Language.getDefaultLanguage()) : languages;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		findPreference(PreferenceEnum.CLEAR_SEARCH_HISTORY.key()).setOnPreferenceClickListener(this);
		findPreference(PreferenceEnum.INDEX_FILES_VERSION_RESET.key()).setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(PreferenceEnum.CLEAR_SEARCH_HISTORY.key())) {
			AbstractSearchSuggestionProvider.clearHistory(this);
			return true;
		}
		if (preference.getKey().equals(PreferenceEnum.INDEX_FILES_VERSION_RESET.key())) {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).edit();
			editor.putInt(PreferenceEnum.INDEX_FILES_VERSION.key(), 0);
			editor.commit();

			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);

			return true;
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
}
