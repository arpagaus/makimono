package net.makimono.dictionary.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class ExampleEntry implements Parcelable, Entry {

	private Map<Language, String> sentences = new HashMap<Language, String>(Language.values().length);

	private List<Meaning> meanings;

	public List<Meaning> getMeanings() {
		if (meanings == null) {
			meanings = new ArrayList<Meaning>();
		}
		return meanings;
	}

	public Meaning getMeaning(Language language) {
		for (Meaning m : getMeanings()) {
			if (m.getLanguage() == language) {
				return m;
			}
		}
		return null;
	}

	public Meaning getJapaneseMeaning() {
		return getMeaning(Language.ja);
	}

	@Override
	public String getExpression() {
		return sentences.get(Language.ja);
	}

	@Override
	public String getReadingSummary() {
		return null;
	}

	@Override
	public String getMeaningSummary(List<Language> languages) {
		String[] meanings = new String[languages.size()];
		for (int i = 0; i < meanings.length; i++) {
			meanings[i] = sentences.get(languages.get(i));
		}
		return StringUtils.join(meanings, "\n");
	}

	@Override
	public String toString() {
		return "ExampleEntry [sentences=" + sentences + "]";
	}

	public static final Parcelable.Creator<ExampleEntry> CREATOR = new Parcelable.Creator<ExampleEntry>() {
		public ExampleEntry createFromParcel(Parcel parcel) {
			ExampleEntry entry = new ExampleEntry();
			entry.readFromParcel(parcel);
			return entry;
		}

		public ExampleEntry[] newArray(int size) {
			return new ExampleEntry[size];
		}
	};

	private void readFromParcel(Parcel parcel) {
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
		parcel.writeByte((byte) getMeanings().size());
		for (Meaning m : getMeanings()) {
			parcel.writeString(m.getValue());
			parcel.writeByte((byte) m.getLanguage().ordinal());
		}
	}
}
