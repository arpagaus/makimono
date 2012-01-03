
package au.edu.monash.csse.jmdict.model;

import java.io.Serializable;
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
    "bibTag",
    "bibTxt"
})
@XmlRootElement(name = "bibl")
public class Bibl
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "bib_tag")
    protected String bibTag;
    @XmlElement(name = "bib_txt")
    protected String bibTxt;

    /**
     * Gets the value of the bibTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBibTag() {
        return bibTag;
    }

    /**
     * Sets the value of the bibTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBibTag(String value) {
        this.bibTag = value;
    }

    /**
     * Gets the value of the bibTxt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBibTxt() {
        return bibTxt;
    }

    /**
     * Sets the value of the bibTxt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBibTxt(String value) {
        this.bibTxt = value;
    }

}
