
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
 *       &lt;attribute name="var_type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="deroo"/>
 *             &lt;enumeration value="jis208"/>
 *             &lt;enumeration value="jis212"/>
 *             &lt;enumeration value="nelson_c"/>
 *             &lt;enumeration value="njecd"/>
 *             &lt;enumeration value="oneill"/>
 *             &lt;enumeration value="s_h"/>
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
@XmlRootElement(name = "variant")
public class Variant {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "var_type", required = true)
    protected String varType;

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
     * Gets the value of the varType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVarType() {
        return varType;
    }

    /**
     * Sets the value of the varType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVarType(String value) {
        this.varType = value;
    }

}
