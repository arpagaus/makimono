
package au.edu.monash.csse.jmdict.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "links",
    "bibl",
    "etym",
    "audit"
})
@XmlRootElement(name = "info")
public class Info {

    protected List<Links> links;
    protected List<Bibl> bibl;
    protected List<Etym> etym;
    protected List<Audit> audit;

    /**
     * Gets the value of the links property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the links property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Links }
     * 
     * 
     */
    public List<Links> getLinks() {
        if (links == null) {
            links = new ArrayList<Links>();
        }
        return this.links;
    }

    /**
     * Gets the value of the bibl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bibl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBibl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Bibl }
     * 
     * 
     */
    public List<Bibl> getBibl() {
        if (bibl == null) {
            bibl = new ArrayList<Bibl>();
        }
        return this.bibl;
    }

    /**
     * Gets the value of the etym property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the etym property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEtym().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Etym }
     * 
     * 
     */
    public List<Etym> getEtym() {
        if (etym == null) {
            etym = new ArrayList<Etym>();
        }
        return this.etym;
    }

    /**
     * Gets the value of the audit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the audit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAudit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Audit }
     * 
     * 
     */
    public List<Audit> getAudit() {
        if (audit == null) {
            audit = new ArrayList<Audit>();
        }
        return this.audit;
    }

}
