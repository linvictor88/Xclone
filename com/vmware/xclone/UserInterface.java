package com.vmware.xclone;
import java.util.*;

public class UserInterface {
	
private String vcUrl;
private	String userName;
private	String passWord;
private	String dataCenter;
private String resourcePool;
private	String vmPath;
private String vmClonePrefix;
private int numberOfVMs;
private String srcHost;
private List<String> dstHostList;
private Boolean acceptLinked;
private Boolean isOn;
private int algthSelect;
private String opselect;
private static UserInterface instance = null;

 

private final static Object syncLock = new Object();  
public  static UserInterface getInstance(String[] args)
{

	if (instance == null) {  
		synchronized (syncLock) {  
			if (instance == null) {  
				instance = new UserInterface(args);  
			}  
		}  
	}  

	return instance;
} 


private void defaultInit() {
	this.setUserName("root");
	this.setPassWord("vmware");
	this.setDataCenter("Datacenter");
	this.setResourcePool("cluster");
	this.setAcceptLinked(true);
	this.setIsOn(true);
	this.setNumberOfVMs(0);
	this.setAlgthSelect(1);
	this.dstHostList = new ArrayList<String>();
	this.setOpselect("create");
}

public UserInterface(String[] args) {
	this.defaultInit();
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
      	     String url = "https://" + val + "/sdk";
             this.setVcUrl(url);
         } else if (param.equalsIgnoreCase("--username") && !val.startsWith("--") &&
               !val.isEmpty()) {
        	 this.setUserName(val);
         } else if (param.equalsIgnoreCase("--password") && !val.startsWith("--") &&
               !val.isEmpty()) {
        	 this.setPassWord(val);
         } else if (param.equalsIgnoreCase("--datacentername") && !val.startsWith("--") &&
               !val.isEmpty()) {
        	 this.setDataCenter(val);
         } else if (param.equalsIgnoreCase("--resourcepool") && !val.startsWith("--") &&
        		 !val.isEmpty()) {
        	this.setResourcePool(val); 
         } else if (param.equalsIgnoreCase("--vmname") &&
               !val.startsWith("--") && !val.isEmpty()) {
        	 String path = this.getDataCenter() + "/vm/" + val;
        	 this.setVmPath(path);
         } else if (param.equalsIgnoreCase("--cloneprefix") && !val.startsWith("--") &&
               !val.isEmpty()) {
             this.setVmClonePrefix(val);
         } else if (param.equalsIgnoreCase("--number") && !val.startsWith("--") &&
                  !val.isEmpty()) {
        	 int number = Integer.parseInt(val);
             this.setNumberOfVMs(number);
         } else if (param.equalsIgnoreCase("--dsthosts") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 String[] hosts = new String[]{}; 
        	 hosts = val.split(",");
        	 this.setDstHostList(Arrays.asList(hosts));
         } else if (param.equalsIgnoreCase("--srchost") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 this.setSrcHost(val);
         } else if (param.equalsIgnoreCase("--acceptLinked") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 if (val.equalsIgnoreCase("false")) {
        		 this.setAcceptLinked(false);
        	 } else{
        		 this.setAcceptLinked(true);
        	 }
         } else if (param.equalsIgnoreCase("--ison") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 if (val.equalsIgnoreCase("false")) {
        		 this.setIsOn(false);
        	 }else{
        		 this.setIsOn(true);
        	 }
         }else if (param.equalsIgnoreCase("--algthselect") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 this.setAlgthSelect(Integer.parseInt(val));
         } else if (param.equalsIgnoreCase("--opselect") && !val.startsWith("--") &&
                 !val.isEmpty()) {
        	 this.setOpselect(val);
         }
       val = "";
       ai += 2;
    }   
    
    if(this.getVcUrl() == null || this.getVmPath() == null || this.getVmClonePrefix() == null  ||
       this.getNumberOfVMs() == 0 || this.getDstHostList() == null || this.getSrcHost() == null) 
    {
       throw new IllegalArgumentException(
          "Expected --url, --vmname, --cloneprefix, --number, --srchost, --dsthosts arguments.");
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
public List<String> getDstHostList() {
	return dstHostList;
}
public void setDstHostList(List<String> dstHostList) {
	this.dstHostList = dstHostList;
}



public String getSrcHost() {
	return srcHost;
}

public void setSrcHost(String srcHost) {
	this.srcHost = srcHost;
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

public String getResourcePool() {
	return resourcePool;
}

public void setResourcePool(String resourcePool) {
	this.resourcePool = resourcePool;
}


public String getOpselect() {
	return opselect;
}


public void setOpselect(String opselect) {
	this.opselect = opselect;
}


public static UserInterface getInstance() {
	return instance;
}


public static void setInstance(UserInterface instance) {
	UserInterface.instance = instance;
}


public static Object getSynclock() {
	return syncLock;
}

}

