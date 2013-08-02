package com.vmware.xclone.managementobjects;
import com.vmware.vim25.*;

import javax.xml.ws.*;
import javax.xml.ws.soap.SOAPFaultException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.util.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.rmi.RemoteException;

import com.vmware.xclone.UserInterface;
import com.vmware.xclone.basicops.*;


public class ManagementObjects {


	//get the dataStore By Name;
	//get the dataStore By Host
	//get the dataStore 
	//public static Map<String,String> dataStoreList; //name->Object map
	//public static Map<String,ManagedObjectReference> vmList; //name->Object map
	//public static String vmClonePrefix;

	
	
	//	public static  String vmName;
	public static String[] meTree = {
		"ManagedEntity",
		"ComputeResource",
		"ClusterComputeResource",
		"Datacenter",
		"Folder",
		"HostSystem",
		"ResourcePool",
		"VirtualMachine"
	};
	public static String[] crTree = {
		"ComputeResource",
		"ClusterComputeResource"
	};
	public static String[] hcTree = {
		"HistoryCollector",
		"EventHistoryCollector",
		"TaskHistoryCollector"
	};
	public   VCConnection vcConn;


	public    ManagedObjectReference propCollectorRef;

	public    ManagedObjectReference rootRef;

	public    VimService vimService;

	public    VimPortType vimPort;
	public   VimPortType getVimPort() {
		return vimPort;
	}

	public   void setVimPort(VimPortType vimPort) {
		this.vimPort = vimPort;
	}



	public   ServiceContent serviceContent;



	public List<String> vmList;


//	public static  ManagementObjects managementObject=null;


	public final static Object syncLock = new Object();  

	public   ManagedObjectReference getPropCollectorRef() {
		return propCollectorRef;
	}

	public   void setPropCollectorRef(ManagedObjectReference propCollectorRef) {
		this.propCollectorRef = propCollectorRef;
	}

	public   ManagedObjectReference getRootRef() {
		return rootRef;
	}

	public   void setRootRef(ManagedObjectReference rootRef) {
		this.rootRef = rootRef;
	}

	public   VimService getVimService() {
		return vimService;
	}

	public   void setVimService(VimService vimService) {
		this.vimService = vimService;
	}

	public   ServiceContent getServiceContent() {
		return serviceContent;
	}

	public   void setServiceContent(ServiceContent serviceContent) {
		this.serviceContent = serviceContent;
	}


