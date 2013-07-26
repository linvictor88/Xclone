package com.vmware.xclone.basicops;

import com.vmware.xclone.*;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.ws.soap.SOAPFaultException;

import com.vmware.vim25.*;

public class LinkedCloneVm {
	// VCConnection vcc, ManagedObjectReference vmMOR, String snapshotName,
	// ManagementObjects mo, UserInterface ui
	private static VCConnection lvcc;

	public void printSoapFaultException(SOAPFaultException sfe) {
		System.out.println("SOAP Fault -");
		if (sfe.getFault().hasDetail()) {
			System.out.println(sfe.getFault().getDetail().getFirstChild()
					.getLocalName());
		}
		if (sfe.getFault().getFaultString() != null) {
			System.out
					.println("\n Message: " + sfe.getFault().getFaultString());
		}
	}

	private void updateValues(String[] props, Object[] vals,
			PropertyChange propchg) {
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

	private Object[] waitForValues(ManagedObjectReference objmor,
			String[] filterProps, String[] endWaitProps, Object[][] expectedVals)
			throws RemoteException, Exception {
		// version string is initially null
		String version = "";
		Object[] endVals = new Object[endWaitProps.length];
		Object[] filterVals = new Object[filterProps.length];

		PropertyFilterSpec spec = new PropertyFilterSpec();

		spec.getObjectSet().add(new ObjectSpec());

		spec.getObjectSet().get(0).setObj(objmor);

		spec.getPropSet().addAll(
				Arrays.asList(new PropertySpec[] { new PropertySpec() }));

		spec.getPropSet().get(0).getPathSet()
				.addAll(Arrays.asList(filterProps));

		spec.getPropSet().get(0).setType(objmor.getType());

		// .spec.getObjectSet().get(0).getSelectSet().add(null);

		spec.getObjectSet().get(0).setSkip(Boolean.FALSE);

		ManagedObjectReference filterSpecRef = lvcc.getVimPort().createFilter(
				lvcc.getPropRef(), spec, true);

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
					updateset = lvcc.getVimPort().waitForUpdates(
							lvcc.getPropRef(), version);
					retry = false;
				} catch (SOAPFaultException sfe) {
					printSoapFaultException(sfe);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (updateset != null) {
				version = updateset.getVersion();
			}
			if (updateset == null || updateset.getFilterSet() == null) {
				continue;
			}
			List<PropertyFilterUpdate> listprfup = updateset.getFilterSet();
			filtupary = listprfup.toArray(new PropertyFilterUpdate[listprfup
					.size()]);
			filtup = null;
			for (int fi = 0; fi < filtupary.length; fi++) {
				filtup = filtupary[fi];
				List<ObjectUpdate> listobjup = filtup.getObjectSet();
				objupary = listobjup
						.toArray(new ObjectUpdate[listobjup.size()]);
				objup = null;
				propchgary = null;
				for (int oi = 0; oi < objupary.length; oi++) {
					objup = objupary[oi];
					if (objup.getKind() == ObjectUpdateKind.MODIFY
							|| objup.getKind() == ObjectUpdateKind.ENTER
							|| objup.getKind() == ObjectUpdateKind.LEAVE) {
						List<PropertyChange> listchset = objup.getChangeSet();
						propchgary = listchset
								.toArray(new PropertyChange[listchset.size()]);
						for (int ci = 0; ci < propchgary.length; ci++) {
							propchg = propchgary[ci];
							updateValues(endWaitProps, endVals, propchg);
							updateValues(filterProps, filterVals, propchg);
						}
					}
				}
			}
			Object expctdval = null;
			// Check if the expected values have been reached and exit the loop
			// if done.
			// Also exit the WaitForUpdates loop if this is the case.
			for (int chgi = 0; chgi < endVals.length && !reached; chgi++) {
				for (int vali = 0; vali < expectedVals[chgi].length && !reached; vali++) {
					expctdval = expectedVals[chgi][vali];
					reached = expctdval.equals(endVals[chgi]) || reached;
				}
			}
		}

		// Destroy the filter when we are done.
		lvcc.getVimPort().destroyPropertyFilter(filterSpecRef);
		return filterVals;
	}

