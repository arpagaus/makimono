
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
 *       &lt;attribute name="skip_misclass">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="posn"/>
 *             &lt;enumeration value="stroke_and_posn"/>
 *             &lt;enumeration value="stroke_count"/>
 *             &lt;enumeration value="stroke_diff"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="qc_type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="deroo"/>
 *             &lt;enumeration value="four_corner"/>
 *             &lt;enumeration value="sh_desc"/>
 *             &lt;enumeration value="skip"/>
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
@XmlRootElement(name = "q_code")
public class QCode {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "skip_misclass")
    protected String skipMisclass;
    @XmlAttribute(name = "qc_type", required = true)
    protected String qcType;

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
     * Gets the value of the skipMisclass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkipMisclass() {
        return skipMisclass;
    }

    /**
     * Sets the value of the skipMisclass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkipMisclass(String value) {
        this.skipMisclass = value;
    }

    /**
     * Gets the value of the qcType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQcType() {
        return qcType;
    }

    /**
     * Sets the value of the qcType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQcType(String value) {
        this.qcType = value;
    }

}
