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
import com.vmware.xclone.managementobjects.ManagementObjects;

public class BasicOps
{
	public static   Object[] waitForValues(ManagedObjectReference objmor,
			String[] filterProps,
			String[] endWaitProps,
			Object[][] expectedVals,ManagedObjectReference propCollectorRef)
					throws RemoteException,
					Exception {
		// version string is initially null
		String version = "";
		Object[] endVals = new Object[endWaitProps.length];
		Object[] filterVals = new Object[filterProps.length];

		PropertyFilterSpec spec = new PropertyFilterSpec();

		spec.getObjectSet().add(new ObjectSpec());

		spec.getObjectSet().get(0).setObj(objmor);

		spec.getPropSet().addAll(Arrays.asList(new PropertySpec[]{new PropertySpec()}));

		spec.getPropSet().get(0).getPathSet().addAll(Arrays.asList(filterProps));

		spec.getPropSet().get(0).setType(objmor.getType());

		//spec.getObjectSet().get(0).getSelectSet().add(null);

		spec.getObjectSet().get(0).setSkip(Boolean.FALSE);

		ManagedObjectReference filterSpecRef = vimPort.createFilter(propCollectorRef, spec, true);

		boolean reached = false;

		UpdateSet updateset = null;
		PropertyFilterUpdate[] filtupary = null;
		PropertyFilterUpdate filtup = null;
		ObjectUpdate[] objupary = null;
		ObjectUpdate objup = null;
		PropertyChange[] propchgary = null;
		PropertyChange propchg = null;
		while (!reached) {
			boolean retry = true;
			while (retry) {
				try {
					updateset = vimPort.waitForUpdates(propCollectorRef, version);
					retry = false;
				} catch (SOAPFaultException sfe) {
					sfe.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(updateset != null) {
				version = updateset.getVersion();
			}
			if (updateset == null || updateset.getFilterSet() == null) {
				continue;
			}
			List<PropertyFilterUpdate> listprfup = updateset.getFilterSet();
			filtupary = listprfup.toArray(new PropertyFilterUpdate[listprfup.size()]);
			filtup = null;
			for (int fi = 0; fi < filtupary.length; fi++) {
				filtup = filtupary[fi];
				List<ObjectUpdate> listobjup = filtup.getObjectSet();
				objupary = listobjup.toArray(new ObjectUpdate[listobjup.size()]);
				objup = null;
				propchgary = null;
				for (int oi = 0; oi < objupary.length; oi++) {
					objup = objupary[oi];
					if (objup.getKind() == ObjectUpdateKind.MODIFY
							|| objup.getKind() == ObjectUpdateKind.ENTER
							|| objup.getKind() == ObjectUpdateKind.LEAVE) {
						List<PropertyChange> listchset = objup.getChangeSet();
						propchgary
						= listchset.toArray(new PropertyChange[listchset.size()]);
						for (int ci = 0; ci < propchgary.length; ci++) {
							propchg = propchgary[ci];
							updateValues(endWaitProps, endVals, propchg);
							updateValues(filterProps, filterVals, propchg);
						}
					}
				}
			}
			Object expctdval = null;
			// Check if the expected values have been reached and exit the loop if done.
			// Also exit the WaitForUpdates loop if this is the case.
			for (int chgi = 0; chgi < endVals.length && !reached; chgi++) {
				for (int vali = 0; vali < expectedVals[chgi].length
						&& !reached; vali++) {
					expctdval = expectedVals[chgi][vali];
					reached = expctdval.equals(endVals[chgi]) || reached;
				}
			}
		}

		// Destroy the filter when we are done.
		vimPort.destroyPropertyFilter(filterSpecRef);
		return filterVals;
	}



	private  static void updateValues(String[] props, Object[] vals, PropertyChange propchg) {
		for (int findi = 0; findi < props.length; findi++) {
			if (propchg.getName().lastIndexOf(props[findi]) >= 0) {
				if (propchg.getOp() == PropertyChangeOp.REMOVE) {
					vals[findi] = "";
				} else {
					vals[findi] = propchg.getVal();
				}
			}
		}
	}
}



public class CloneVM {


	private  ManagedObjectReference propCollectorRef;
	private  ManagedObjectReference rootRef;
	private  VimService vimService;
	private  VimPortType vimPort;
	private  ServiceContent serviceContent;

	private  String dataCenterName;
	private  String vmPathName;
	private  String cloneName;

	private  String  targetIp;

	private  String targetDS ;
	private  String targetPool;




	private ManagementObjects managementObject;
	private  boolean powerOn= false;


	public CloneVM(String vmPathName,String cloneName,String targetIp,String targetDS,String targetPool,boolean powerOn)
	{

		this.vmPathName =  vmPathName;
		this.cloneName = cloneName; 
		this.targetIp = targetIp;
		this.targetDS	=	targetDS;
		this.targetPool = targetPool;
		this.powerOn 	= powerOn;

		managementObject = ManagementObjects.getInstance();
	};

	public boolean doClone()
	{




		return true;
	}

	private  VirtualMachineCloneSpec  getCloneSpec()
	{
		VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();

		try
		{
			//	InetAddress addr = InetAddress.getByName( VMClone.targetIp);

			VirtualMachineRelocateSpec relocSpec = new VirtualMachineRelocateSpec();

			cloneSpec.setLocation(relocSpec);
			cloneSpec.setPowerOn(true);
			cloneSpec.setTemplate(false);

			if(targetIp!=null&&!(targetIp.equals("")))
			{
				ManagedObjectReference  target  =managementObject.getHostByIp(targetIp);

				ManagedObjectReference datastoreRef = managementObject.getDataStore(targetIp);

				List<DynamicProperty> datastoresSource
				= getDynamicProarray(target, "HostSystem", "datastore");
				ArrayOfManagedObjectReference dsSourceArr =
						((ArrayOfManagedObjectReference) (datastoresSource.get(0)).getVal());

				List<ManagedObjectReference> dsTarget = dsSourceArr.getManagedObjectReference();

				ManagedObjectReference dsMOR =browseDSMOR(dsTarget);

				if(dsMOR == null) {
					throw new IllegalArgumentException(" DataSource "
							+ "dest" + " Not Found.");
				}


				ManagedObjectReference poolMOR = getDecendentMoRef(null, "ResourcePool", "cluster");


				relocSpec.setHost(target);

				relocSpec.setDatastore(dsMOR);

				relocSpec.setPool(poolMOR);
			}


		}catch(Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return cloneSpec;
	}


	private  void execCloneVM() throws Exception {
		// Find the Datacenter reference by using findByInventoryPath().
		try 
		{


			ManagedObjectReference datacenterRef  = managementObject.getDataCenterByName(dataCenterName);

			if (datacenterRef == null) {
				System.out.printf(
						"The specified datacenter [ %s ]is not found %n", dataCenterName);
				return;
			}
			ManagedObjectReference folderRef = managementObject.getFolderInDatacenter(dataCenterName);
			if (folderRef == null) {
				System.out.println("The virtual machine is not found");
				return;
			}

			managementObject.getVMByName(vmPathName);

			ManagedObjectReference vmRef
			= vimPort.findByInventoryPath(serviceContent.getSearchIndex(), vmPathName);
			if (vmRef == null) {
				System.out.printf("The VMPath specified [ %s ] is not found %n", vmPathName);
				return;
			}

			VirtualMachineCloneSpec cloneSpec = getCloneSpec();



			ManagedObjectReference cloneTask
			= vimPort.cloneVMTask(vmRef, folderRef, cloneName, cloneSpec);

			if (cloneTask != null) {
				String[] opts = new String[]{"info.state", "info.error", "info.progress"};
				String[] opt = new String[]{"state"};

				Object[] results = waitForValues(cloneTask, opts, opt,
						new Object[][]{new Object[]{
								TaskInfoState.SUCCESS,
								TaskInfoState.ERROR}});


				// Wait till the task completes.
				if (results[0].equals(TaskInfoState.SUCCESS)) {
					System.out.printf(
							"Successfully cloned Virtual Machine [%s] to clone name [%s] %n",
							vmPathName.substring(vmPathName.lastIndexOf("/") + 1), cloneName);


					//this.cloneName
				} else {
					System.out.printf(
							"Failure Cloning Virtual Machine [%s] to clone name [%s] %n",
							vmPathName.substring(vmPathName.lastIndexOf("/") + 1), cloneName);
				}
			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}


class LinkedCloneVM {

	private LinkedParam param;

	public LinkedCloneVM()
	{

	}

	public boolean DoLinkedClone()
	{
		return true;
	}

}


class PoweroffVM {
	private  ManagedObjectReference propCollectorRef;
	private  ManagedObjectReference rootRef;
	private  VimService vimService;
	private  VimPortType vimPort;
	private  ServiceContent serviceContent;
	private  ManagementObjects	managementObject;
	private  String vmName;

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

	public PoweroffVM(String vmName)
	{
		this.setManagementObject(ManagementObjects.getInstance());
		this.setVimService(managementObject.getVimService());
		this.setVimPort(managementObject.getVimPort());
		this.setServiceContent(managementObject.getServiceContent());
		this.setRootRef(managementObject.getRootRef());
		this.setPropCollectorRef(managementObject.getPropCollectorRef());
		this.setVmName(vmName);
		
	}

	public boolean powerOff()
	{
		try
		{
			ManagedObjectReference powerOffTask = vimPort.powerOffVMTask(managementObject.getVMByName(vmName));

			if (powerOffTask != null) {
				String[] opts = new String[]{"info.state", "info.error", "info.progress"};
				String[] opt = new String[]{"state"};

				Object[] results = BasicOps.waitForValues(powerOffTask, opts, opt,
						new Object[][]{new Object[]{
								TaskInfoState.SUCCESS,
								TaskInfoState.ERROR}},propCollectorRef);

				if (results[0].equals(TaskInfoState.SUCCESS)) {
					System.out.printf("Successfully poweroff vm [%s] %n \n",	vmName);

					return true;

				} else {
					System.out.printf("Failure  poweroff vm [%s] %n \n",	vmName);
				}
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}

class PoweronVM {
	private  ManagedObjectReference propCollectorRef;
	private  ManagedObjectReference rootRef;
	private  VimService vimService;
	private  VimPortType vimPort;
	private  ServiceContent serviceContent;
	private  ManagementObjects	managementObject;
	private  String vmName;

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

	public PoweronVM(String vmName)
	{
		this.setManagementObject(ManagementObjects.getInstance());
		this.setVimService(managementObject.getVimService());
		this.setVimPort(managementObject.getVimPort());
		this.setServiceContent(managementObject.getServiceContent());
		this.setRootRef(managementObject.getRootRef());
		this.setPropCollectorRef(managementObject.getPropCollectorRef());
		this.setVmName(vmName);
		
	}

	public boolean poweron()
	{
		try
		{
			ManagedObjectReference poweronTask = vimPort.poweronVMTask(managementObject.getVMByName(vmName));

			if (poweronTask != null) {
				String[] opts = new String[]{"info.state", "info.error", "info.progress"};
				String[] opt = new String[]{"state"};

				Object[] results = BasicOps.waitForValues(poweronTask, opts, opt,
						new Object[][]{new Object[]{
								TaskInfoState.SUCCESS,
								TaskInfoState.ERROR}},propCollectorRef);

				if (results[0].equals(TaskInfoState.SUCCESS)) {
					System.out.printf("Successfully poweron vm [%s] %n \n",	vmName);

					return true;

				} else {
					System.out.printf("Failure  poweron vm [%s] %n \n",	vmName);
				}
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}

class DeleteVM {
	private  ManagedObjectReference propCollectorRef;
	private  ManagedObjectReference rootRef;
	private  VimService vimService;
	private  VimPortType vimPort;
	private  ServiceContent serviceContent;
	private  ManagementObjects	managementObject;
	private  String vmName;

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
		this.setManagementObject(ManagementObjects.getInstance());
		this.setVimService(managementObject.getVimService());
		this.setVimPort(managementObject.getVimPort());
		this.setServiceContent(managementObject.getServiceContent());
		this.setRootRef(managementObject.getRootRef());
		this.setPropCollectorRef(managementObject.getPropCollectorRef());
		this.setVmName(vmName);
		
	}

	public boolean destroy()
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
								TaskInfoState.ERROR}},propCollectorRef);

				if (results[0].equals(TaskInfoState.SUCCESS)) {
					System.out.printf("Successfully destroy vm [%s] %n \n",	vmName);

					return true;

				} else {
					System.out.printf("Failure  destroy vm [%s] %n \n",	vmName);
				}
			}


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}
