package com.vmware.xclone.algorithm;

import com.vmware.vim25.*;
import com.vmware.xclone.UserInterface;

import javax.swing.text.html.HTMLDocument.Iterator;
import javax.xml.ws.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.rmi.RemoteException;

import javax.xml.ws.soap.SOAPFaultException;

public class TreeDeployVm {

	private Map<String, String> srcHostVm;
	private List<String> dstHostList;
	private int numOfVm;
	private String prefix;
	private String datacenter;

	private String CreateVMName(int numth) {
		return prefix + String.format("%03d", numth);
	}

	public TreeDeployVm(UserInterface ui) {
		this.srcHostVm = new HashMap<String, String>();
		srcHostVm.put(ui.getSrcHost(), ui.getVmPath());
		this.dstHostList = new ArrayList<String>(ui.getDstHostList());
		this.numOfVm = ui.getNumberOfVMs() / ui.getDstHostList().size() + 1;
		this.prefix = ui.getVmClonePrefix();
		this.datacenter = ui.getDataCenter();
	}

	public void DeployVmFromList() {

		try {
			int numStart = 0;
			while (!dstHostList.isEmpty()) {
				int srcNum = srcHostVm.size();
				int dstNum = dstHostList.size();
				int tmpStart = numStart;

				int tmpDeployNum = (srcNum < dstNum) ? srcNum : dstNum;
				CountDownLatch latch = new CountDownLatch(tmpDeployNum);
				int i = 0;

				for (Map.Entry<String, String> deployStr : srcHostVm.entrySet()) {
					DeployOneHost deployTask = new DeployOneHost(
							deployStr.getValue(), dstHostList.get(i), numOfVm,
							numStart, false,latch);
					deployTask.start();
					i++;
					numStart += numOfVm;
					if (i == tmpDeployNum) {
						break;
					}
				}
				// wait until full clone threads all complete.
				// add the finished full clone hosts to srcHostList
				latch.await();
				for (int j = 0; j < i; j++) {
					srcHostVm.put(dstHostList.get(0), datacenter + "/vm/"
							+ CreateVMName(tmpStart + numOfVm * j));
					dstHostList.remove(0);
				}
			}
			// linked clone the scrHostList vm
			int i = 0;
			CountDownLatch latch = new CountDownLatch(srcHostVm.size());
			for (Map.Entry<String, String> deployStr : srcHostVm.entrySet()) {
				DeployOneHost deployTask = new DeployOneHost(
						deployStr.getValue(), deployStr.getKey(), numOfVm - 1,
						i * numOfVm + 1, true, latch);
				deployTask.start();
				i++;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
