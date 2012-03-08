package net.makimono.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import android.os.Parcel;
import android.os.Parcelable;

public class KanjiEntry implements Parcelable {

	private static final Properties RADICAL_KANA = new Properties();
	private static final Properties RADICAL_KANJI = new Properties();

	static {
		ClassLoader classLoader = KanjiEntry.class.getClassLoader();
		try {
			String path = KanjiEntry.class.getName().replace('.', '/');
			RADICAL_KANA.load(classLoader.getResourceAsStream(path + "_radical_kana.properties"));
			RADICAL_KANJI.load(classLoader.getResourceAsStream(path + "_radical_kanji.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String literal;
	private int codePoint;
	private byte jlpt;
	private byte grade;
	private short frequency;
	private short radical;
	private byte strokeCount;
	private List<String> onYomi;
	private List<String> kunYomi;
	private List<Meaning> meanings;

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

	public byte getJlpt() {
		return jlpt;
	}

	public void setJlpt(byte jlpt) {
		this.jlpt = jlpt;
	}

	public byte getGrade() {
		return grade;
	}

	public void setGrade(byte grade) {
		this.grade = grade;
	}

	public short getFrequency() {
		return frequency;
	}

	public void setFrequency(short frequency) {
		this.frequency = frequency;
	}

	public short getRadical() {
		return radical;
	}

	public char getRadicalKanji() {
		try {
			return RADICAL_KANJI.get(String.valueOf(getRadical())).toString().charAt(0);
		} catch (Exception e) {
			e.printStackTrace();
			return 'E';
		}
	}

	public String getRadicalKana() {
		try {
			return RADICAL_KANA.get(String.valueOf(getRadical())).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Unexpected error";
		}
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

	public List<String> getOnYomi() {
		if (onYomi == null) {
			onYomi = new ArrayList<String>();
		}
		return onYomi;
	}

	public void setOnYomi(ArrayList<String> onYomi) {
		this.onYomi = onYomi;
	}

	public List<String> getKunYomi() {
		if (kunYomi == null) {
			kunYomi = new ArrayList<String>();

		}
		return kunYomi;
	}

	public void setKunYomi(ArrayList<String> kunYomi) {
		this.kunYomi = kunYomi;
	}

	public List<Meaning> getMeanings() {
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

	public static final Parcelable.Creator<KanjiEntry> CREATOR = new Parcelable.Creator<KanjiEntry>() {
		public KanjiEntry createFromParcel(Parcel parcel) {
			KanjiEntry entry = new KanjiEntry();
			entry.readFromParcel(parcel);
			return entry;
		}

		public KanjiEntry[] newArray(int size) {
			return new KanjiEntry[size];
		}
	};

	private void readFromParcel(Parcel parcel) {
		codePoint = parcel.readInt();
		literal = String.valueOf(Character.toChars(codePoint));
		jlpt = parcel.readByte();
		grade = parcel.readByte();
		frequency = (short) parcel.readInt();
		radical = (short) parcel.readInt();
		strokeCount = parcel.readByte();

		onYomi = Arrays.asList((String[]) parcel.readValue(null));
		kunYomi = Arrays.asList((String[]) parcel.readValue(null));

		byte meaningCount = parcel.readByte();
		for (byte i = 0; i < meaningCount; i++) {
			getMeanings().add(new Meaning(parcel.readString(), Language.values()[parcel.readByte()]));
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(codePoint);
		parcel.writeByte(jlpt);
		parcel.writeByte(grade);
		parcel.writeInt(frequency);
		parcel.writeInt(radical);
		parcel.writeByte(strokeCount);

		parcel.writeValue(onYomi.toArray(new String[onYomi.size()]));
		parcel.writeValue(kunYomi.toArray(new String[kunYomi.size()]));

		parcel.writeByte((byte) meanings.size());
		for (Meaning m : meanings) {
			parcel.writeString(m.getValue());
			parcel.writeByte((byte) m.getLanguage().ordinal());
		}
	}

}
