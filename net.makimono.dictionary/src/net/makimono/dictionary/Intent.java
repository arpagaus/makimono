package net.makimono.dictionary;

public interface Intent {

	String EXTRA_STROKE_PATHS = Intent.class.getName() + ".EXTRA_STROKE_PATHS";

	String ACTION_RADICAL_SEARCH = Intent.class.getName() + ".ACTION_RADICAL_SEARCH";
	String EXTRA_MIN_STROKES = Intent.class.getName() + ".EXTRA_MIN_STROKES";
	String EXTRA_MAX_STROKES = Intent.class.getName() + ".EXTRA_MAX_STROKES";
	String EXTRA_RADICALS = Intent.class.getName() + ".EXTRA_RADICALS";

}
