package com.vmware.xclone;
import com.vmware.vim25.*;

import javax.xml.ws.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.util.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.rmi.RemoteException;

import javax.xml.ws.soap.SOAPFaultException;

/**
 *<pre>
 *UserInterface
 *
 *This sample UserInterface
 *
 *<b>Parameters:</b>
 *url            [required] : url of the web service.
 *username       [optional] : username for the authentication (default: root)
 *password       [optional] : password for the authentication (default: vmware)
 *datacentername [optional] : name of the datacenter (default: Datacenter)
 *vmname         [required] : Name of the virtual machine
 *cloneprefix    [required] : prefix of the cloned virtual machine
 *number         [required] : the number of cloned virtual machines
 *targethosts    [optional] : the listed host ips (seperate by comma, default: all listed hosts)
 *acceptlinked   [optional] : Whether accept Linked clone (default: True)
 *isOn           [optional] : Whether Power on the VM after cloning (default: True)
 *algthselect    [optional] : select which algorithm to deploy the VMs [0 | 1 | 2| 3| ...] and default: 0
 *
 *Input example: --url 10.117.5.79 --username root --password vmware --datacentername Datacenter --vmname XX 
 *--cloneprefix XX_clone_ --number 100 --targethosts 10.117.4.14,10.117.4.140 --acceptlinked true --ison true --algthselect 1
 */



public class UserInterface {
	
private String vcUrl;
private	String userName;
private	String passWord;
private	String dataCenter;
private	String vmPath;
private String vmClonePrefix;
private int numberOfVMs;
private List<String> targetHosts;
private Boolean acceptLinked;
private Boolean isOn;
private int algthSelect;

private void defaultInit() {
	this.setUserName("root");
	this.setPassWord("vmware");
	this.setDataCenter("Datacenter");
	this.setAcceptLinked(true);
	this.setIsOn(true);
	this.setNumberOfVMs(0);
	this.setAlgthSelect(0);
	this.targetHosts = new ArrayList<String>();
	
}
public UserInterface(String[] args) {
	
    int ai = 0;
    String param = "";
    String val = "";
    while (ai < args.length) {
       param = args[ai].trim();
       if (ai + 1 < args.length) {
          val = args[ai + 1].trim();
       }
       if (param.equalsIgnoreCase("--url") && !val.startsWith("--") &&
               !val.isEmpty()) {
             this.setVcUrl(val);
         } else if (param.equalsIgnoreCase("--username") && !val.startsWith("--") &&
               !val.isEmpty()) {
        	 this.setUserName(val);
         } else if (param.equalsIgnoreCase("--password") && !val.startsWith("--") &&
               !val.isEmpty()) {
        	 this.setPassWord(val);
         } else if (param.equalsIgnoreCase("--datacentername") && !val.startsWith("--") &&
               !val.isEmpty()) {
        	 this.setDataCenter(val);
         } else if (param.equalsIgnoreCase("--vmname") &&
               !val.startsWith("--") && !val.isEmpty()) {
        	 String vmPath = "https://" + val + "/sdk";
        	 this.setVmPath(vmPath);
         } else if (param.equalsIgnoreCase("--cloneprefix") && !val.startsWith("--") &&
               !val.isEmpty()) {
             this.setVmClonePrefix(val);
         } else if (param.equalsIgnoreCase("--number") && !val.startsWith("--") &&
                  !val.isEmpty()) {
        	 int number = Integer.parseInt(val);
             this.setNumberOfVMs(number);
         } else if (param.equalsIgnoreCase("--targethosts") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 String[] hosts = new String[]{}; 
        	 hosts = val.split(",");
        	 this.setTargetHosts(Arrays.asList(hosts));
         } else if (param.equalsIgnoreCase("--acceptLinked") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 if (val.equalsIgnoreCase("false")) {
        		 this.setAcceptLinked(false);
        	 }
         } else if (param.equalsIgnoreCase("--ison") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 if (val.equalsIgnoreCase("false")) {
        		 this.setIsOn(false);
        	 }
         }
       val = "";
       ai += 2;
    }   
    
    if(this.getVcUrl() == null || this.getVmPath() == null || this.getVmClonePrefix() == null ||
       this.getNumberOfVMs() == 0) {
       throw new IllegalArgumentException(
          "Expected --url, --vmname, --cloneprefix, --number arguments.");
    }	
}

public String getVcUrl() {
	return vcUrl;
}
public void setVcUrl(String vcUrl) {
	this.vcUrl = vcUrl;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getPassWord() {
	return passWord;
}
public void setPassWord(String passWord) {
	this.passWord = passWord;
}
public String getDataCenter() {
	return dataCenter;
}
public void setDataCenter(String dataCenter) {
	this.dataCenter = dataCenter;
}
public String getVmPath() {
	return vmPath;
}
public void setVmPath(String vmPath) {
	this.vmPath = vmPath;
}
public String getVmClonePrefix() {
	return vmClonePrefix;
}
public void setVmClonePrefix(String vmClonePrefix) {
	this.vmClonePrefix = vmClonePrefix;
}
public int getNumberOfVMs() {
	return numberOfVMs;
}
public void setNumberOfVMs(int numberOfVMs) {
	this.numberOfVMs = numberOfVMs;
}
public List<String> getTargetHosts() {
	return targetHosts;
}
public void setTargetHosts(List<String> targetHosts) {
	this.targetHosts = targetHosts;
}
public Boolean getAcceptLinked() {
	return acceptLinked;
}
public void setAcceptLinked(Boolean acceptLinked) {
	this.acceptLinked = acceptLinked;
}
public Boolean getIsOn() {
	return isOn;
}
public void setIsOn(Boolean isOn) {
	this.isOn = isOn;
}
public int getAlgthSelect() {
	return algthSelect;
}
public void setAlgthSelect(int algthSelect) {
	this.algthSelect = algthSelect;
}
}
