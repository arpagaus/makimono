package jiten.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Sense {

	private ArrayList<PartOfSpeech> partsOfSpeech;
	private ArrayList<Dialect> dialects;
	private ArrayList<Gloss> glosses;

	public ArrayList<PartOfSpeech> getPartsOfSpeech() {
		if (partsOfSpeech == null) {
			partsOfSpeech = new ArrayList<PartOfSpeech>();

		}
		return partsOfSpeech;
	}

	public ArrayList<Dialect> getDialects() {
		if (dialects == null) {
			dialects = new ArrayList<Dialect>();
		}
		return dialects;
	}

	public ArrayList<Gloss> getGlosses() {
		if (glosses == null) {
			glosses = new ArrayList<Gloss>();
		}
		return glosses;
	}

	@Override
	public String toString() {
		return "partsOfSpeech=" + partsOfSpeech + ", dialects=" + dialects + ", glosses=" + glosses + "";
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dialects == null) ? 0 : dialects.hashCode());
		result = prime * result + ((glosses == null) ? 0 : glosses.hashCode());
		result = prime * result + ((partsOfSpeech == null) ? 0 : partsOfSpeech.hashCode());
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
		Sense other = (Sense) obj;
		if (dialects == null) {
			if (other.dialects != null)
				return false;
		} else if (!dialects.equals(other.dialects))
			return false;
		if (glosses == null) {
			if (other.glosses != null)
				return false;
		} else if (!glosses.equals(other.glosses))
			return false;
		if (partsOfSpeech == null) {
			if (other.partsOfSpeech != null)
				return false;
		} else if (!partsOfSpeech.equals(other.partsOfSpeech))
			return false;
		return true;
	}

	public static void writeSense(ObjectOutputStream outputStream, Sense sense) throws IOException {
		outputStream.writeByte(sense.getPartsOfSpeech().size());
		for (PartOfSpeech p : sense.getPartsOfSpeech()) {
			outputStream.writeByte(p.ordinal());
		}

		outputStream.writeByte(sense.getDialects().size());
		for (Dialect d : sense.getDialects()) {
			outputStream.writeByte(d.ordinal());
		}

		outputStream.writeByte(sense.getGlosses().size());
		for (Gloss g : sense.getGlosses()) {
			outputStream.writeByte(g.getLanguage().ordinal());
			outputStream.writeUTF(g.getValue());
		}
	}

	public static Sense readSense(ObjectInputStream inputStream) throws IOException {
		Sense sense = new Sense();

		byte partOfSpeechCount = inputStream.readByte();
		for (int i = 0; i < partOfSpeechCount; i++) {
			sense.getPartsOfSpeech().add(PartOfSpeech.values()[inputStream.readByte()]);
		}

		byte dialectCount = inputStream.readByte();
		for (int i = 0; i < dialectCount; i++) {
			sense.getDialects().add(Dialect.values()[inputStream.readByte()]);
		}

		byte glossCount = inputStream.readByte();
		;
		for (int i = 0; i < glossCount; i++) {
			Gloss g = new Gloss();
			g.setLanguage(Language.values()[inputStream.readByte()]);
			g.setValue(inputStream.readUTF());
			sense.getGlosses().add(g);
		}
		return sense;
	}
}
