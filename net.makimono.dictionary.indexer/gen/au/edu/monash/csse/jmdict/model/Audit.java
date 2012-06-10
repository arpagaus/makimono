
package au.edu.monash.csse.jmdict.model;

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
    "updDate",
    "updDetl"
})
@XmlRootElement(name = "audit")
public class Audit {

    @XmlElement(name = "upd_date", required = true)
    protected String updDate;
    @XmlElement(name = "upd_detl", required = true)
    protected String updDetl;

    /**
     * Gets the value of the updDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdDate() {
        return updDate;
    }

    /**
     * Sets the value of the updDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdDate(String value) {
        this.updDate = value;
    }

    /**
     * Gets the value of the updDetl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdDetl() {
        return updDetl;
    }

    /**
     * Sets the value of the updDetl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdDetl(String value) {
        this.updDetl = value;
    }

}
