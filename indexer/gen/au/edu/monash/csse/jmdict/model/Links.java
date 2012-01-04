
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
    "linkTag",
    "linkDesc",
    "linkUri"
})
@XmlRootElement(name = "links")
public class Links {

    @XmlElement(name = "link_tag", required = true)
    protected String linkTag;
    @XmlElement(name = "link_desc", required = true)
    protected String linkDesc;
    @XmlElement(name = "link_uri", required = true)
    protected String linkUri;

    /**
     * Gets the value of the linkTag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkTag() {
        return linkTag;
    }

    /**
     * Sets the value of the linkTag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkTag(String value) {
        this.linkTag = value;
    }

    /**
     * Gets the value of the linkDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkDesc() {
        return linkDesc;
    }

    /**
     * Sets the value of the linkDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkDesc(String value) {
        this.linkDesc = value;
    }

    /**
     * Gets the value of the linkUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkUri() {
        return linkUri;
    }

    /**
     * Sets the value of the linkUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkUri(String value) {
        this.linkUri = value;
    }

}