	private boolean createSnapshot(VCConnection vcc,
			ManagedObjectReference vmMor, String snapName) throws Exception {

		ManagedObjectReference taskMor = vcc.getVimPort().createSnapshotTask(
				vmMor, snapName, null, false, false);
		if (taskMor != null) {
			String[] opts = new String[] { "info.state", "info.error",
					"info.progress" };
			String[] opt = new String[] { "state" };
			Object[] results = waitForValues(taskMor, opts, opt,
					new Object[][] { new Object[] { TaskInfoState.SUCCESS,
							TaskInfoState.ERROR } });

			// Wait till the task completes.
			if (results[0].equals(TaskInfoState.SUCCESS)) {
				System.out.printf(" Creating Snapshot - [ %s ] Successful %n",
						snapName);
				return Boolean.TRUE;
			} else {
				System.out.printf(" Creating Snapshot - [ %s ] Failure %n",
						snapName);
				return Boolean.FALSE;
			}
		}
		return false;
	}

	/**
	 * Traverse snapshot in tree.
	 * 
	 * @param snapTree
	 *            the snap tree
	 * @param findName
	 *            the find name
	 * @return the managed object reference
	 */
	private ManagedObjectReference traverseSnapshotInTree(
			List<VirtualMachineSnapshotTree> snapTree, String findName) {
		ManagedObjectReference snapmor = null;
		if (snapTree == null) {
			return snapmor;
		}
		for (int i = 0; i < snapTree.size() && snapmor == null; i++) {
			VirtualMachineSnapshotTree node = snapTree.get(i);
			if (findName != null && node.getName().equals(findName)) {
				snapmor = node.getSnapshot();
			} else {
				List<VirtualMachineSnapshotTree> listvmsst = node
						.getChildSnapshotList();
				List<VirtualMachineSnapshotTree> childTree = listvmsst;
				snapmor = traverseSnapshotInTree(childTree, findName);
			}
		}
		return snapmor;
	}

