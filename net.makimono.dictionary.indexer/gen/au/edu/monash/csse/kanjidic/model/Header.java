
package au.edu.monash.csse.kanjidic.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element ref="{}file_version"/>
 *         &lt;element ref="{}database_version"/>
 *         &lt;element ref="{}date_of_creation"/>
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
    "fileVersion",
    "databaseVersion",
    "dateOfCreation"
})
@XmlRootElement(name = "header")
public class Header {

    @XmlElement(name = "file_version")
    protected byte fileVersion;
    @XmlElement(name = "database_version", required = true)
    protected String databaseVersion;
    @XmlElement(name = "date_of_creation", required = true)
    protected XMLGregorianCalendar dateOfCreation;

    /**
     * Gets the value of the fileVersion property.
     * 
     */
    public byte getFileVersion() {
        return fileVersion;
    }

    /**
     * Sets the value of the fileVersion property.
     * 
     */
    public void setFileVersion(byte value) {
        this.fileVersion = value;
    }

    /**
     * Gets the value of the databaseVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatabaseVersion() {
        return databaseVersion;
    }

    /**
     * Sets the value of the databaseVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatabaseVersion(String value) {
        this.databaseVersion = value;
    }

    /**
     * Gets the value of the dateOfCreation property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfCreation() {
        return dateOfCreation;
    }

    /**
     * Sets the value of the dateOfCreation property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfCreation(XMLGregorianCalendar value) {
        this.dateOfCreation = value;
    }

}
