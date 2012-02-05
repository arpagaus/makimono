
package au.edu.monash.csse.jmdict.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stagk",
    "stagr",
    "pos",
    "xref",
    "ant",
    "field",
    "misc",
    "sInf",
    "lsource",
    "dial",
    "gloss",
    "example"
})
@XmlRootElement(name = "sense")
public class Sense {

    protected List<Stagk> stagk;
    protected List<Stagr> stagr;
    protected List<Pos> pos;
    protected List<Xref> xref;
    protected List<Ant> ant;
    protected List<Field> field;
    protected List<Misc> misc;
    @XmlElement(name = "s_inf")
    protected List<SInf> sInf;
    protected List<Lsource> lsource;
    protected List<Dial> dial;
    protected List<Gloss> gloss;
    protected List<Example> example;

    /**
     * Gets the value of the stagk property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stagk property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStagk().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Stagk }
     * 
     * 
     */
    public List<Stagk> getStagk() {
        if (stagk == null) {
            stagk = new ArrayList<Stagk>();
        }
        return this.stagk;
    }

    /**
     * Gets the value of the stagr property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stagr property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStagr().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Stagr }
     * 
     * 
     */
    public List<Stagr> getStagr() {
        if (stagr == null) {
            stagr = new ArrayList<Stagr>();
        }
        return this.stagr;
    }

    /**
     * Gets the value of the pos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pos }
     * 
     * 
     */
    public List<Pos> getPos() {
        if (pos == null) {
            pos = new ArrayList<Pos>();
        }
        return this.pos;
    }

    /**
     * Gets the value of the xref property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xref property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXref().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Xref }
     * 
     * 
     */
    public List<Xref> getXref() {
        if (xref == null) {
            xref = new ArrayList<Xref>();
        }
        return this.xref;
    }

    /**
     * Gets the value of the ant property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ant property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Ant }
     * 
     * 
     */
    public List<Ant> getAnt() {
        if (ant == null) {
            ant = new ArrayList<Ant>();
        }
        return this.ant;
    }

    /**
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Field }
     * 
     * 
     */
    public List<Field> getField() {
        if (field == null) {
            field = new ArrayList<Field>();
        }
        return this.field;
    }

    /**
     * Gets the value of the misc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the misc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMisc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Misc }
     * 
     * 
     */
    public List<Misc> getMisc() {
        if (misc == null) {
            misc = new ArrayList<Misc>();
        }
        return this.misc;
    }

    /**
     * Gets the value of the sInf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sInf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSInf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SInf }
     * 
     * 
     */
    public List<SInf> getSInf() {
        if (sInf == null) {
            sInf = new ArrayList<SInf>();
        }
        return this.sInf;
    }

    /**
     * Gets the value of the lsource property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lsource property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLsource().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Lsource }
     * 
     * 
     */
    public List<Lsource> getLsource() {
        if (lsource == null) {
            lsource = new ArrayList<Lsource>();
        }
        return this.lsource;
    }

    /**
     * Gets the value of the dial property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dial property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDial().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dial }
     * 
     * 
     */
    public List<Dial> getDial() {
        if (dial == null) {
            dial = new ArrayList<Dial>();
        }
        return this.dial;
    }

    /**
     * Gets the value of the gloss property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gloss property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGloss().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Gloss }
     * 
     * 
     */
    public List<Gloss> getGloss() {
        if (gloss == null) {
            gloss = new ArrayList<Gloss>();
        }
        return this.gloss;
    }

    /**
     * Gets the value of the example property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the example property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExample().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Example }
     * 
     * 
     */
    public List<Example> getExample() {
        if (example == null) {
            example = new ArrayList<Example>();
        }
        return this.example;
    }

}
