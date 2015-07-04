package net.makimono.dictionary.activity;

import java.util.EnumSet;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.widget.Toast;
import net.makimono.dictionary.R;
import net.makimono.dictionary.content.AbstractSearchSuggestionProvider;
import net.makimono.dictionary.model.Language;

public class PreferenceFragment extends android.preference.PreferenceFragment implements OnPreferenceClickListener {

	public static EnumSet<Language> getConfiguredLanguages(SharedPreferences preferences) {
		EnumSet<Language> languages = EnumSet.noneOf(Language.class);

		for (PreferenceEnum languageEnum : PreferenceEnum.getLanguageEnums()) {
			boolean enabled = preferences.getBoolean(languageEnum.key(), true);
			if (enabled) {
				String languageString = languageEnum.key().substring(languageEnum.key().lastIndexOf('_') + 1);
				Language language = Language.valueOf(languageString);
				languages.add(language);
			}
		}

		if (languages.isEmpty()) {
			languages.add(Language.getDefaultLanguage());
		}

		return languages;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		findPreference(PreferenceEnum.CLEAR_SEARCH_HISTORY.key()).setOnPreferenceClickListener(this);
		findPreference(PreferenceEnum.INDEX_FILES_VERSION_RESET.key()).setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(PreferenceEnum.CLEAR_SEARCH_HISTORY.key())) {
			AbstractSearchSuggestionProvider.clearHistory(getActivity());
			return true;
		}
		if (preference.getKey().equals(PreferenceEnum.INDEX_FILES_VERSION_RESET.key())) {
			Editor editor = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext()).edit();
			editor.putInt(PreferenceEnum.INDEX_FILES_VERSION.key(), 0);
			editor.commit();

			// FIXME rar
			// Intent intent = new Intent(this, HomeActivity.class);
			// startActivity(intent);
			Toast.makeText(getActivity(), "Not implemented", Toast.LENGTH_SHORT).show();

			return true;
		}
		return false;
	}
}
