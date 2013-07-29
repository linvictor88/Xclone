
package com.vmware.vim25;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebFault(name = "InvalidEventFault", targetNamespace = "urn:vim25")
public class InvalidEventFaultMsg
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private InvalidEvent faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public InvalidEventFaultMsg(String message, InvalidEvent faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public InvalidEventFaultMsg(String message, InvalidEvent faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: com.vmware.vim25.InvalidEvent
     */
    public InvalidEvent getFaultInfo() {
        return faultInfo;
    }

}
