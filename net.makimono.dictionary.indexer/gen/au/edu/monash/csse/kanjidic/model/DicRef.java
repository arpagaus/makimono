
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
 *       &lt;attribute name="m_vol">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}byte">
 *             &lt;enumeration value="0"/>
 *             &lt;enumeration value="1"/>
 *             &lt;enumeration value="10"/>
 *             &lt;enumeration value="11"/>
 *             &lt;enumeration value="12"/>
 *             &lt;enumeration value="13"/>
 *             &lt;enumeration value="2"/>
 *             &lt;enumeration value="3"/>
 *             &lt;enumeration value="4"/>
 *             &lt;enumeration value="5"/>
 *             &lt;enumeration value="6"/>
 *             &lt;enumeration value="7"/>
 *             &lt;enumeration value="8"/>
 *             &lt;enumeration value="9"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="m_page" type="{http://www.w3.org/2001/XMLSchema}short" />
 *       &lt;attribute name="dr_type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="busy_people"/>
 *             &lt;enumeration value="crowley"/>
 *             &lt;enumeration value="gakken"/>
 *             &lt;enumeration value="halpern_kkld"/>
 *             &lt;enumeration value="halpern_njecd"/>
 *             &lt;enumeration value="heisig"/>
 *             &lt;enumeration value="henshall"/>
 *             &lt;enumeration value="henshall3"/>
 *             &lt;enumeration value="jf_cards"/>
 *             &lt;enumeration value="kanji_in_context"/>
 *             &lt;enumeration value="kodansha_compact"/>
 *             &lt;enumeration value="moro"/>
 *             &lt;enumeration value="nelson_c"/>
 *             &lt;enumeration value="nelson_n"/>
 *             &lt;enumeration value="oneill_kk"/>
 *             &lt;enumeration value="oneill_names"/>
 *             &lt;enumeration value="sakade"/>
 *             &lt;enumeration value="sh_kk"/>
 *             &lt;enumeration value="tutt_cards"/>
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
@XmlRootElement(name = "dic_ref")
public class DicRef {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "m_vol")
    protected Byte mVol;
    @XmlAttribute(name = "m_page")
    protected Short mPage;
    @XmlAttribute(name = "dr_type", required = true)
    protected String drType;

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
     * Gets the value of the mVol property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getMVol() {
        return mVol;
    }

    /**
     * Sets the value of the mVol property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setMVol(Byte value) {
        this.mVol = value;
    }

    /**
     * Gets the value of the mPage property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getMPage() {
        return mPage;
    }

    /**
     * Sets the value of the mPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setMPage(Short value) {
        this.mPage = value;
    }

    /**
     * Gets the value of the drType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrType() {
        return drType;
    }

    /**
     * Sets the value of the drType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrType(String value) {
        this.drType = value;
    }

}
