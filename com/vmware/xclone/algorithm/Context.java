package com.vmware.xclone.algorithm;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;

public class Context {

	private ManagedObjectReference propCollectorRef;
	private  ManagedObjectReference rootRef;
	private  VimService vimService;
	private  VimPortType vimPort;
	private  ServiceContent serviceContent;


	public ManagedObjectReference getPropCollectorRef() {
		return propCollectorRef;
	}
	public void setPropCollectorRef(ManagedObjectReference propCollectorRef) {
		this.propCollectorRef = propCollectorRef;
	}
	public ManagedObjectReference getRootRef() {
		return rootRef;
	}
	public void setRootRef(ManagedObjectReference rootRef) {
		this.rootRef = rootRef;
	}
	public VimService getVimService() {
		return vimService;
	}
	public void setVimService(VimService vimService) {
		this.vimService = vimService;
	}
	public VimPortType getVimPort() {
		return vimPort;
	}
	public void setVimPort(VimPortType vimPort) {
		this.vimPort = vimPort;
	}
	public ServiceContent getServiceContent() {
		return serviceContent;
	}
	public void setServiceContent(ServiceContent serviceContent) {
		this.serviceContent = serviceContent;
	}

}
