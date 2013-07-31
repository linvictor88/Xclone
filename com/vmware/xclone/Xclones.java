package com.vmware.xclone;

import com.vmware.vim25.*;
import com.vmware.xclone.algorithm.*;
import com.vmware.xclone.managementobjects.*;
import com.vmware.xclone.basicops.*;

import javax.xml.ws.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.util.*;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.rmi.RemoteException;

import javax.xml.ws.soap.SOAPFaultException;

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
		    		+ "--datacentername Datacenter --vmname vm_clone --cloneprefix clone_ "
		    		+ "--resourcepool cluster "
		    		+ "--number 100 --dsthosts 10.117.4.71,10.117.5.148,10.117.7.125,10.117.4.140,10.117.5.78,10.117.4.14 --srchost 10.117.4.140 --acceptlinked true "
		    		+ "--ison false --algthselect 1 --opselect destroy";
	
		    String[] Params = inputString.split("\\s+");
		    UserInterface ui = UserInterface.getInstance(Params);
		    String ops = ui.getOpselect();
		    if (ops.equalsIgnoreCase("destroy")) {
                int i=0;
				for (i=0; i<ui.getNumberOfVMs(); i++) {
                    String vmname = ui.getVmClonePrefix() + String.format("%03d", i);
					PoweroffVM offTask = new PoweroffVM(vmname);
					offTask.start();
					i++;
				}
				for (i=0; i<ui.getNumberOfVMs(); i++) {
                    String vmname = ui.getVmClonePrefix() + String.format("%03d", i);
					DeleteVM offTask = new DeleteVM(vmname);
					offTask.start();
					i++;
				}
		    } else if (ops.equalsIgnoreCase("stop")) {
                int i=0;
				for (i=0; i<ui.getNumberOfVMs(); i++) {
                    String vmname = ui.getVmClonePrefix() + String.format("%03d", i);
					PoweroffVM offTask = new PoweroffVM(vmname);
					offTask.start();
					i++;
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