package com.vmware.xclone.algorithm;

import java.util.ArrayList;
import java.util.List;

public class SimpleDeploy {
	private String srcHost;
	private String srcVMName;
	private List<String> dstHostList;
	private int numOfVm;
	
	public SimpleDeploy(String srcHost, ArrayList<String> dstList,
			String srcVmPath, int num, String prefix) {
		this.srcHost = srcHost;
		this.srcVMName = srcVmPath;
		this.dstHostList = new ArrayList<String>(dstList);
		this.numOfVm = num;
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
													true);
				linkedClone.start();
			} else {
				DeployOneHost fullClone = new DeployOneHost(
													srcVMName, 
													dstHostList.get(i), 
													1,
													numStart, 
													false);
				fullClone.start();
				try {
					fullClone.wait();
					DeployOneHost linkedClone = new DeployOneHost(
													srcVMName,
													dstHostList.get(i),
													numPerHost - 1,
													numStart,
													true);
					linkedClone.start();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			numStart = numStart + numPerHost;
		}
	}
}
