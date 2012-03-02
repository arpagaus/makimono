
package au.edu.monash.csse.kanjidic.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="r_type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="ja_kun"/>
 *             &lt;enumeration value="ja_on"/>
 *             &lt;enumeration value="korean_h"/>
 *             &lt;enumeration value="korean_r"/>
 *             &lt;enumeration value="pinyin"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="r_status">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="jy"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="on_type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="go"/>
 *             &lt;enumeration value="kan"/>
 *             &lt;enumeration value="tou"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "reading")
public class Reading {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "r_type", required = true)
    protected String rType;
    @XmlAttribute(name = "r_status")
    protected String rStatus;
    @XmlAttribute(name = "on_type")
    protected String onType;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
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
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the rType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRType() {
        return rType;
    }

    /**
     * Sets the value of the rType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRType(String value) {
        this.rType = value;
    }

    /**
     * Gets the value of the rStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRStatus() {
        return rStatus;
    }

    /**
     * Sets the value of the rStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRStatus(String value) {
        this.rStatus = value;
    }

    /**
     * Gets the value of the onType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnType() {
        return onType;
    }

    /**
     * Sets the value of the onType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnType(String value) {
        this.onType = value;
    }

}
