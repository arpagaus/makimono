package net.makimono.util;

import java.util.HashMap;
import java.util.Map;

import net.makimono.R;
import net.makimono.model.Language;

public class IconResolver {
	@SuppressWarnings("serial")
	private static final Map<Language, Integer> LANGUAGE_ICONS = new HashMap<Language, Integer>() {
		{
			put(Language.en, R.drawable.ic_english);
			put(Language.de, R.drawable.ic_german);
			put(Language.fr, R.drawable.ic_french);
			put(Language.ru, R.drawable.ic_russian);
			put(Language.es, R.drawable.ic_spanish);
			put(Language.pt, R.drawable.ic_portugese);
		}
	};

	public static int resolveIcon(Language language) {
		return LANGUAGE_ICONS.get(language);
	}
}
