package net.makimono.model;

/**
 * These enums are defined in the JMdict.dtd. They had to be transformed in
 * order to compile them:
 * <ol>
 * <li>Replace all dashes '-' with under scores '_'</li>
 * <li>Add the prefix 'JMdict_'</li>
 * </ol>
 */
public enum Dialect {
	JMdict_ksb, JMdict_ktb, JMdict_kyb, JMdict_kyu, JMdict_nab, JMdict_osb, JMdict_rkb, JMdict_thb, JMdict_tsb, JMdict_tsug
}
