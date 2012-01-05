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
	
	
}
