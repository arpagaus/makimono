package net.makimono.model;

import java.util.Collection;

public class Gloss {
	private String value;
	private Language language;

	public Gloss() {
	}

	public Gloss(String value, Language language) {
		this.value = value;
		this.language = language;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gloss other = (Gloss) obj;
		if (language != other.language)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public static CharSequence getGlossString(Language language, Collection<Gloss> glosses) {
		StringBuilder builder = new StringBuilder();
		for (Gloss g : glosses) {
			if (g.getLanguage() == language) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(g.getValue());
			}
		}
		return builder;
	}

}
