package jiten.model;

import java.io.Serializable;

public class Gloss implements Serializable {
	private static final long serialVersionUID = 5870963060575331502L;
	
	private String value;
	private Language language;

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

}
