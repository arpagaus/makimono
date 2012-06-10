package net.makimono.dictionary.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class DictionaryEntry implements Entry {
	private int id;
	transient private int docId;
	private ArrayList<String> expressions;
	private ArrayList<String> readings;
	private Map<Integer, Set<Integer>> readingRestrictions;
	private ArrayList<Sense> senses;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

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

	public ArrayList<String> getReadings(String expression) {
		int expressionIndex = getExpressions().indexOf(expression);
		ArrayList<String> readignsForExpression = new ArrayList<String>(getReadings());
		for (Map.Entry<Integer, Set<Integer>> e : getReadingRestrictions().entrySet()) {
			Set<Integer> set = e.getValue();
			if (!set.isEmpty() && !set.contains(expressionIndex)) {
				readignsForExpression.remove(getReadings().get(e.getKey()));
			}
		}

		return readignsForExpression;
	}

	public void addReadingRestriction(int readingIndex, int expressionIndex) {
		getReadingRestrictions().get(readingIndex).add(expressionIndex);
	}

	@SuppressWarnings("serial")
	public Map<Integer, Set<Integer>> getReadingRestrictions() {
		if (readingRestrictions == null) {
			readingRestrictions = new HashMap<Integer, Set<Integer>>() {
				@Override
				public Set<Integer> get(Object key) {
					Set<Integer> set = super.get(key);
					if (set == null) {
						set = new HashSet<Integer>();
						put((Integer) key, set);
					}
					return set;
				}
			};
		}
		return readingRestrictions;
	}

	public ArrayList<Sense> getSenses() {
		if (senses == null) {
			senses = new ArrayList<Sense>();
		}
		return senses;
	}

	@Override
	public String getExpression() {
		if (getExpressions().isEmpty()) {
			return getReadings().get(0);
		} else {
			return getExpressions().get(0);
		}
	}

	@Override
	public String getReadingSummary() {
		if (!getExpressions().isEmpty()) {
			return StringUtils.join(getReadings(), ", ");
		} else {
			return null;
		}
	}

	@Override
	public String getMeaningSummary(List<Language> languages) {
		List<Meaning> meanings = new ArrayList<Meaning>();
		for (Language lang : languages) {
			for (Sense sense : getSenses()) {
				meanings.addAll(sense.getMeanings(lang));
			}
		}
		return StringUtils.join(meanings, ", ");
	}

	@Override
	public String toString() {
		return "id=" + id + "\nexpressions=" + expressions + "\nreadings=" + readings + "\nreadingRestrictions=" + readingRestrictions + "\nsenses=" + senses;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expressions == null) ? 0 : expressions.hashCode());
		result = prime * result + id;
		result = prime * result + ((readingRestrictions == null) ? 0 : readingRestrictions.hashCode());
		result = prime * result + ((readings == null) ? 0 : readings.hashCode());
		result = prime * result + ((senses == null) ? 0 : senses.hashCode());
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
		DictionaryEntry other = (DictionaryEntry) obj;
		if (expressions == null) {
			if (other.expressions != null)
				return false;
		} else if (!expressions.equals(other.expressions))
			return false;
		if (id != other.id)
			return false;
		if (readingRestrictions == null) {
			if (other.readingRestrictions != null)
				return false;
		} else if (!readingRestrictions.equals(other.readingRestrictions))
			return false;
		if (readings == null) {
			if (other.readings != null)
				return false;
		} else if (!readings.equals(other.readings))
			return false;
		if (senses == null) {
			if (other.senses != null)
				return false;
		} else if (!senses.equals(other.senses))
			return false;
		return true;
	}

	public static void writeEntry(ObjectOutputStream outputStream, DictionaryEntry entry) throws IOException {
		outputStream.writeInt(entry.getId());

		outputStream.writeByte(entry.getExpressions().size());
		for (String s : entry.getExpressions()) {
			outputStream.writeUTF(s);
		}

		outputStream.writeByte(entry.getReadings().size());
		for (String s : entry.getReadings()) {
			outputStream.writeUTF(s);
		}

		outputStream.writeByte(entry.getReadingRestrictions().size());
		for (Map.Entry<Integer, Set<Integer>> e : entry.getReadingRestrictions().entrySet()) {
			outputStream.writeByte(e.getKey());
			outputStream.writeByte(e.getValue().size());
			for (Integer i : e.getValue()) {
				outputStream.writeByte(i);
			}
		}

		outputStream.writeByte(entry.getSenses().size());
		for (Sense s : entry.getSenses()) {
			Sense.writeSense(outputStream, s);
		}

		outputStream.flush();
	}

	public static DictionaryEntry readEntry(ObjectInputStream inputStream) throws IOException {
		DictionaryEntry entry = new DictionaryEntry();
		entry.id = inputStream.readInt();

		entry.expressions = readStringList(inputStream);
		entry.readings = readStringList(inputStream);

		byte restrictionCount = inputStream.readByte();
		for (int i = 0; i < restrictionCount; i++) {
			Set<Integer> set = entry.getReadingRestrictions().get((int) inputStream.readByte());
			byte readingCount = inputStream.readByte();
			for (int j = 0; j < readingCount; j++) {
				set.add((int) inputStream.readByte());
			}
		}

		byte senseCount = inputStream.readByte();
		for (int i = 0; i < senseCount; i++) {
			Sense s = Sense.readSense(inputStream);
			entry.getSenses().add(s);
		}

		return entry;
	}

	private static ArrayList<String> readStringList(ObjectInputStream inputStream) throws IOException {
		String[] expressions = new String[inputStream.readByte()];
		for (int i = 0; i < expressions.length; i++) {
			expressions[i] = inputStream.readUTF();
		}
		ArrayList<String> list = new ArrayList<String>(expressions.length);
		Collections.addAll(list, expressions);
		return list;
	}

}
