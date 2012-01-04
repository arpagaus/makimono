package jiten.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Entry implements Serializable {
	private static final long serialVersionUID = 6769638866236499483L;

	private ArrayList<String> expressions;
	private ArrayList<String> readings;
	private ArrayList<Sense> senses;

	public ArrayList<String> getExpressions() {
		if (expressions == null) {
			expressions = new ArrayList<String>();
		}
		return expressions;
	}

	public ArrayList<String> getReadings() {
		if (readings == null) {
			readings = new ArrayList<String>();
		}
		return readings;
	}

	public ArrayList<Sense> getSenses() {
		if (senses == null) {
			senses = new ArrayList<Sense>();
		}
		return senses;
	}
}
