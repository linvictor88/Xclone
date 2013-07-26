package com.vmware.vm;

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
 *VMClone
 *
 *This sample makes a template of an existing VM and
 *deploy multiple instances of this template onto a datacenter
 *
 *<b>Parameters:</b>
 *url             [required] : url of the web service
 *username        [required] : username for the authentication
 *password        [required] : password for the authentication
 *datacentername  [required] : name of Datacenter
 *vmpath          [required] : inventory path of the VMC
 *clonename       [required] : name of the clone
 *
 *targetip           targetip
 *
 *<b>Command Line:</b>
 *java com.vmware.samples.vm.VMClone --url [webserviceurl]
 *--username [username] --password [password]
 *--datacentername [DatacenterName]"
 *--vmpath [vmPath] --clonename [CloneName]
 *
 *--targetip
 *</pre>
 */
public class VMClone {


	private static String[] meTree = {
		"ManagedEntity",
		"ComputeResource",
		"ClusterComputeResource",
		"Datacenter",
		"Folder",
		"HostSystem",
		"ResourcePool",
		"VirtualMachine"
	};
	private static String[] crTree = {
		"ComputeResource",
		"ClusterComputeResource"
	};
	private static String[] hcTree = {
		"HistoryCollector",
		"EventHistoryCollector",
		"TaskHistoryCollector"
	};

