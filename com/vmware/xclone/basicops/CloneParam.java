package com.vmware.xclone.basicops;

public class CloneParam	 {
	private String url;
	private	String userName;
	private	String passWord;
	
	private	String dataCenter;
	private	String vmPath;
	private	String cloneName;
	
	private	String targetIp;
	private	String targetDs;
	
	private	String resPool;
	private	boolean powerOn;
	
	public void CloneParam (String url, 
							String username,
							String password,
							String datacenter,
							String vmPath,
							String cloneName,
							String targetIp,
							String targetDs,
							String resPool,
							boolean powerOn) {
		this.url 		= url;
		this.userName 	= username;
		this.passWord 	= password;
		this.dataCenter	= datacenter;
		this.vmPath 	= vmPath;
		this.cloneName	= cloneName;
		this.targetIp	= targetIp;
		this.targetDs	= targetDs;
		this.resPool	= resPool;
		this.powerOn	= powerOn;
	}
	
	public void CloneParam (String datacenter,
							String vmPath,
							String cloneName,
							String targetIp,
							String targetDs,
							String resPool,
							boolean powerOn) {	
		this.dataCenter	= datacenter;
		this.vmPath 	= vmPath;
		this.cloneName	= cloneName;
		this.targetIp	= targetIp;
		this.targetDs	= targetDs;
		this.resPool	= resPool;
		this.powerOn	= powerOn;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
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
	
	public String getCloneName() {
		return cloneName;
	}
	
	public void setCloneName(String cloneName) {
		this.cloneName = cloneName;
	}
	
	public String getTargetIp() {
		return targetIp;
	}
	
	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}
	
	public String getTargetDs() {
		return targetDs;
	}
	
	public void setTargetDs(String targetDs) {
		this.targetDs = targetDs;
	}
	
	public String getResPool() {
		return resPool;
	}
	
	public void setResPool(String resPool) {
		this.resPool = resPool;
	}
	
	public boolean isPowerOn() {
		return powerOn;
	}
	
	public void setPowerOn(boolean powerOn) {
		this.powerOn = powerOn;
	}
}