	/**
	 * Retrieve contents for a single object based on the property collector
	 * registered with the service.
	 * 
	 * @param collector
	 *            Property collector registered with service
	 * @param mobj
	 *            Managed Object Reference to get contents for
	 * @param properties
	 *            names of properties of object to retrieve
	 * 
	 * @return retrieved object contents
	 */
	private ObjectContent[] getObjectProperties(ManagedObjectReference mobj,
			String[] properties) throws Exception {
		if (mobj == null) {
			return null;
		}
		PropertyFilterSpec spec = new PropertyFilterSpec();
		spec.getPropSet().add(new PropertySpec());
		if ((properties == null || properties.length == 0)) {
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
	 * 
	 * @param obj
	 *            The Object to check
	 * @param methodName
	 *            The method name
	 * @param parameterTypes
	 *            Array of Class objects for the parameter types
	 * @return true if the method exists, false otherwise
	 */
	private boolean methodExists(Object obj, String methodName,
			Class[] parameterTypes) {
		boolean exists = false;
		try {
			Method method = obj.getClass()
					.getMethod(methodName, parameterTypes);
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

	private Object getDynamicProperty(ManagedObjectReference mor,
			String propertyName) throws Exception {
		ObjectContent[] objContent = getObjectProperties(mor,
				new String[] { propertyName });

		Object propertyValue = null;
		if (objContent != null) {
			List<DynamicProperty> listdp = objContent[0].getPropSet();
			if (listdp != null && listdp.size() > 0) {
				/*
				 * Check the dynamic propery for ArrayOfXXX object
				 */
				Object dynamicPropertyVal = listdp.get(0).getVal();
				String dynamicPropertyName = dynamicPropertyVal.getClass()
						.getName();
				if (dynamicPropertyName.indexOf("ArrayOf") != -1) {
					String methodName = dynamicPropertyName.substring(
							dynamicPropertyName.indexOf("ArrayOf")
									+ "ArrayOf".length(),
							dynamicPropertyName.length());
					/*
					 * If object is ArrayOfXXX object, then get the XXX[] by
					 * invoking getXXX() on the object. For Ex:
					 * ArrayOfManagedObjectReference.getManagedObjectReference()
					 * returns ManagedObjectReference[] array.
					 */
					if (methodExists(dynamicPropertyVal, "get" + methodName,
							null)) {
						methodName = "get" + methodName;
					} else {
						/*
						 * Construct methodName for ArrayOf primitive types Ex:
						 * For ArrayOfInt, methodName is get_int
						 */
						methodName = "get_" + methodName.toLowerCase();
					}
					Method getMorMethod = dynamicPropertyVal.getClass()
							.getDeclaredMethod(methodName, (Class[]) null);
					propertyValue = getMorMethod.invoke(dynamicPropertyVal,
							(Object[]) null);
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
	 * Gets the snapshot reference.
	 * 
	 * @param vmmor
	 *            the vmmor
	 * @param snapName
	 *            the snap name
	 * @return the snapshot reference
	 * @throws Exception
	 *             the exception
	 */
	private ManagedObjectReference getSnapshotReference(
			ManagedObjectReference vmmor, String snapName) throws Exception {
		VirtualMachineSnapshotInfo snapInfo = (VirtualMachineSnapshotInfo) getDynamicProperty(
				vmmor, "snapshot");
		ManagedObjectReference snapmor = null;
		if (snapInfo != null) {
			List<VirtualMachineSnapshotTree> listvmsst = snapInfo
					.getRootSnapshotList();
			List<VirtualMachineSnapshotTree> snapTree = listvmsst;
			snapmor = traverseSnapshotInTree(snapTree, snapName);
		}
		return snapmor;
	}

	/**
	 * Gets the vM hardware details.
	 * 
	 * @param mor
	 *            ManagedObjectReference of a managed object, specifically
	 *            virtual machine
	 * @return String the name of the virtual machine
	 */
	private VirtualHardware getVMHardwareDetails(ManagedObjectReference mor) {
		VirtualHardware retVal = null;
		try {
			// Create Property Spec
			PropertySpec propertySpec = new PropertySpec();
			propertySpec.setAll(Boolean.FALSE);
			propertySpec.getPathSet().add(new String("config.hardware"));
			propertySpec.setType("VirtualMachine");
			List<PropertySpec> propertySpecs = new ArrayList<PropertySpec>();
			propertySpecs.add(propertySpec);

			// Now create Object Spec
			ObjectSpec objectSpec = new ObjectSpec();
			objectSpec.setObj(mor);
			List<ObjectSpec> objectSpecs = new ArrayList<ObjectSpec>();
			objectSpecs.add(objectSpec);

			// Create PropertyFilterSpec using the PropertySpec and ObjectPec
			// created above.
			PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
			propertyFilterSpec.getPropSet().addAll(propertySpecs);
			propertyFilterSpec.getObjectSet().addAll(objectSpecs);

			List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>();
			listpfs.add(propertyFilterSpec);

			List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);
			if (listobjcont != null) {
				for (ObjectContent oc : listobjcont) {
					List<DynamicProperty> dps = oc.getPropSet();
					if (dps != null) {
						for (DynamicProperty dp : dps) {
							System.out.println(dp.getName() + " : "
									+ dp.getVal());
							retVal = (VirtualHardware) dp.getVal();
						}
					}
				}
			}
		} catch (SOAPFaultException sfe) {
			printSoapFaultException(sfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	/**
	 * Gets the independenet virtual disk keys.
	 * 
	 * @param vmMOR
	 *            the vm mor
	 * @return the independenet virtual disk keys
	 * @throws Exception
	 *             the exception
	 */
	private ArrayList getIndependenetVirtualDiskKeys(
			ManagedObjectReference vmMOR) throws Exception {
		ArrayList independenetVirtualDiskKeys = new ArrayList();
		VirtualHardware hw = getVMHardwareDetails(vmMOR);
		List<VirtualDevice> listvd = hw.getDevice();
		VirtualDevice[] deviceArray = new VirtualDevice[listvd.size()];
		for (int i = 0; i < listvd.size(); i++) {
			deviceArray[i] = (VirtualDevice) listvd.get(i);
		}

		for (int i = 0; i < deviceArray.length; i++) {
			if (deviceArray[i].getClass().getCanonicalName()
					.indexOf("VirtualDisk") != -1) {
				VirtualDisk vDisk = (VirtualDisk) deviceArray[i];
				String diskMode = "";
				if (vDisk.getBacking().getClass().getCanonicalName()
						.indexOf("VirtualDiskFlatVer1BackingInfo") != -1) {
					diskMode = ((VirtualDiskFlatVer1BackingInfo) vDisk
							.getBacking()).getDiskMode();
				} else if (vDisk.getBacking().getClass().getCanonicalName()
						.indexOf("VirtualDiskFlatVer2BackingInfo") != -1) {
					diskMode = ((VirtualDiskFlatVer2BackingInfo) vDisk
							.getBacking()).getDiskMode();
				} else if (vDisk.getBacking().getClass().getCanonicalName()
						.indexOf("VirtualDiskRawDiskMappingVer1BackingInfo") != -1) {
					diskMode = ((VirtualDiskRawDiskMappingVer1BackingInfo) vDisk
							.getBacking()).getDiskMode();
				} else if (vDisk.getBacking().getClass().getCanonicalName()
						.indexOf("VirtualDiskSparseVer1BackingInfo") != -1) {
					diskMode = ((VirtualDiskSparseVer1BackingInfo) vDisk
							.getBacking()).getDiskMode();
				} else if (vDisk.getBacking().getClass().getCanonicalName()
						.indexOf("VirtualDiskSparseVer2BackingInfo") != -1) {
					diskMode = ((VirtualDiskSparseVer2BackingInfo) vDisk
							.getBacking()).getDiskMode();
				}
				if (diskMode.indexOf("independent") != -1) {
					independenetVirtualDiskKeys.add(vDisk.getKey());
				}
			}
		}
		return independenetVirtualDiskKeys;
	}

	/**
	 * Uses the new RetrievePropertiesEx method to emulate the now deprecated
	 * RetrieveProperties method
	 * 
	 * @param listpfs
	 * @return list of object content
	 * @throws Exception
	 */
	private List<ObjectContent> retrievePropertiesAllObjects(
			List<PropertyFilterSpec> listpfs) throws Exception {

		RetrieveOptions propObjectRetrieveOpts = new RetrieveOptions();

		List<ObjectContent> listobjcontent = new ArrayList<ObjectContent>();

		try {
			RetrieveResult rslts = lvcc.getVimPort().retrievePropertiesEx(
					lvcc.getPropRef(), listpfs, propObjectRetrieveOpts);
			if (rslts != null && rslts.getObjects() != null
					&& !rslts.getObjects().isEmpty()) {
				listobjcontent.addAll(rslts.getObjects());
			}
			String token = null;
			if (rslts != null && rslts.getToken() != null) {
				token = rslts.getToken();
			}
			while (token != null && !token.isEmpty()) {
				rslts = lvcc.getVimPort().continueRetrievePropertiesEx(
						lvcc.getPropRef(), token);
				token = null;
				if (rslts != null) {
					token = rslts.getToken();
					if (rslts.getObjects() != null
							&& !rslts.getObjects().isEmpty()) {
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

	/**
	 * Gets the vM parent.
	 * 
	 * @param mor
	 *            ManagedObjectReference of a managed object, specifically
	 *            virtual machine
	 * @return String the name of the virtual machine
	 */
	private ManagedObjectReference getVMParent(ManagedObjectReference mor) {
		ManagedObjectReference retVal = null;
		try {
			// Create Property Spec
			PropertySpec propertySpec = new PropertySpec();
			propertySpec.setAll(Boolean.FALSE);
			propertySpec.getPathSet().addAll(
					Arrays.asList(new String[] { "parent" }));
			propertySpec.setType("VirtualMachine");
			List<PropertySpec> propertySpecs = new ArrayList<PropertySpec>();
			propertySpecs.add(propertySpec);

			// Now create Object Spec
			ObjectSpec objectSpec = new ObjectSpec();
			objectSpec.setObj(mor);
			List<ObjectSpec> objectSpecs = new ArrayList<ObjectSpec>();
			objectSpecs.add(objectSpec);

			// Create PropertyFilterSpec using the PropertySpec and ObjectPec
			// created above.
			PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
			propertyFilterSpec.getPropSet().addAll(propertySpecs);
			propertyFilterSpec.getObjectSet().addAll(objectSpecs);

			List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>();
			listpfs.add(propertyFilterSpec);

			List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);
			if (listobjcont != null) {
				for (ObjectContent oc : listobjcont) {
					List<DynamicProperty> dps = oc.getPropSet();
					if (dps != null) {
						for (DynamicProperty dp : dps) {
							System.out.println(dp.getName() + " : "
									+ dp.getVal());
							retVal = (ManagedObjectReference) dp.getVal();
						}
					}
				}
			}
		} catch (SOAPFaultException sfe) {
			printSoapFaultException(sfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}

	public void linkedCloneVM(VCConnection vcc, ManagedObjectReference vmMOR,
			String snapshotName, ManagementObjects mo, String cloneName, String vmName) {
		try {
			lvcc = vcc;

			if (vmMOR != null) {
				ManagedObjectReference snapMOR = getSnapshotReference(vmMOR,
						snapshotName);

				if (snapMOR == null)
					if (!createSnapshot(vcc, vmMOR, snapshotName)) {
						System.out.println("create snapshot failed");
						return;
					}

				snapMOR = getSnapshotReference(vmMOR, snapshotName);
				if (snapMOR != null) {
					ArrayList independentVirtualDiskKeys = getIndependenetVirtualDiskKeys(vmMOR);

					VirtualMachineRelocateSpec rSpec = new VirtualMachineRelocateSpec();
					if (independentVirtualDiskKeys.size() > 0) {
						ManagedObjectReference[] ds = mo.getDatastoreByVM(vmMOR);
						List<VirtualMachineRelocateSpecDiskLocator> diskLocator = new ArrayList<VirtualMachineRelocateSpecDiskLocator>();

						Iterator it = independentVirtualDiskKeys.iterator();
						int count = 0;
						while (it.hasNext()) {
							diskLocator.get(count).setDatastore(ds[0]);
							diskLocator
									.get(count)
									.setDiskMoveType(
											VirtualMachineRelocateDiskMoveOptions.MOVE_ALL_DISK_BACKINGS_AND_DISALLOW_SHARING
													.value());
							diskLocator.get(count).setDiskId(
									(Integer) it.next());
							count = count + 1;
						}
						rSpec.setDiskMoveType(VirtualMachineRelocateDiskMoveOptions.CREATE_NEW_CHILD_DISK_BACKING
								.value());
						rSpec.getDisk().addAll(diskLocator);
					} else {
						rSpec.setDiskMoveType(VirtualMachineRelocateDiskMoveOptions.CREATE_NEW_CHILD_DISK_BACKING
								.value());
					}
					VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();
					cloneSpec.setPowerOn(false);
					cloneSpec.setTemplate(false);
					cloneSpec.setLocation(rSpec);
					cloneSpec.setSnapshot(snapMOR);

					try {
						ManagedObjectReference parentMOR = getVMParent(vmMOR);
						ManagedObjectReference cloneTask = lvcc.getVimPort()
								.cloneVMTask(vmMOR, parentMOR, cloneName,
										cloneSpec);
						if (cloneTask != null) {
							String[] opts = new String[] { "info.state",
									"info.error", "info.progress" };
							String[] opt = new String[] { "state" };
							Object[] results = waitForValues(cloneTask, opts,
									opt, new Object[][] { new Object[] {
											TaskInfoState.SUCCESS,
											TaskInfoState.ERROR } });

							// Wait till the task completes.
							if (results[0].equals(TaskInfoState.SUCCESS)) {
								System.out.println(" Cloning Successful"
										+ cloneName);
							} else {
								System.out.println(" Cloning Failure"
										+ cloneName);
							}
						}
					} catch (SOAPFaultException sfe) {
						printSoapFaultException(sfe);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Snapshot " + snapshotName
							+ " doesn't exist");
				}
			} else {
				System.out.println("Virtual Machine " + vmName
						+ " doesn't exist");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
