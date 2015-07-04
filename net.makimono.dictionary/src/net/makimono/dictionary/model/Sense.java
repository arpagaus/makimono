package net.makimono.dictionary.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class Sense {

	private TreeSet<PartOfSpeech> partsOfSpeech = new TreeSet<PartOfSpeech>();
	private TreeSet<Miscellaneous> miscellaneous = new TreeSet<Miscellaneous>();
	private TreeSet<FieldOfApplication> fieldsOfApplication = new TreeSet<FieldOfApplication>();
	private TreeSet<Dialect> dialects = new TreeSet<Dialect>();

	private ArrayList<Meaning> meanings;

	public TreeSet<PartOfSpeech> getPartsOfSpeech() {
		return partsOfSpeech;
	}

	public TreeSet<Miscellaneous> getMiscellaneous() {
		return miscellaneous;
	}

	public TreeSet<FieldOfApplication> getFieldsOfApplication() {
		return fieldsOfApplication;
	}

	public TreeSet<Dialect> getDialects() {
		return dialects;
	}

	public List<Meaning> getMeanings(Language language) {
		List<Meaning> meanings = new ArrayList<Meaning>();
		for (Meaning meaning : getMeanings()) {
			if (meaning.getLanguage() == language) {
				meanings.add(meaning);
			}
		}
		return meanings;
	}

	public ArrayList<Meaning> getMeanings() {
		if (meanings == null) {
			meanings = new ArrayList<Meaning>();
		}
		return meanings;
	}

	public boolean hasMeaningsForLanguage(Collection<Language> languages) {
		for (Meaning meaning : getMeanings()) {
			if (languages.contains(meaning.getLanguage())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "partsOfSpeech=" + partsOfSpeech + ", miscellaneous=" + miscellaneous + ", fieldsOfApplication=" + fieldsOfApplication + ", dialects=" + dialects + ", meanings=" + meanings + "";
	}

	public ArrayList<String> getAdditionalInfo() {
		ArrayList<String> additionalInfo = new ArrayList<String>();
		for (PartOfSpeech p : getPartsOfSpeech()) {
			additionalInfo.add(p.name());
		}
		for (Miscellaneous m : getMiscellaneous()) {
			additionalInfo.add(m.name());
		}
		for (FieldOfApplication f : getFieldsOfApplication()) {
			additionalInfo.add(f.name());
		}
		for (Dialect d : getDialects()) {
			additionalInfo.add(d.name());
		}
		return additionalInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dialects == null) ? 0 : dialects.hashCode());
		result = prime * result + ((fieldsOfApplication == null) ? 0 : fieldsOfApplication.hashCode());
		result = prime * result + ((meanings == null) ? 0 : meanings.hashCode());
		result = prime * result + ((miscellaneous == null) ? 0 : miscellaneous.hashCode());
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
		if (fieldsOfApplication == null) {
			if (other.fieldsOfApplication != null)
				return false;
		} else if (!fieldsOfApplication.equals(other.fieldsOfApplication))
			return false;
		if (meanings == null) {
			if (other.meanings != null)
				return false;
		} else if (!meanings.equals(other.meanings))
			return false;
		if (miscellaneous == null) {
			if (other.miscellaneous != null)
				return false;
		} else if (!miscellaneous.equals(other.miscellaneous))
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

		outputStream.writeByte(sense.getMiscellaneous().size());
		for (Miscellaneous m : sense.getMiscellaneous()) {
			outputStream.writeByte(m.ordinal());
		}

		outputStream.writeByte(sense.getFieldsOfApplication().size());
		for (FieldOfApplication f : sense.getFieldsOfApplication()) {
			outputStream.writeByte(f.ordinal());
		}

		outputStream.writeByte(sense.getMeanings().size());
		for (Meaning g : sense.getMeanings()) {
			outputStream.writeByte(g.getLanguage().ordinal());
			outputStream.writeUTF(g.getValue());
		}
	}

	public static Sense readSense(ObjectInputStream inputStream) throws IOException {
		Sense sense = new Sense();

		byte partOfSpeechCount = inputStream.readByte();
		for (byte i = 0; i < partOfSpeechCount; i++) {
			sense.getPartsOfSpeech().add(PartOfSpeech.values()[inputStream.readByte()]);
		}

		byte dialectCount = inputStream.readByte();
		for (byte i = 0; i < dialectCount; i++) {
			sense.getDialects().add(Dialect.values()[inputStream.readByte()]);
		}

		byte miscellaneousCount = inputStream.readByte();
		for (byte i = 0; i < miscellaneousCount; i++) {
			sense.getMiscellaneous().add(Miscellaneous.values()[inputStream.readByte()]);
		}

		byte fieldOfApplicationCount = inputStream.readByte();
		for (byte i = 0; i < fieldOfApplicationCount; i++) {
			sense.getFieldsOfApplication().add(FieldOfApplication.values()[inputStream.readByte()]);
		}

		byte meaningCount = inputStream.readByte();
		for (byte i = 0; i < meaningCount; i++) {
			Meaning g = new Meaning();
			g.setLanguage(Language.values()[inputStream.readByte()]);
			g.setValue(inputStream.readUTF());
			sense.getMeanings().add(g);
		}
		return sense;
	}
}
