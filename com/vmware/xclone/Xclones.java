package com.vmware.xclone;
import com.vmware.xclone.algorithm.*;
import com.vmware.xclone.basicops.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
/**
 *<pre>
 *Xclone
 *
 *Fastest VM Provision
 *
 *<b>Parameters:</b>
 *url            [required] : url of the web service.
 *username       [optional] : username for the authentication (default: root)
 *password       [optional] : password for the authentication (default: vmware)
 *datacentername [optional] : name of the datacenter (default: Datacenter)
 *resourcepool   [optional] : name of the resource pool in the datacenter (default: cluster)
 *vmname         [required] : Name of the virtual machine
 *cloneprefix    [required] : prefix of the cloned virtual machine
 *number         [required] : the number of cloned virtual machines
 *srchost        [required] : the Ip of src host
 *dsthosts       [required] : the Ip of all dst hosts
 *acceptlinked   [optional] : Whether accept Linked clone (default: True)
 *isOn           [optional] : Whether Power on the VM after cloning (default: True)
 *algthselect    [optional] : select which algorithm to deploy the VMs [0 | 1 | 2| 3| ...] and default: 1
 *opselect       [optional] : select which operations (create(default) | start | stop | destroy)
 *<b>Example:</b>
 *Create example:
 *<<                
 *                    "--url 10.117.4.228 --username root --password vmware "
 *		    		+ "--datacentername Datacenter --vmname vm_clone --cloneprefix clone_ "
 *		    		+ "--resourcepool cluster "
 *		    		+ "--number 100 --dsthosts 10.117.4.71,10.117.5.148,10.117.7.125,10.117.4.140,10.117.5.78,10.117.4.14 --srchost 10.117.4.140 --acceptlinked true "
 *		    		+ "--ison false --algthselect 1"
 *>>
 *Stop VMs example:
 *<<
 *                    "--url 10.117.4.228 --username root --password vmware "
 *     	    		+ "--datacentername Datacenter --vmname vm_clone --cloneprefix clone_ "
 *		    		+ "--resourcepool cluster "
 *		    		+ "--number 100 --dsthosts 10.117.4.71,10.117.5.148,10.117.7.125,10.117.4.140,10.117.5.78,10.117.4.14 --srchost 10.117.4.140 --acceptlinked true "
 *		    		+ "--ison false --algthselect 1 --opselect stop"
 *>>
 *Destroy VMs example:
 *<<
 *                    "--url 10.117.4.228 --username root --password vmware "
 *		    		+ "--datacentername Datacenter --vmname vm_clone --cloneprefix tttclone_ "
 *		    		+ "--resourcepool cluster "
 *		    		+ "--number 160 --dsthosts 10.117.4.71,10.117.5.148,10.117.7.125,10.117.4.140,10.117.5.78,10.117.4.14 --srchost 10.117.4.140 --acceptlinked true "
 *		    		+ "--ison false --algthselect 1 --opselect destroy";
 *>>
 *</pre>
 */


public class Xclones {
	private UserInterface ui;

	public UserInterface getUi() {
		return ui;
	}

	public void setUi(UserInterface ui) {
		this.ui = ui;
	}

	public Xclones(UserInterface ui) {
		this.setUi(ui);
	}

