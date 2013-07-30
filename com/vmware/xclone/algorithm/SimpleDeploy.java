package com.vmware.xclone.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vmware.xclone.UserInterface;

public class SimpleDeploy {
	private String srcHost;
	private String srcVMName;
	private List<String> dstHostList;
	private String prefix;
	private Map<String, String> hostVMList;
	private int numOfVm;
	
	private String CreateVMName(int numth) {
		return prefix + String.format("%03d", numth);
	}
	
	public SimpleDeploy(UserInterface ui) {
		this.srcHost = ui.getSrcHost();
		this.srcVMName = ui.getVmPath();
		this.hostVMList.put(ui.getSrcHost(), ui.getVmPath());
		this.dstHostList = new ArrayList<String>(ui.getDstHostList());
		this.numOfVm = ui.getNumberOfVMs()/ui.getDstHostList().size();
		this.prefix = ui.getVmClonePrefix();
	}

	public void run() {
		int numStart = 0;
		
		assert(dstHostList.size() != 0);
		int numPerHost = numOfVm / dstHostList.size() + 1;
		for (int count = dstHostList.size(), i = 0; i < count; i++) {
			if (dstHostList.get(i).equals(srcHost)) {
				DeployOneHost linkedClone = new DeployOneHost(
													srcVMName, 
													dstHostList.get(i), 
													numPerHost - 1,
													numStart, 
													true,null);
				linkedClone.start();
			} else {
				DeployOneHost fullClone = new DeployOneHost(
													srcVMName, 
													dstHostList.get(i), 
													1,
													numStart, 
													false,null);
				hostVMList.put(dstHostList.get(i), CreateVMName(numStart));
				fullClone.start();
				try {
					fullClone.wait();
					DeployOneHost linkedClone = new DeployOneHost(
													hostVMList.get(dstHostList.get(i)),
													dstHostList.get(i),
													numPerHost - 1,
													numStart + 1,
													true, null);
					linkedClone.run();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			numStart = numStart + numPerHost;
		}
	}
}
