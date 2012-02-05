package net.makimono.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Entry {
	private int id;
	transient private int docId;
	private ArrayList<String> expressions;
	private ArrayList<String> readings;
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

	public ArrayList<Sense> getSenses() {
		if (senses == null) {
			senses = new ArrayList<Sense>();
		}
		return senses;
	}

	@Override
	public String toString() {
		return "id=" + id + "\nexpressions=" + expressions + "\nreadings=" + readings + "\nsenses=" + senses + "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expressions == null) ? 0 : expressions.hashCode());
		result = prime * result + id;
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
		Entry other = (Entry) obj;
		if (expressions == null) {
			if (other.expressions != null)
				return false;
		} else if (!expressions.equals(other.expressions))
			return false;
		if (id != other.id)
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

	public static void writeEntry(ObjectOutputStream outputStream, Entry entry) throws IOException {
		outputStream.writeInt(entry.getId());

		outputStream.writeByte(entry.getExpressions().size());
		for (String s : entry.getExpressions()) {
			outputStream.writeUTF(s);
		}

		outputStream.writeByte(entry.getReadings().size());
		for (String s : entry.getReadings()) {
			outputStream.writeUTF(s);
		}

		outputStream.writeByte(entry.getSenses().size());
		for (Sense s : entry.getSenses()) {
			Sense.writeSense(outputStream, s);
		}
		
		outputStream.flush();
	}

	public static Entry readEntry(ObjectInputStream inputStream) throws IOException {
		Entry entry = new Entry();
		entry.id = inputStream.readInt();

		entry.expressions = readStringList(inputStream);
		entry.readings = readStringList(inputStream);

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
