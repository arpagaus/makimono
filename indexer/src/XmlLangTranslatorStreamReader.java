import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;

public class XmlLangTranslatorStreamReader extends StreamReaderDelegate {

	private final Map<String, Locale> LANG_TRANSLATIONS;

	public XmlLangTranslatorStreamReader(XMLStreamReader reader) {
		super(reader);

		Map<String, Locale> translations = new HashMap<String, Locale>();
		for (String lang : Locale.getISOLanguages()) {
			Locale locale = new Locale(lang);
			translations.put(locale.getISO3Language(), locale);
		}
		translations.put("eng", new Locale("en"));
		translations.put("ger", new Locale("de"));
		translations.put("fre", new Locale("fr"));
		translations.put("rus", new Locale("ru"));
		LANG_TRANSLATIONS = Collections.unmodifiableMap(translations);
	}

	@Override
	public String getAttributeValue(int index) {
		String attributeValue = super.getAttributeValue(index);
		if (LANG_TRANSLATIONS.containsKey(attributeValue)) {
			return LANG_TRANSLATIONS.get(attributeValue).getLanguage();
		}
		return attributeValue;
	}

}
