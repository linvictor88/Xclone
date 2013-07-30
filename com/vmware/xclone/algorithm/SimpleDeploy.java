package com.vmware.xclone.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	public SimpleDeploy(String srcHost, ArrayList<String> dstList,
			String srcVmPath, int num, String prefix) {
		this.srcHost = srcHost;
		this.srcVMName = srcVmPath;
		this.hostVMList.put(srcHost, srcVMName);
		this.dstHostList = new ArrayList<String>(dstList);
		this.numOfVm = num;
		this.prefix = prefix;
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
				hostVMList.put(dstHostList.get(i), CreateVMName(numStart));
				fullClone.start();
				try {
					fullClone.wait();
					DeployOneHost linkedClone = new DeployOneHost(
													hostVMList.get(dstHostList.get(i)),
													dstHostList.get(i),
													numPerHost - 1,
													numStart + 1,
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
