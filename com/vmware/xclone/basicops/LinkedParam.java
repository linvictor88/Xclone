package com.vmware.xclone.basicops;

public class LinkedParam {
	 /*****<b>Parameters:</b>
	 *url             [required] : url of the web service
	 *username        [required] : username for the authentication
	 *password        [required] : password for the authentication
	 *vmname          [required] : Name of the virtual machine
	 *snapshotname    [required] : Name of the snaphot
	 *clonename       [required] : Name of the cloneName
	 
	 **/
	
	private String url;
	private String userName;
	private String passWord;
	
	private String vmName;
	private String snapshotName;
	private String cloneName;
 	private String desc;
 	private boolean powerOn;
 	
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
	
	public String getVmName() {
		return vmName;
	}
	
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	
	public String getSnapshotName() {
		return snapshotName;
	}
	
	public void setSnapshotName(String snapshotName) {
		this.snapshotName = snapshotName;
	}
	
	public String getCloneName() {
		return cloneName;
	}
	
	public void setCloneName(String cloneName) {
		this.cloneName = cloneName;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public boolean getPowerOn() {
		return powerOn;
	}
	
	public void setPowerOn(boolean powerOn) {
		this.powerOn = powerOn;
	}
}