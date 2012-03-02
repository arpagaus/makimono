package net.makimono.model;

import java.util.ArrayList;

public class KanjiEntry {

	private String literal;
	private int codePoint;
	private short radical;
	private byte strokeCount;
	private ArrayList<String> onYomi;
	private ArrayList<String> kunYomi;
	private ArrayList<Meaning> meanings;

	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}

	public int getCodePoint() {
		return codePoint;
	}

	public void setCodePoint(int codePoint) {
		this.codePoint = codePoint;
	}

	public short getRadical() {
		return radical;
	}

	public void setRadical(short radical) {
		this.radical = radical;
	}

	public byte getStrokeCount() {
		return strokeCount;
	}

	public void setStrokeCount(byte strokeCount) {
		this.strokeCount = strokeCount;
	}

	public ArrayList<String> getOnYomi() {
		if (onYomi == null) {
			onYomi = new ArrayList<String>();
		}
		return onYomi;
	}

	public void setOnYomi(ArrayList<String> onYomi) {
		this.onYomi = onYomi;
	}

	public ArrayList<String> getKunYomi() {
		if (kunYomi == null) {
			kunYomi = new ArrayList<String>();

		}
		return kunYomi;
	}

	public void setKunYomi(ArrayList<String> kunYomi) {
		this.kunYomi = kunYomi;
	}

	public ArrayList<Meaning> getMeanings() {
		if (meanings == null) {
			meanings = new ArrayList<Meaning>();
		}
		return meanings;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codePoint;
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
		KanjiEntry other = (KanjiEntry) obj;
		if (codePoint != other.codePoint)
			return false;
		return true;
	}
}
