
package au.edu.monash.csse.kanjidic.model;

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
 *         &lt;element ref="{}literal"/>
 *         &lt;element ref="{}codepoint"/>
 *         &lt;element ref="{}radical"/>
 *         &lt;element ref="{}misc"/>
 *         &lt;element ref="{}dic_number"/>
 *         &lt;element ref="{}query_code"/>
 *         &lt;element ref="{}reading_meaning"/>
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
    "literal",
    "codepoint",
    "radical",
    "misc",
    "dicNumber",
    "queryCode",
    "readingMeaning"
})
@XmlRootElement(name = "character")
public class Character {

    @XmlElement(required = true)
    protected String literal;
    @XmlElement(required = true)
    protected Codepoint codepoint;
    @XmlElement(required = true)
    protected Radical radical;
    @XmlElement(required = true)
    protected Misc misc;
    @XmlElement(name = "dic_number", required = true)
    protected DicNumber dicNumber;
    @XmlElement(name = "query_code", required = true)
    protected QueryCode queryCode;
    @XmlElement(name = "reading_meaning", required = true)
    protected ReadingMeaning readingMeaning;

    /**
     * Gets the value of the literal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLiteral() {
        return literal;
    }

    /**
     * Sets the value of the literal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLiteral(String value) {
        this.literal = value;
    }

    /**
     * Gets the value of the codepoint property.
     * 
     * @return
     *     possible object is
     *     {@link Codepoint }
     *     
     */
    public Codepoint getCodepoint() {
        return codepoint;
    }

    /**
     * Sets the value of the codepoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link Codepoint }
     *     
     */
    public void setCodepoint(Codepoint value) {
        this.codepoint = value;
    }

    /**
     * Gets the value of the radical property.
     * 
     * @return
     *     possible object is
     *     {@link Radical }
     *     
     */
    public Radical getRadical() {
        return radical;
    }

    /**
     * Sets the value of the radical property.
     * 
     * @param value
     *     allowed object is
     *     {@link Radical }
     *     
     */
    public void setRadical(Radical value) {
        this.radical = value;
    }

    /**
     * Gets the value of the misc property.
     * 
     * @return
     *     possible object is
     *     {@link Misc }
     *     
     */
    public Misc getMisc() {
        return misc;
    }

    /**
     * Sets the value of the misc property.
     * 
     * @param value
     *     allowed object is
     *     {@link Misc }
     *     
     */
    public void setMisc(Misc value) {
        this.misc = value;
    }

    /**
     * Gets the value of the dicNumber property.
     * 
     * @return
     *     possible object is
     *     {@link DicNumber }
     *     
     */
    public DicNumber getDicNumber() {
        return dicNumber;
    }

    /**
     * Sets the value of the dicNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link DicNumber }
     *     
     */
    public void setDicNumber(DicNumber value) {
        this.dicNumber = value;
    }

    /**
     * Gets the value of the queryCode property.
     * 
     * @return
     *     possible object is
     *     {@link QueryCode }
     *     
     */
    public QueryCode getQueryCode() {
        return queryCode;
    }

    /**
     * Sets the value of the queryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryCode }
     *     
     */
    public void setQueryCode(QueryCode value) {
        this.queryCode = value;
    }

    /**
     * Gets the value of the readingMeaning property.
     * 
     * @return
     *     possible object is
     *     {@link ReadingMeaning }
     *     
     */
    public ReadingMeaning getReadingMeaning() {
        return readingMeaning;
    }

    /**
     * Sets the value of the readingMeaning property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadingMeaning }
     *     
     */
    public void setReadingMeaning(ReadingMeaning value) {
        this.readingMeaning = value;
    }

}
