package com.vmware.xclone.basicops;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.ObjectUpdate;
import com.vmware.vim25.ObjectUpdateKind;
import com.vmware.vim25.PropertyChange;
import com.vmware.vim25.PropertyChangeOp;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertyFilterUpdate;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.UpdateSet;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.xclone.UserInterface;
import com.vmware.xclone.managementobjects.ManagementObjects;

public class DeleteVM extends Thread {
	private  ManagedObjectReference propCollectorRef;
	private  ManagedObjectReference rootRef;
	private  VimService vimService;
	private  VimPortType vimPort;
	private  ServiceContent serviceContent;
	private  ManagementObjects	managementObject;
	private  String vmName;
	private UserInterface ui;
	private VCConnection conn;

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

	public String getVmName() {
		return vmName;
	}

	public void setVmName(String vmName) {
		this.vmName = vmName;
	}

	public ManagementObjects getManagementObject() {
		return managementObject;
	}

	public void setManagementObject(ManagementObjects managementObject) {
		this.managementObject = managementObject;
	}

	public DeleteVM(String vmName)
	{
		ui = UserInterface.getInstance(null);
		try {
			conn = new VCConnection(ui.getVcUrl(), ui.getUserName(),
					ui.getPassWord());

			conn.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setManagementObject(new ManagementObjects(ui, conn));
		this.setVimService(managementObject.getVimService());
		this.setVimPort(managementObject.getVimPort());
		this.setServiceContent(managementObject.getServiceContent());
		this.setRootRef(managementObject.getRootRef());
		this.setPropCollectorRef(managementObject.getPropCollectorRef());
		this.setVmName(vmName);
		
	}

	public void run()
	{
		try
		{
			ManagedObjectReference destroyTask = vimPort.destroyTask(managementObject.getVMByName(vmName));

			if (destroyTask != null) {
				String[] opts = new String[]{"info.state", "info.error", "info.progress"};
				String[] opt = new String[]{"state"};

				Object[] results = BasicOps.waitForValues(destroyTask, opts, opt,
						new Object[][]{new Object[]{
								TaskInfoState.SUCCESS,
								TaskInfoState.ERROR}},propCollectorRef,vimPort);

				if (results[0].equals(TaskInfoState.SUCCESS)) {
					System.out.printf("Successfully destroy vm [%s] %n \n",	vmName);

				} else {
					System.out.printf("Failure  destroy vm [%s] %n \n",	vmName);
				}
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
