//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.09.07 at 10:27:11 PM EDT 
//


package registry.workset;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WorksetContent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WorksetContent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://registry.htrc.i3.illinois.edu/registry/workset}volumes"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorksetContent", propOrder = {
    "volumes"
})
public class WorksetContent {

    @XmlElement(required = true)
    protected Volumes volumes;

    /**
     * Gets the value of the volumes property.
     * 
     * @return
     *     possible object is
     *     {@link Volumes }
     *     
     */
    public Volumes getVolumes() {
        return volumes;
    }

    /**
     * Sets the value of the volumes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Volumes }
     *     
     */
    public void setVolumes(Volumes value) {
        this.volumes = value;
    }

}
