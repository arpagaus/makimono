package net.makimono.activity;

import java.util.ArrayList;
import java.util.List;

import net.makimono.R;
import net.makimono.content.AbstractSearchSuggestionProvider;
import net.makimono.model.Language;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

public class PreferenceActivity extends android.preference.PreferenceActivity implements OnPreferenceChangeListener, OnPreferenceClickListener {
	public static final String CLEAR_SEARCH_HISTORY = "clear_search_history";
	public static final String LANGUAGE_EN = "language_en";
	public static final String LANGUAGE_DE = "language_de";
	public static final String LANGUAGE_FR = "language_fr";
	public static final String LANGUAGE_RU = "language_ru";
	public static final String LANGUAGE_ES = "language_es";
	public static final String LANGUAGE_PT = "language_pt";

	public static List<Language> getConfiguredLanguages(SharedPreferences preferences) {
		ArrayList<Language> languages = new ArrayList<Language>();
		if (preferences.getBoolean(LANGUAGE_EN, true)) {
			languages.add(Language.en);
		}
		if (preferences.getBoolean(LANGUAGE_DE, true)) {
			languages.add(Language.de);
		}
		if (preferences.getBoolean(LANGUAGE_FR, true)) {
			languages.add(Language.fr);
		}
		if (preferences.getBoolean(LANGUAGE_RU, true)) {
			languages.add(Language.ru);
		}
		if (preferences.getBoolean(LANGUAGE_ES, true)) {
			languages.add(Language.es);
		}
		if (preferences.getBoolean(LANGUAGE_PT, true)) {
			languages.add(Language.pt);
		}
		return languages.isEmpty() ? Language.getDefaultLanguage() : languages;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		findPreference(CLEAR_SEARCH_HISTORY).setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(CLEAR_SEARCH_HISTORY)) {
			AbstractSearchSuggestionProvider.clearHistory(this);
			return true;
		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
}
