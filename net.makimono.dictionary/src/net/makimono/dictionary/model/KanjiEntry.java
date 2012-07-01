package net.makimono.dictionary.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class KanjiEntry implements Parcelable, Entry {

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
	private SortedSet<String> radicals;
	private byte strokeCount;
	private List<String> onYomi;
	private List<String> kunYomi;
	private List<String> nanori;
	private List<String> pinyin;
	private List<String> hangul;
	private List<Meaning> meanings;
	private List<String> strokePaths;

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
			return ' ';
		}
	}

	public String getRadicalKana() {
		try {
			return RADICAL_KANA.get(String.valueOf(getRadical())).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setRadical(short radical) {
		this.radical = radical;
	}

	public SortedSet<String> getRadicals() {
		if (radicals == null) {
			radicals = new TreeSet<String>();
		}
		return radicals;
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

	public List<String> getKunYomi() {
		if (kunYomi == null) {
			kunYomi = new ArrayList<String>();
		}
		return kunYomi;
	}

	public List<String> getNanori() {
		if (nanori == null) {
			nanori = new ArrayList<String>();

		}
		return nanori;
	}

	public List<String> getPinyin() {
		if (pinyin == null) {
			pinyin = new ArrayList<String>();
		}
		return pinyin;
	}

	public List<String> getHangul() {
		if (hangul == null) {
			hangul = new ArrayList<String>();
		}
		return hangul;
	}

	public List<Meaning> getMeanings() {
		if (meanings == null) {
			meanings = new ArrayList<Meaning>();
		}
		return meanings;
	}

	public List<String> getStrokePaths() {
		if (strokePaths == null) {
			strokePaths = new ArrayList<String>();
		}
		return strokePaths;
	}

	@Override
	public String getExpression() {
		return getLiteral();
	}

	@Override
	public String getReadingSummary() {
		String kunYomi = StringUtils.join(getKunYomi(), ", ");
		String onYomi = StringUtils.join(getOnYomi(), ", ");
		String separator = onYomi.length() > 0 && kunYomi.length() > 0 ? " / " : "";
		return kunYomi + separator + onYomi;
	}

	@Override
	public String getMeaningSummary(List<Language> languages) {
		StringBuilder meaning = new StringBuilder();
		for (Meaning g : getMeanings()) {
			if (languages.contains(g.getLanguage())) {
				if (meaning.length() > 0) {
					meaning.append(", ");
				}
				meaning.append(g.getValue());
			}
		}
		return meaning.toString();
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

	@Override
	public String toString() {
		return "KanjiEntry [literal=" + literal + ", codePoint=" + codePoint + ", jlpt=" + jlpt + ", grade=" + grade + ", frequency=" + frequency + ", radical=" + radical + ", radicals=" + radicals
				+ ", strokeCount=" + strokeCount + ", onYomi=" + onYomi + ", kunYomi=" + kunYomi + ", nanori=" + nanori + ", pinyin=" + pinyin + ", hangul=" + hangul + ", meanings=" + meanings + "]";
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
		nanori = Arrays.asList((String[]) parcel.readValue(null));
		hangul = Arrays.asList((String[]) parcel.readValue(null));
		pinyin = Arrays.asList((String[]) parcel.readValue(null));

		byte meaningCount = parcel.readByte();
		for (byte i = 0; i < meaningCount; i++) {
			getMeanings().add(new Meaning(parcel.readString(), Language.values()[parcel.readByte()]));
		}

		strokePaths = Arrays.asList((String[]) parcel.readValue(null));
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

		parcel.writeValue(getOnYomi().toArray(new String[getOnYomi().size()]));
		parcel.writeValue(getKunYomi().toArray(new String[getKunYomi().size()]));
		parcel.writeValue(getNanori().toArray(new String[getNanori().size()]));
		parcel.writeValue(getHangul().toArray(new String[getHangul().size()]));
		parcel.writeValue(getPinyin().toArray(new String[getPinyin().size()]));

		parcel.writeByte((byte) getMeanings().size());
		for (Meaning m : getMeanings()) {
			parcel.writeString(m.getValue());
			parcel.writeByte((byte) m.getLanguage().ordinal());
		}

		parcel.writeValue(getStrokePaths().toArray(new String[getStrokePaths().size()]));
	}

}
