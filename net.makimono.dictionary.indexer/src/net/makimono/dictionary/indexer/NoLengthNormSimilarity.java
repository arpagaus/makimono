package net.makimono.dictionary.indexer;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.DefaultSimilarity;

@SuppressWarnings("serial")
public class NoLengthNormSimilarity extends DefaultSimilarity {
	@Override
	public float computeNorm(String field, FieldInvertState state) {
		return state.getBoost(); // Don't do length normalization
	}
}