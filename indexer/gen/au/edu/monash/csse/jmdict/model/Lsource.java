
package au.edu.monash.csse.jmdict.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "lsource")
public class Lsource {

    @XmlAttribute(name = "lang", namespace="http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String xmlLang;
    @XmlAttribute(name = "ls_type")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String lsType;
    @XmlAttribute(name = "ls_wasei")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String lsWasei;
    @XmlValue
    protected String value;

    /**
     * Gets the value of the xmlLang property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlLang() {
        if (xmlLang == null) {
            return "en";
        } else {
            return xmlLang;
        }
    }

    /**
     * Sets the value of the xmlLang property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlLang(String value) {
        this.xmlLang = value;
    }

    /**
     * Gets the value of the lsType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLsType() {
        return lsType;
    }

    /**
     * Sets the value of the lsType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLsType(String value) {
        this.lsType = value;
    }

    /**
     * Gets the value of the lsWasei property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLsWasei() {
        return lsWasei;
    }

    /**
     * Sets the value of the lsWasei property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLsWasei(String value) {
        this.lsWasei = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getvalue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setvalue(String value) {
        this.value = value;
    }

}