	public ManagementObjects(VCConnection vcConn)
	{
	try {
			
			this.vcConn = vcConn;
			vimService = vcConn.getVimService();
			rootRef = vcConn.getRootRef();
			propCollectorRef = vcConn.getPropRef();
			vimPort = vcConn.getVimPort();
			serviceContent = vcConn.getServiceContent();

			vmList = new ArrayList<String>();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

 
	public ManagementObjects(UserInterface ui, VCConnection vcConn)
	{
		try {
			
			this.vcConn = vcConn;
			vimService = vcConn.getVimService();
			rootRef = vcConn.getRootRef();
			propCollectorRef = vcConn.getPropRef();
			vimPort = vcConn.getVimPort();
			serviceContent = vcConn.getServiceContent();

			vmList = new ArrayList<String>();
		} catch(Exception e){
			e.printStackTrace();
		}
	}



	public void setConn(VCConnection vcconn)
	{
		if(vcconn==null)
		{
			System.out.println("the connection object is null.");
			return;
		}

		this.vcConn = vcconn;


	}





	public void addClonedList(String vmName)
	{
		vmList.add(vmName);
	}


	public List<String> getClonedList()
	{
		return vmList;
	}


	public  VirtualMachineCloneSpec  getCloneSpec(String targetIp,String vmPathName,String targetPool,boolean powerOn,boolean template)
	{
		VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();

		try
		{
			//	InetAddress addr = InetAddress.getByName( VMClone.targetIp);

			VirtualMachineRelocateSpec relocSpec = new VirtualMachineRelocateSpec();

			cloneSpec.setLocation(relocSpec);
			cloneSpec.setPowerOn(powerOn);
			cloneSpec.setTemplate(template);

			if(targetIp!=null&&!(targetIp.equals("")))
			{
				ManagedObjectReference  hostRef  =getHostByIp(targetIp);
				//ManagedObjectReference vmRef = getVMByPathname(vmPathName);
				List<ManagedObjectReference> datastoreRef = getDatastoreByHost(hostRef);
				ManagedObjectReference resPoolRef = getRespool( targetPool);
				relocSpec.setHost(hostRef);
				relocSpec.setDatastore(datastoreRef.get(1));
				relocSpec.setPool(resPoolRef);
			}


		}catch(Exception e)
		{
			e.printStackTrace();

			return null;
		}

		return cloneSpec;
	}

	public ManagedObjectReference getRespool(String resPoolName)
	{
		try
		{
			return getDecendentMoRef(null, "ResourcePool", resPoolName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}



	public ManagedObjectReference getVMByPathname(String vmPathName)
	{
		if(vmPathName==null||vmPathName.equals(""))
			return null;

		try
		{
			return vimPort.findByInventoryPath(serviceContent.getSearchIndex(), vmPathName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public   List<PropertySpec> buildPropertySpecArray(String[][] typeinfo) {
		// Eliminate duplicates
		HashMap<String, Set> tInfo = new HashMap<String, Set>();
		for(int ti = 0; ti < typeinfo.length; ++ti) {
			Set props = (Set) tInfo.get(typeinfo[ti][0]);
			if(props == null) {
				props = new HashSet<String>();
				tInfo.put(typeinfo[ti][0], props);
			}
			boolean typeSkipped = false;
			for(int pi = 0; pi < typeinfo[ti].length; ++pi) {
				String prop = typeinfo[ti][pi];
				if(typeSkipped) {
					props.add(prop);
				} else {
					typeSkipped = true;
				}
			}
		}

		// Create PropertySpecs
		ArrayList<PropertySpec> pSpecs = new ArrayList<PropertySpec>();
		for(Iterator<String> ki = tInfo.keySet().iterator(); ki.hasNext();) {
			String type = (String) ki.next();
			PropertySpec pSpec = new PropertySpec();
			Set props = (Set) tInfo.get(type);
			pSpec.setType(type);
			pSpec.setAll(props.isEmpty() ? Boolean.TRUE : Boolean.FALSE);
			for(Iterator pi = props.iterator(); pi.hasNext();) {
				String prop = (String) pi.next();
				pSpec.getPathSet().add(prop);
			}
			pSpecs.add(pSpec);
		}

		return pSpecs;
	}
	public  List<ObjectContent>    getContentsRecursively(ManagedObjectReference collector,
			ManagedObjectReference root,
			String[][] typeinfo, boolean recurse)
					throws Exception {
		if (typeinfo == null || typeinfo.length == 0) {
			return null;
		}

		ManagedObjectReference usecoll = collector;
		if (usecoll == null) {
			usecoll = serviceContent.getPropertyCollector();
		}

		ManagedObjectReference useroot = root;
		if (useroot == null) {
			useroot = serviceContent.getRootFolder();
		}

		List<SelectionSpec> selectionSpecs = null;
		if (recurse) {
			selectionSpecs = buildFullTraversal();
		}

		List<PropertySpec> propspecary = buildPropertySpecArray(typeinfo);
		ObjectSpec objSpec = new ObjectSpec();
		objSpec.setObj(useroot);
		objSpec.setSkip(Boolean.FALSE);
		objSpec.getSelectSet().addAll(selectionSpecs);
		List<ObjectSpec> objSpecList = new ArrayList<ObjectSpec>();
		objSpecList.add(objSpec);
		PropertyFilterSpec spec = new PropertyFilterSpec();
		spec.getPropSet().addAll(propspecary);
		spec.getObjectSet().addAll(objSpecList);
		List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>();
		listpfs.add(spec);
		List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);

		return listobjcont;
	}
	public  ManagedObjectReference getDecendentMoRef(ManagedObjectReference root,
			String type,
			String name)
					throws Exception {
		if (name == null || name.length() == 0) {
			return null;
		}

		String[][] typeinfo =
				new String[][] {new String[] {type, "name"}, };

		List<ObjectContent> ocary =	getContentsRecursively(null, root, typeinfo, true);

		if (ocary == null || ocary.size() == 0) {
			return null;
		}

		ObjectContent oc = null;
		ManagedObjectReference mor = null;
		List<DynamicProperty> propary = null;
		String propval = null;
		boolean found = false;
		for (int oci = 0; oci < ocary.size() && !found; oci++) {
			oc = ocary.get(oci);
			mor = oc.getObj();
			propary = oc.getPropSet();

			propval = null;
			if (type == null || typeIsA(type, mor.getType())) {
				if (propary.size() > 0) {
					propval = (String) propary.get(0).getVal();
				}
				found = propval != null && name.equals(propval);
			}
		}

		if (!found) {
			mor = null;
		}

		return mor;
	}
	public static boolean typeIsA(String searchType,
			String foundType) {
		if(searchType.equals(foundType)) {
			return true;
		} else if(searchType.equals("ManagedEntity")) {
			for(int i = 0; i < meTree.length; ++i) {
				if(meTree[i].equals(foundType)) {
					return true;
				}
			}
		} else if(searchType.equals("ComputeResource")) {
			for(int i = 0; i < crTree.length; ++i) {
				if(crTree[i].equals(foundType)) {
					return true;
				}
			}
		} else if(searchType.equals("HistoryCollector")) {
			for(int i = 0; i < hcTree.length; ++i) {
				if(hcTree[i].equals(foundType)) {
					return true;
				}
			}
		}
		return false;
	}

	public ManagedObjectReference[] getDatastoreByVM(ManagedObjectReference vmRef)
	{
		return getDataStorebyVMMor(vmRef);
	}

	public    ManagedObjectReference getVMByName(String vmName)
	{
		return getVmByVMname(vmName);
	}


	public List<String> getDatastoreNameByHostIp(String hostIp)
	{
		if(hostIp==null||hostIp.equals(""))
			return null;
		return getDataStore(hostIp);
	}


	public ManagedObjectReference getDataCenterByName(String dataCenterName)
	{

		if(dataCenterName==null||dataCenterName.equals(""))
			return null;

		try
		{
			return  vimPort.findByInventoryPath(serviceContent.getSearchIndex(), dataCenterName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public ManagedObjectReference getFolderInDatacenter(String dataCenterName)
	{

		try
		{

			ManagedObjectReference dataCenterRef = getDataCenterByName(dataCenterName);

			if(dataCenterRef==null)
				return null;
			ManagedObjectReference vmFolderRef =(ManagedObjectReference) getDynamicProperty(dataCenterRef, "vmFolder");

			if (vmFolderRef == null) 
				System.out.println("The virtual machine is not found");
			return vmFolderRef;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}



	////jizhi
	public ManagedObjectReference getFolderByDatacenterName(String dataCenterName)
	{

		if(dataCenterName==null||dataCenterName.equals(""))
			return null;

		try
		{
			ManagedObjectReference folderRef = (ManagedObjectReference) getDynamicProperty(getDataCenterByName(dataCenterName), "vmFolder");
			return folderRef;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
	//


	public List<ManagedObjectReference> getDatastoreByHost(ManagedObjectReference  target)
	{
		try
		{
			List<DynamicProperty> datastoresSource
			= getDynamicProarray(target, "HostSystem", "datastore");


			//dsSourceArr.getManagedObjectReference() return more null objects....
			ArrayOfManagedObjectReference dsSourceArr =
					((ArrayOfManagedObjectReference) (datastoresSource.get(0)).getVal());

			return  dsSourceArr.getManagedObjectReference();
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public  List<String> getDataStore(String targetIp)
	{

		try
		{
			ManagedObjectReference  target  =getHostByIp(targetIp);
			List<DynamicProperty> datastoresSource
			= getDynamicProarray(target, "HostSystem", "datastore");


			//dsSourceArr.getManagedObjectReference() return more null objects....
			ArrayOfManagedObjectReference dsSourceArr =
					((ArrayOfManagedObjectReference) (datastoresSource.get(0)).getVal());

			List<ManagedObjectReference> dsTarget = dsSourceArr.getManagedObjectReference();


			List<String> dsNameList= new ArrayList<String>();

			try {
				if(dsTarget != null && dsTarget.size() > 0) {
					for (int i = 0; i < dsTarget.size(); i++) {
						DatastoreSummary ds = getDataStoreSummary(dsTarget.get(i));
						String dsname  = ds.getName();
						dsNameList.add(dsname);
					}
				}
			} catch (SOAPFaultException sfe) {
				sfe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dsNameList;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;

	}

	public  DatastoreSummary getDataStoreSummary(ManagedObjectReference dataStore)
			throws Exception {
		DatastoreSummary dataStoreSummary = new DatastoreSummary();
		PropertySpec propertySpec = new PropertySpec();
		propertySpec.setAll(Boolean.FALSE);
		propertySpec.getPathSet().add("summary");
		propertySpec.setType("Datastore");

		// Now create Object Spec
		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(dataStore);
		objectSpec.setSkip(Boolean.FALSE);
		objectSpec.getSelectSet().addAll(buildFullTraversal());
		// Create PropertyFilterSpec using the PropertySpec and ObjectPec
		// created above.
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getPropSet().add(propertySpec);
		propertyFilterSpec.getObjectSet().add(objectSpec);
		List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
		listpfs.add(propertyFilterSpec);
		List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);
		for(int j = 0; j < listobjcont.size(); j++) {
			List<DynamicProperty> propSetList = listobjcont.get(j).getPropSet();
			for(int k = 0; k < propSetList.size(); k++) {
				dataStoreSummary = (DatastoreSummary) propSetList.get(k).getVal();
			}
		}
		return dataStoreSummary;
	}


	public   SelectionSpec getSelectionSpec(String name) {
		SelectionSpec genericSpec = new SelectionSpec();
		genericSpec.setName(name);
		return genericSpec;
	}

	public   List<SelectionSpec> buildFullTraversal() {
		// Terminal traversal specs

		// RP -> VM
		TraversalSpec rpToVm = new TraversalSpec();
		rpToVm.setName("rpToVm");
		rpToVm.setType("ResourcePool");
		rpToVm.setPath("vm");
		rpToVm.setSkip(Boolean.FALSE);

		// vApp -> VM
		TraversalSpec vAppToVM = new TraversalSpec();
		vAppToVM.setName("vAppToVM");
		vAppToVM.setType("VirtualApp");
		vAppToVM.setPath("vm");

		// HostSystem -> VM
		TraversalSpec hToVm = new TraversalSpec();
		hToVm.setType("HostSystem");
		hToVm.setPath("vm");
		hToVm.setName("hToVm");
		hToVm.getSelectSet().add(getSelectionSpec("visitFolders"));
		hToVm.setSkip(Boolean.FALSE);

		// DC -> DS
		TraversalSpec dcToDs = new TraversalSpec();
		dcToDs.setType("Datacenter");
		dcToDs.setPath("datastore");
		dcToDs.setName("dcToDs");
		dcToDs.setSkip(Boolean.FALSE);

		// Recurse through all ResourcePools
		TraversalSpec rpToRp = new TraversalSpec();
		rpToRp.setType("ResourcePool");
		rpToRp.setPath("resourcePool");
		rpToRp.setSkip(Boolean.FALSE);
		rpToRp.setName("rpToRp");
		rpToRp.getSelectSet().add(getSelectionSpec("rpToRp"));
		rpToRp.getSelectSet().add(getSelectionSpec("rpToVm"));

		TraversalSpec crToRp = new TraversalSpec();
		crToRp.setType("ComputeResource");
		crToRp.setPath("resourcePool");
		crToRp.setSkip(Boolean.FALSE);
		crToRp.setName("crToRp");
		crToRp.getSelectSet().add(getSelectionSpec("rpToRp"));
		crToRp.getSelectSet().add(getSelectionSpec("rpToVm"));

		TraversalSpec crToH = new TraversalSpec();
		crToH.setSkip(Boolean.FALSE);
		crToH.setType("ComputeResource");
		crToH.setPath("host");
		crToH.setName("crToH");

		TraversalSpec dcToHf = new TraversalSpec();
		dcToHf.setSkip(Boolean.FALSE);
		dcToHf.setType("Datacenter");
		dcToHf.setPath("hostFolder");
		dcToHf.setName("dcToHf");
		dcToHf.getSelectSet().add(getSelectionSpec("visitFolders"));

		TraversalSpec vAppToRp = new TraversalSpec();
		vAppToRp.setName("vAppToRp");
		vAppToRp.setType("VirtualApp");
		vAppToRp.setPath("resourcePool");
		vAppToRp.getSelectSet().add(getSelectionSpec("rpToRp"));

		TraversalSpec dcToVmf = new TraversalSpec();
		dcToVmf.setType("Datacenter");
		dcToVmf.setSkip(Boolean.FALSE);
		dcToVmf.setPath("vmFolder");
		dcToVmf.setName("dcToVmf");
		dcToVmf.getSelectSet().add(getSelectionSpec("visitFolders"));

		// For Folder -> Folder recursion
		TraversalSpec visitFolders = new TraversalSpec();
		visitFolders.setType("Folder");
		visitFolders.setPath("childEntity");
		visitFolders.setSkip(Boolean.FALSE);
		visitFolders.setName("visitFolders");
		List <SelectionSpec> sspecarrvf = new ArrayList<SelectionSpec>();
		sspecarrvf.add(getSelectionSpec("visitFolders"));
		sspecarrvf.add(getSelectionSpec("dcToVmf"));
		sspecarrvf.add(getSelectionSpec("dcToHf"));
		sspecarrvf.add(getSelectionSpec("dcToDs"));
		sspecarrvf.add(getSelectionSpec("crToRp"));
		sspecarrvf.add(getSelectionSpec("crToH"));
		sspecarrvf.add(getSelectionSpec("hToVm"));
		sspecarrvf.add(getSelectionSpec("rpToVm"));
		sspecarrvf.add(getSelectionSpec("rpToRp"));
		sspecarrvf.add(getSelectionSpec("vAppToRp"));
		sspecarrvf.add(getSelectionSpec("vAppToVM"));

		visitFolders.getSelectSet().addAll(sspecarrvf);

		List <SelectionSpec> resultspec = new ArrayList<SelectionSpec>();
		resultspec.add(visitFolders);
		resultspec.add(dcToVmf);
		resultspec.add(dcToHf);
		resultspec.add(dcToDs);
		resultspec.add(crToRp);
		resultspec.add(crToH);
		resultspec.add(hToVm);
		resultspec.add(rpToVm);
		resultspec.add(vAppToRp);
		resultspec.add(vAppToVM);
		resultspec.add(rpToRp);

		return resultspec;
	}
	public  List<DynamicProperty> getDynamicProarray(ManagedObjectReference ref,
			String type,
			String propertyString)
					throws Exception {
		PropertySpec propertySpec = new PropertySpec();
		propertySpec.setAll(Boolean.FALSE);
		propertySpec.getPathSet().add(propertyString);
		propertySpec.setType(type);

		// Now create Object Spec
		ObjectSpec objectSpec = new ObjectSpec();
		objectSpec.setObj(ref);
		objectSpec.setSkip(Boolean.FALSE);
		objectSpec.getSelectSet().addAll(buildFullTraversal());
		// Create PropertyFilterSpec using the PropertySpec and ObjectPec
		// created above.
		PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
		propertyFilterSpec.getPropSet().add(propertySpec);
		propertyFilterSpec.getObjectSet().add(objectSpec);
		List<PropertyFilterSpec> listPfs = new ArrayList<PropertyFilterSpec>(1);
		listPfs.add(propertyFilterSpec);
		List<ObjectContent> oContList
		= retrievePropertiesAllObjects(listPfs);
		ObjectContent contentObj = oContList.get(0);
		List<DynamicProperty> objList = contentObj.getPropSet();
		return objList;
	}
	public ManagedObjectReference  getHostByIp(String ipAddress)
	{

		if(ipAddress==null||ipAddress.equals(""))
			return null;

		ManagedObjectReference retVal = null;
		ManagedObjectReference rootFolder = serviceContent.getRootFolder();
		try {
			TraversalSpec tSpec = getHostSystemTraversalSpec();
			// Create Property Spec
			PropertySpec propertySpec = new PropertySpec();
			propertySpec.setAll(Boolean.FALSE);
			propertySpec.getPathSet().add("name");
			propertySpec.setType("HostSystem");

			// Now create Object Spec
			ObjectSpec objectSpec = new ObjectSpec();
			objectSpec.setObj(rootFolder);
			objectSpec.setSkip(Boolean.TRUE);
			objectSpec.getSelectSet().add(tSpec);

			// Create PropertyFilterSpec using the PropertySpec and ObjectPec
			// created above.
			PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
			propertyFilterSpec.getPropSet().add(propertySpec);
			propertyFilterSpec.getObjectSet().add(objectSpec);
			List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
			listpfs.add(propertyFilterSpec);
			List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);

			if (listobjcont != null) {
				for (ObjectContent oc : listobjcont) {
					ManagedObjectReference mr = oc.getObj();
					String hostnm = null;
					List<DynamicProperty> listDynamicProps = oc.getPropSet();
					DynamicProperty[] dps
					= listDynamicProps.toArray(
							new DynamicProperty[listDynamicProps.size()]);
					if (dps != null) {
						for (DynamicProperty dp : dps) {
							hostnm = (String) dp.getVal();
						}
					}
					if (hostnm != null && hostnm.equals(ipAddress)) {
						retVal = mr;
						break;
					}
				}
			} else {
				System.out.println("The Object Content is Null");
			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}


	public   TraversalSpec getHostSystemTraversalSpec() {
		// Create a traversal spec that starts from the 'root' objects
		// and traverses the inventory tree to get to the Host system.
		// Build the traversal specs bottoms up
		SelectionSpec ss = new SelectionSpec();
		ss.setName("VisitFolders");

		// Traversal to get to the host from ComputeResource
		TraversalSpec computeResourceToHostSystem = new TraversalSpec();
		computeResourceToHostSystem.setName("computeResourceToHostSystem");
		computeResourceToHostSystem.setType("ComputeResource");
		computeResourceToHostSystem.setPath("host");
		computeResourceToHostSystem.setSkip(false);
		computeResourceToHostSystem.getSelectSet().add(ss);

		// Traversal to get to the ComputeResource from hostFolder
		TraversalSpec hostFolderToComputeResource = new TraversalSpec();
		hostFolderToComputeResource.setName("hostFolderToComputeResource");
		hostFolderToComputeResource.setType("Folder");
		hostFolderToComputeResource.setPath("childEntity");
		hostFolderToComputeResource.setSkip(false);
		hostFolderToComputeResource.getSelectSet().add(ss);

		// Traversal to get to the hostFolder from DataCenter
		TraversalSpec dataCenterToHostFolder = new TraversalSpec();
		dataCenterToHostFolder.setName("DataCenterToHostFolder");
		dataCenterToHostFolder.setType("Datacenter");
		dataCenterToHostFolder.setPath("hostFolder");
		dataCenterToHostFolder.setSkip(false);
		dataCenterToHostFolder.getSelectSet().add(ss);

		//TraversalSpec to get to the DataCenter from rootFolder
		TraversalSpec traversalSpec = new TraversalSpec();
		traversalSpec.setName("VisitFolders");
		traversalSpec.setType("Folder");
		traversalSpec.setPath("childEntity");
		traversalSpec.setSkip(false);

		List<SelectionSpec> sSpecArr = new ArrayList<SelectionSpec>();
		sSpecArr.add(ss);
		sSpecArr.add(dataCenterToHostFolder);
		sSpecArr.add(hostFolderToComputeResource);
		sSpecArr.add(computeResourceToHostSystem);
		traversalSpec.getSelectSet().addAll(sSpecArr);
		return traversalSpec;
	}

	public  ObjectContent[] getObjectProperties(ManagedObjectReference mobj,
			String[] properties)
					throws Exception {
		if (mobj == null) {
			return null;
		}

		PropertyFilterSpec spec = new PropertyFilterSpec();
		spec.getPropSet().add(new PropertySpec());
		if((properties == null || properties.length == 0)) {
			spec.getPropSet().get(0).setAll(Boolean.TRUE);
		} else {
			spec.getPropSet().get(0).setAll(Boolean.FALSE);
		}
		spec.getPropSet().get(0).setType(mobj.getType());
		spec.getPropSet().get(0).getPathSet().addAll(Arrays.asList(properties));
		spec.getObjectSet().add(new ObjectSpec());
		spec.getObjectSet().get(0).setObj(mobj);
		spec.getObjectSet().get(0).setSkip(Boolean.FALSE);
		List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
		listpfs.add(spec);
		List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);
		return listobjcont.toArray(new ObjectContent[listobjcont.size()]);
	}


	/**
	 * Determines of a method 'methodName' exists for the Object 'obj'.
	 * @param obj The Object to check
	 * @param methodName The method name
	 * @param parameterTypes Array of Class objects for the parameter types
	 * @return true if the method exists, false otherwise
	 */
	public  boolean methodExists(Object obj,
			String methodName,
			Class[] parameterTypes) {
		boolean exists = false;
		try {
			Method method = obj.getClass().getMethod(methodName, parameterTypes);
			if (method != null) {
				exists = true;
			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exists;
	}

	public  Object getDynamicProperty(ManagedObjectReference mor,
			String propertyName)
					throws Exception {
		ObjectContent[] objContent = getObjectProperties(mor, new String[]{propertyName});

		Object propertyValue = null;
		if (objContent != null) {
			List<DynamicProperty> listdp =  objContent[0].getPropSet();
			if (listdp != null) {
				/*
				 * Check the dynamic propery for ArrayOfXXX object
				 */
				Object dynamicPropertyVal = listdp.get(0).getVal();
				String dynamicPropertyName = dynamicPropertyVal.getClass().getName();
				if (dynamicPropertyName.indexOf("ArrayOf") != -1) {
					String methodName
					= dynamicPropertyName.substring(
							dynamicPropertyName.indexOf("ArrayOf")
							+ "ArrayOf".length(),
							dynamicPropertyName.length());
					/*
					 * If object is ArrayOfXXX object, then get the XXX[] by
					 * invoking getXXX() on the object.
					 * For Ex:
					 * ArrayOfManagedObjectReference.getManagedObjectReference()
					 * returns ManagedObjectReference[] array.
					 */
					if (methodExists(dynamicPropertyVal,
							"get" + methodName, null)) {
						methodName = "get" + methodName;
					} else {
						/*
						 * Construct methodName for ArrayOf primitive types
						 * Ex: For ArrayOfInt, methodName is get_int
						 */
						methodName = "get_"
								+ methodName.toLowerCase();
					}
					Method getMorMethod = dynamicPropertyVal.getClass().
							getDeclaredMethod(methodName, (Class[]) null);
					propertyValue = getMorMethod.invoke(dynamicPropertyVal, (Object[]) null);
				} else if (dynamicPropertyVal.getClass().isArray()) {
					/*
					 * Handle the case of an unwrapped array being deserialized.
					 */
					propertyValue = dynamicPropertyVal;
				} else {
					propertyValue = dynamicPropertyVal;
				}
			}
		}
		return propertyValue;
	}

	public   ManagedObjectReference getVmByVMname(String vmName) {
		ManagedObjectReference retVal = null;


		ManagedObjectReference rootFolder = serviceContent.getRootFolder();
		try {
			TraversalSpec tSpec = getVMTraversalSpec();
			// Create Property Spec
			PropertySpec propertySpec = new PropertySpec();
			propertySpec.setAll(Boolean.FALSE);
			propertySpec.getPathSet().add("name");
			propertySpec.setType("VirtualMachine");

			// Now create Object Spec
			ObjectSpec objectSpec = new ObjectSpec();
			objectSpec.setObj(rootFolder);
			objectSpec.setSkip(Boolean.TRUE);
			objectSpec.getSelectSet().add(tSpec);

			// Create PropertyFilterSpec using the PropertySpec and ObjectPec
			// created above.et
			PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
			propertyFilterSpec.getPropSet().add(propertySpec);
			propertyFilterSpec.getObjectSet().add(objectSpec);

			List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
			listpfs.add(propertyFilterSpec);
			List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);

			if (listobjcont != null) {
				for (ObjectContent oc : listobjcont) {
					ManagedObjectReference mr = oc.getObj();
					String vmnm = null;
					List<DynamicProperty> dps = oc.getPropSet();
					if (dps != null) {
						for (DynamicProperty dp : dps) {
							vmnm = (String) dp.getVal();
						}
					}
					if (vmnm != null && vmnm.equals(vmName)) {
						retVal = mr;
						break;
					}
				}
			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
	
	public List<String> getVMNames()
	{
		
		List<String> names = new ArrayList<String>();
		
 

		ManagedObjectReference rootFolder = serviceContent.getRootFolder();
		try {
			TraversalSpec tSpec = getVMTraversalSpec();
			// Create Property Spec
			PropertySpec propertySpec = new PropertySpec();
			propertySpec.setAll(Boolean.FALSE);
			propertySpec.getPathSet().add("name");
			propertySpec.setType("VirtualMachine");

			// Now create Object Spec
			ObjectSpec objectSpec = new ObjectSpec();
			objectSpec.setObj(rootFolder);
			objectSpec.setSkip(Boolean.TRUE);
			objectSpec.getSelectSet().add(tSpec);

			// Create PropertyFilterSpec using the PropertySpec and ObjectPec
			// created above.et
			PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
			propertyFilterSpec.getPropSet().add(propertySpec);
			propertyFilterSpec.getObjectSet().add(objectSpec);

			List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
			listpfs.add(propertyFilterSpec);
			List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);

			if (listobjcont != null) {
				for (ObjectContent oc : listobjcont) {
 					String vmnm = null;
					List<DynamicProperty> dps = oc.getPropSet();
					if (dps != null) {
						for (DynamicProperty dp : dps) {
							vmnm = (String) dp.getVal();
						}
					}
					
					names.add(vmnm);
					 
				}
			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return names;
		
	}

	
	public   List<ManagedObjectReference> getVm() {
	
		List<ManagedObjectReference>  vms = new ArrayList<ManagedObjectReference>();
		
	 


		ManagedObjectReference rootFolder = serviceContent.getRootFolder();
		try {
			TraversalSpec tSpec = getVMTraversalSpec();
			// Create Property Spec
			PropertySpec propertySpec = new PropertySpec();
			propertySpec.setAll(Boolean.FALSE);
			propertySpec.getPathSet().add("name");
			propertySpec.setType("VirtualMachine");

			// Now create Object Spec
			ObjectSpec objectSpec = new ObjectSpec();
			objectSpec.setObj(rootFolder);
			objectSpec.setSkip(Boolean.TRUE);
			objectSpec.getSelectSet().add(tSpec);

			// Create PropertyFilterSpec using the PropertySpec and ObjectPec
			// created above.et
			PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
			propertyFilterSpec.getPropSet().add(propertySpec);
			propertyFilterSpec.getObjectSet().add(objectSpec);

			List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
			listpfs.add(propertyFilterSpec);
			List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);

			if (listobjcont != null) {
				for (ObjectContent oc : listobjcont) {
					ManagedObjectReference mr = oc.getObj();
 					 
						vms.add(mr);
					}
				 
			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vms;
	}

	public    List<ObjectContent> retrievePropertiesAllObjects(List<PropertyFilterSpec> listpfs)
			throws Exception {

		RetrieveOptions propObjectRetrieveOpts = new RetrieveOptions();

		List<ObjectContent> listobjcontent = new ArrayList<ObjectContent>();

		try {
			RetrieveResult rslts =
					vimPort.retrievePropertiesEx(propCollectorRef,
							listpfs,
							propObjectRetrieveOpts);
			if (rslts != null && rslts.getObjects() != null &&
					!rslts.getObjects().isEmpty()) {
				listobjcontent.addAll(rslts.getObjects());
			}
			String token = null;
			if(rslts != null && rslts.getToken() != null) {
				token = rslts.getToken();
			}
			while (token != null && !token.isEmpty()) {
				rslts = vimPort.continueRetrievePropertiesEx(propCollectorRef, token);
				token = null;
				if (rslts != null) {
					token = rslts.getToken();
					if (rslts.getObjects() != null && !rslts.getObjects().isEmpty()) {
						listobjcontent.addAll(rslts.getObjects());
					}
				}
			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
		} catch (Exception e) {
			System.out.println(" : Failed Getting Contents");
			e.printStackTrace();
		}

		return listobjcontent;
	}


	public    TraversalSpec getVMTraversalSpec() {
		// Create a traversal spec that starts from the 'root' objects
		// and traverses the inventory tree to get to the VirtualMachines.
		// Build the traversal specs bottoms up

		//Traversal to get to the VM in a VApp
		TraversalSpec vAppToVM = new TraversalSpec();
		vAppToVM.setName("vAppToVM");
		vAppToVM.setType("VirtualApp");
		vAppToVM.setPath("vm");

		//Traversal spec for VApp to VApp
		TraversalSpec vAppToVApp = new TraversalSpec();
		vAppToVApp.setName("vAppToVApp");
		vAppToVApp.setType("VirtualApp");
		vAppToVApp.setPath("resourcePool");
		//SelectionSpec for VApp to VApp recursion
		SelectionSpec vAppRecursion = new SelectionSpec();
		vAppRecursion.setName("vAppToVApp");
		//SelectionSpec to get to a VM in the VApp
		SelectionSpec vmInVApp = new SelectionSpec();
		vmInVApp.setName("vAppToVM");
		//SelectionSpec for both VApp to VApp and VApp to VM
		List<SelectionSpec> vAppToVMSS = new ArrayList<SelectionSpec>();
		vAppToVMSS.add(vAppRecursion);
		vAppToVMSS.add(vmInVApp);
		vAppToVApp.getSelectSet().addAll(vAppToVMSS);

		//This SelectionSpec is used for recursion for Folder recursion
		SelectionSpec sSpec = new SelectionSpec();
		sSpec.setName("VisitFolders");

		// Traversal to get to the vmFolder from DataCenter
		TraversalSpec dataCenterToVMFolder = new TraversalSpec();
		dataCenterToVMFolder.setName("DataCenterToVMFolder");
		dataCenterToVMFolder.setType("Datacenter");
		dataCenterToVMFolder.setPath("vmFolder");
		dataCenterToVMFolder.setSkip(false);
		dataCenterToVMFolder.getSelectSet().add(sSpec);

		// TraversalSpec to get to the DataCenter from rootFolder
		TraversalSpec traversalSpec = new TraversalSpec();
		traversalSpec.setName("VisitFolders");
		traversalSpec.setType("Folder");
		traversalSpec.setPath("childEntity");
		traversalSpec.setSkip(false);
		List<SelectionSpec> sSpecArr = new ArrayList<SelectionSpec>();
		sSpecArr.add(sSpec);
		sSpecArr.add(dataCenterToVMFolder);
		sSpecArr.add(vAppToVM);
		sSpecArr.add(vAppToVApp);
		traversalSpec.getSelectSet().addAll(sSpecArr);
		return traversalSpec;
	}



	/**
	 * Gets the data storeby vm mor.
	 *
	 * @param vmmor the vmmor
	 * @return the data storeby vm mor
	 */
	public  ManagedObjectReference[] getDataStorebyVMMor(ManagedObjectReference vmmor) {
		ManagedObjectReference[] retVal = null;
		try {
			// Create Property Spec
			PropertySpec propertySpec = new PropertySpec();
			propertySpec.setAll(Boolean.FALSE);
			propertySpec.getPathSet().add("datastore");
			propertySpec.setType("VirtualMachine");

			// Now create Object Spec
			ObjectSpec objectSpec = new ObjectSpec();
			objectSpec.setObj(vmmor);

			// Create PropertyFilterSpec using the PropertySpec and ObjectPec
			// created above.
			PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
			propertyFilterSpec.getPropSet().add(propertySpec);
			propertyFilterSpec.getObjectSet().add(objectSpec);

			PropertyFilterSpec[] propertyFilterSpecs
			= new PropertyFilterSpec[]{propertyFilterSpec};
			List<ObjectContent> listobjcont
			= retrievePropertiesAllObjects(Arrays.asList(propertyFilterSpecs));
			if (listobjcont != null) {
				for (ObjectContent oc : listobjcont) {
					List<DynamicProperty> listdps = oc.getPropSet();
					if (listdps != null) {
						for (DynamicProperty dp : listdps) {
							List<ManagedObjectReference> listmors
							= ((ArrayOfManagedObjectReference)
									dp.getVal()).getManagedObjectReference();
							ManagedObjectReference[] ds
							= listmors.toArray(new ManagedObjectReference[listmors.size()]);
							if (ds.length > 0) {
								retVal = Arrays.copyOf(ds, ds.length);
							}
						}
					}
				}
			}
		} catch (SOAPFaultException sfe) {
			sfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}


}
