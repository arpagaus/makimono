package jiten.model;

/**
 * These enums are defined in the JMdict.dtd. They had to be transformed in
 * order to compile them:
 * <ol>
 * <li>Replace all dashes '-' with under scores '_'</li>
 * <li>Add the prefix 'JMdict_'</li>
 * </ol>
 */
public enum FieldOfApplication {
	JMdict_Buddh, JMdict_MA, JMdict_chem, JMdict_comp, JMdict_food, JMdict_geom, JMdict_ling, JMdict_math, JMdict_mil, JMdict_physics
}