	private static class TrustAllTrustManager implements javax.net.ssl.TrustManager,
	javax.net.ssl.X509TrustManager {

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;//
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
				String authType)
						throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
				String authType)
						throws java.security.cert.CertificateException {
			return;
		}
	}

	private  final ManagedObjectReference SVC_INST_REF = new ManagedObjectReference();
	private  ManagedObjectReference propCollectorRef;
	private  ManagedObjectReference rootRef;
	private  VimService vimService;
	private  VimPortType vimPort;
	private  ServiceContent serviceContent;


	private  final String SVC_INST_NAME = "ServiceInstance";


	private  boolean isConnected = false;
	private  String dataCenterName;
	private  String vmPathName;
	private  String cloneName;

	private  String  targetIp;

	private  String targetDS ;
	private  String targetPool;

	private  boolean powerOn= false;


	public VMClone(Context context)
	{
		this.propCollectorRef = context.getPropCollectorRef();
		this.rootRef = context.getRootRef();
		this.vimService = context.getVimService();
		this.vimPort = context.getVimPort();
		this.serviceContent= context.getServiceContent();
	}

	private  DatastoreSummary getDataStoreSummary(ManagedObjectReference dataStore)
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

	private  ManagedObjectReference browseDSMOR(List<ManagedObjectReference> dsMOR) {
		ManagedObjectReference dataMOR = null;
		try {
			if(dsMOR != null && dsMOR.size() > 0) {
				for (int i = 0; i < dsMOR.size(); i++) {
					DatastoreSummary ds = getDataStoreSummary(dsMOR.get(i));
					String dsname  = ds.getName();
					if(dsname.equalsIgnoreCase(targetDS)) {
						dataMOR = dsMOR.get(i);
						break;
					}
				}
			}
		} catch (SOAPFaultException sfe) {
			printSoapFaultException(sfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataMOR;
	}
	private  ManagedObjectReference getDecendentMoRef(ManagedObjectReference root,
			String type,
			String name)
					throws Exception {
		if (name == null || name.length() == 0) {
			return null;
		}

		String[][] typeinfo =
				new String[][] {new String[] {type, "name"}, };

		List<ObjectContent> ocary =
				getContentsRecursively(null, root, typeinfo, true);

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

	private static boolean typeIsA(String searchType,
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


	private static List<PropertySpec> buildPropertySpecArray(String[][] typeinfo) {
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


	private  List<ObjectContent>    getContentsRecursively(ManagedObjectReference collector,
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
				ManagedObjectReference  target  =getHostByHostName(targetIp);
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


	public  List<String> getDataStore(String targetIp)
	{

		try
		{
			ManagedObjectReference  target  =getHostByHostName(targetIp);
			List<DynamicProperty> datastoresSource
			= getDynamicProarray(target, "HostSystem", "datastore");


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
				printSoapFaultException(sfe);
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
	private  void execCloneVM() throws Exception {
		// Find the Datacenter reference by using findByInventoryPath().
		try 
		{


			ManagedObjectReference datacenterRef
			= vimPort.findByInventoryPath(
					serviceContent.getSearchIndex(), dataCenterName);
			if (datacenterRef == null) {
				System.out.printf(
						"The specified datacenter [ %s ]is not found %n", dataCenterName);
				return;
			}


			// Find the virtual machine folder for this datacenter.
			ManagedObjectReference vmFolderRef =
					(ManagedObjectReference) getDynamicProperty(datacenterRef, "vmFolder");
			if (vmFolderRef == null) {
				System.out.println("The virtual machine is not found");
				return;
			}


			ManagedObjectReference vmRef
			= vimPort.findByInventoryPath(serviceContent.getSearchIndex(), vmPathName);
			if (vmRef == null) {
				System.out.printf("The VMPath specified [ %s ] is not found %n", vmPathName);
				return;
			}

			VirtualMachineCloneSpec cloneSpec = getCloneSpec();


			System.out.printf(
					"Cloning Virtual Machine [%s] to clone name [%s] %n",
					vmPathName.substring(vmPathName.lastIndexOf("/") + 1), cloneName);

			ManagedObjectReference cloneTask
			= vimPort.cloneVMTask(vmRef, vmFolderRef, cloneName, cloneSpec);

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
			printSoapFaultException(sfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private static TraversalSpec getHostSystemTraversalSpec() {
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

	private  ManagedObjectReference getHostByHostName(String hostName) {
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
					if (hostnm != null && hostnm.equals(hostName)) {
						retVal = mr;
						break;
					}
				}
			} else {
				System.out.println("The Object Content is Null");
			}
		} catch (SOAPFaultException sfe) {
			printSoapFaultException(sfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
	/**
	 * Uses the new RetrievePropertiesEx method to emulate the now
	 * deprecated RetrieveProperties method
	 *
	 * @param listpfs
	 * @return list of object content
	 * @throws Exception
	 */
	private  List<ObjectContent> retrievePropertiesAllObjects(List<PropertyFilterSpec> listpfs) {

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
			printSoapFaultException(sfe);
		} catch (Exception e) {
			System.out.println(" : Failed Getting Contents");
			e.printStackTrace();
		}

		return listobjcontent;
	}

	private  Object getDynamicProperty(ManagedObjectReference mor,
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

	/**
	 * Retrieve contents for a single object based on the property collector
	 * registered with the service.
	 *de
	 * @param collector Property collector registered with service
	 * @param mobj Managed Object Reference to get contents for
	 * @param properties names of properties of object to retrieve
	 *
	 * @return retrieved object contents
	 */
	private  ObjectContent[] getObjectProperties(ManagedObjectReference mobj,
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
	private  boolean methodExists(Object obj,
			String methodName,
			Class[] parameterTypes) {
		boolean exists = false;
		try {
			Method method = obj.getClass().getMethod(methodName, parameterTypes);
			if (method != null) {
				exists = true;
			}
		} catch (SOAPFaultException sfe) {
			printSoapFaultException(sfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return exists;
	}

	private  Object[] waitForValues(ManagedObjectReference objmor,
			String[] filterProps,
			String[] endWaitProps,
			Object[][] expectedVals)
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
					printSoapFaultException(sfe);
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

	private  void updateValues(String[] props, Object[] vals, PropertyChange propchg) {
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

	public  void printSoapFaultException(SOAPFaultException sfe) {
		System.out.println("SOAP Fault -");
		if (sfe.getFault().hasDetail()) {
			System.out.println(sfe.getFault().getDetail().getFirstChild().getLocalName());
		}
		if (sfe.getFault().getFaultString() != null) {
			System.out.println("\n Message: " + sfe.getFault().getFaultString());
		}
	}
	private  List<DynamicProperty> getDynamicProarray(ManagedObjectReference ref,
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

	private   List<SelectionSpec> buildFullTraversal() {
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
	private   SelectionSpec getSelectionSpec(String name) {
		SelectionSpec genericSpec = new SelectionSpec();
		genericSpec.setName(name);
		return genericSpec;
	}


	//parameters input:

	// 
	/***
	 * url             [required] : url of the web service
	 *username        [required] : username for the authentication
	 *password        [required] : password for the authentication
	 *datacentername  [required] : name of Datacenter
	 *vmpath          [required] : inventory path of the VM
	 *clonename       [required] : name of the clone
	 *
	 *targetip           targetip
	 *
	 *<b>Command Line:</b>
	 *java com.vmware.samples.vm.VMClone --url [webserviceurl]
	 *--username [username] --password [password]
	 *--datacentername [DatacenterName]"
	 *--vmpath [vmPath] --clonename [CloneName]
	 *
	 *--targetip***/


	private   void parseParams(CloneParam param)
	{
 		vmPathName= param.getVmPath();

		cloneName = param.getCloneName();

		targetIp = param.getTargetIp();
 
		targetDS =param.getTargetDs();
		targetPool=param.getResPool();
		powerOn = param.isPowerOn();
		dataCenterName = param.getDataCenter();

	}


	public   boolean cloneVM(CloneParam param)
	{

		/**HashMap<String,String> item = new HashMap<String,String*/

		try {
			parseParams(param);
			execCloneVM();
		} catch (SOAPFaultException sfe) {
			printSoapFaultException(sfe);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return true;
	}

	public void setParam(CloneParam param)
	{
		vmPathName= param.getVmPath();

		cloneName = param.getCloneName();

		targetIp = param.getTargetIp();
 
		targetDS =param.getTargetDs();
		targetPool=param.getResPool();
		powerOn = param.isPowerOn();
	}

	public static void main(String[] args) {


		try
		{

			ArrayList<String> targetMachines = new ArrayList<String>();
			targetMachines.add("10.117.4.140");
			targetMachines.add("10.117.4.71");
			targetMachines.add("10.117.5.148");
			targetMachines.add("10.117.5.78");
			targetMachines.add("10.117.7.125");

			for(int i = 0, count = targetMachines.size(); i < count; i++){

				DeployOneHost deployOneHost =  new DeployOneHost(targetMachines.get(i), 20);
				deployOneHost.start();
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
 