	public static void main(String[] args) {
		try {
			//private static CloningAlgorithms cAlgorithm;
		    String inputString = "--url 10.117.4.228 --username root --password vmware "
		    		+ "--datacentername Datacenter --vmname vm_clone --cloneprefix clone_ "
		    		+ "--resourcepool cluster "
		    		+ "--number 100 --dsthosts 10.117.4.71,10.117.5.148,10.117.7.125,10.117.4.140,10.117.5.78,10.117.4.14 --srchost 10.117.4.140 --acceptlinked true "
		    		+ "--ison false --algthselect 1";
		    
		    //offString opselect: create | start | stop | destroy
		    String stopString = "--url 10.117.4.228 --username root --password vmware "
		    		+ "--datacentername Datacenter --vmname vm_clone --cloneprefix clone_ "
		    		+ "--resourcepool cluster "
		    		+ "--number 100 --dsthosts 10.117.4.71,10.117.5.148,10.117.7.125,10.117.4.140,10.117.5.78,10.117.4.14 --srchost 10.117.4.140 --acceptlinked true "
		    		+ "--ison false --algthselect 1 --opselect stop";
		    
		    String startString =  "--url 10.117.4.228 --username root --password vmware "
		    		+ "--datacentername Datacenter --vmname vm_clone --cloneprefix clone_ "
		    		+ "--resourcepool cluster "
		    		+ "--number 100 --dsthosts 10.117.4.71,10.117.5.148,10.117.7.125,10.117.4.140,10.117.5.78,10.117.4.14 --srchost 10.117.4.140 --acceptlinked true "
		    		+ "--ison false --algthselect 1 --opselect start";
		    
		    String destroyString = "--url 10.117.4.228 --username root --password vmware "
		    		+ "--datacentername Datacenter --vmname vm_clone --cloneprefix tttclone_ "
		    		+ "--resourcepool cluster "
		    		+ "--number 160 --dsthosts 10.117.4.71,10.117.5.148,10.117.7.125,10.117.4.140,10.117.5.78,10.117.4.14 --srchost 10.117.4.140 --acceptlinked true "
		    		+ "--ison false --algthselect 1 --opselect destroy";
	
		    String[] Params = inputString.split("\\s+");
		
		    UserInterface ui = UserInterface.getInstance(Params);
		    String ops = ui.getOpselect();
		    int perNumDel = 6;
		    if (ops.equalsIgnoreCase("destroy")) {
		    	int i=0;
		    	int threadNum = (ui.getNumberOfVMs() + perNumDel - 1)/perNumDel;
		    	CountDownLatch latch = new CountDownLatch(threadNum);
		    	for(i=0; i<ui.getNumberOfVMs(); i=i+perNumDel)
		    	{
					PoweroffVM offTask = new PoweroffVM(ui.getVmClonePrefix(), i, perNumDel);
					offTask.start();
		    	}
		    	latch.wait();
		    	for(i=0; i<ui.getNumberOfVMs(); i=i+perNumDel)
		    	{
					DeleteVM delTask = new DeleteVM(ui.getVmClonePrefix(), i, perNumDel);
					delTask.start();
		    	}

		    } else if (ops.equalsIgnoreCase("stop")) {
		    	int i=0;
		    	for(i=0; i<ui.getNumberOfVMs(); i=i+perNumDel)
		    	{
					PoweroffVM offTask = new PoweroffVM(ui.getVmClonePrefix(), i, perNumDel);
					offTask.start();
		    	}
			} else {
				switch(ui.getAlgthSelect())
				{
					case 1: 
						TreeDeployVm deployVm = new TreeDeployVm(ui);
						deployVm.DeployVmFromList();
						break;
					case 2:
						SimpleDeploy sdeployVm = new SimpleDeploy(ui);
						sdeployVm.run();
						break;
					default:
						System.out.println("Stupid Boy,  you Exceed our alg selection!");
				}

			}		    	
	} catch(Exception e) {
		e.printStackTrace();


	}
}

class vmStatus extends Thread {
	public Map<String, Boolean> statusMap;

	public vmStatus(List<String> vmList) {
		statusMap = new HashMap<String, Boolean>();
		if (vmList != null) {
			for (String vm : vmList) {
				statusMap.put(vm, false);
			}
		}
	}

	public void run() {
		boolean check = true;
		int onNum = 0;
		try {
			while (check) {
				Thread.sleep(3000);

				for (Map.Entry<String, Boolean> s : statusMap.entrySet()) {
					if (s.getValue()) {
						onNum++;
						// sent poweron msg to UI

					}
				}

				if (onNum == statusMap.size()) {
					check = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
}