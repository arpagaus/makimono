package jiten.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Sense implements Serializable {
	private static final long serialVersionUID = -8953037595127299724L;

	private ArrayList<Gloss> glosses;

	public ArrayList<Gloss> getGlosses() {
		if (glosses == null) {
			glosses = new ArrayList<Gloss>();
		}
		return glosses;
	}

	@Override
	public String toString() {
		return "glosses=" + glosses + "";
	}

	public CharSequence getGlossString(Language langauge) {
		StringBuilder builder = new StringBuilder();
		for (Gloss g : glosses) {
			if (g.getLanguage() == langauge) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(g.getValue());
			}
		}
		return builder;
	}
}
