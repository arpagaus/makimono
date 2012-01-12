package jiten.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Sense {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((glosses == null) ? 0 : glosses.hashCode());
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
		if (glosses == null) {
			if (other.glosses != null)
				return false;
		} else if (!glosses.equals(other.glosses))
			return false;
		return true;
	}

	public static void writeSense(ObjectOutputStream outputStream, Sense sense) throws IOException {
		outputStream.writeByte(sense.glosses.size());
		for (Gloss g : sense.glosses) {
			outputStream.writeByte(g.getLanguage().ordinal());
			outputStream.writeUTF(g.getValue());
		}
	}

	public static Sense readSense(ObjectInputStream inputStream) throws IOException {
		Sense sense = new Sense();
		byte glossCount = inputStream.readByte();
		for (int i = 0; i < glossCount; i++) {
			Gloss g = new Gloss();
			g.setLanguage(Language.values()[inputStream.readByte()]);
			g.setValue(inputStream.readUTF());
			sense.getGlosses().add(g);
		}
		return sense;
	}
}
