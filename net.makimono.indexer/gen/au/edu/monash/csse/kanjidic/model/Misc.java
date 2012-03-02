
package au.edu.monash.csse.kanjidic.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}grade" minOccurs="0"/>
 *         &lt;element ref="{}stroke_count" maxOccurs="unbounded"/>
 *         &lt;element ref="{}variant" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}freq"/>
 *         &lt;element ref="{}rad_name" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "grade",
    "strokeCount",
    "variant",
    "freq",
    "radName"
})
@XmlRootElement(name = "misc")
public class Misc {

    protected Byte grade;
    @XmlElement(name = "stroke_count", type = Byte.class)
    protected List<Byte> strokeCount;
    protected List<Variant> variant;
    protected short freq;
    @XmlElement(name = "rad_name")
    protected List<String> radName;

    /**
     * Gets the value of the grade property.
     * 
     * @return
     *     possible object is
     *     {@link Byte }
     *     
     */
    public Byte getGrade() {
        return grade;
    }

    /**
     * Sets the value of the grade property.
     * 
     * @param value
     *     allowed object is
     *     {@link Byte }
     *     
     */
    public void setGrade(Byte value) {
        this.grade = value;
    }

    /**
     * Gets the value of the strokeCount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the strokeCount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStrokeCount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Byte }
     * 
     * 
     */
    public List<Byte> getStrokeCount() {
        if (strokeCount == null) {
            strokeCount = new ArrayList<Byte>();
        }
        return this.strokeCount;
    }

    /**
     * Gets the value of the variant property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the variant property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVariant().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Variant }
     * 
     * 
     */
    public List<Variant> getVariant() {
        if (variant == null) {
            variant = new ArrayList<Variant>();
        }
        return this.variant;
    }

    /**
     * Gets the value of the freq property.
     * 
     */
    public short getFreq() {
        return freq;
    }

    /**
     * Sets the value of the freq property.
     * 
     */
    public void setFreq(short value) {
        this.freq = value;
    }

    /**
     * Gets the value of the radName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the radName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRadName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRadName() {
        if (radName == null) {
            radName = new ArrayList<String>();
        }
        return this.radName;
    }

}
