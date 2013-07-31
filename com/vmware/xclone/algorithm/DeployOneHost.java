package com.vmware.xclone.algorithm;

import java.util.List;

import com.vmware.xclone.basicops.CloneParam;
import com.vmware.xclone.basicops.VCConnection;
import com.vmware.vim25.*;
import com.vmware.xclone.basicops.BasicOps;
import com.vmware.xclone.managementobjects.ManagementObjects;

import javax.xml.ws.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UID;

import javax.xml.ws.soap.SOAPFaultException;

import com.vmware.xclone.*;

import com.vmware.xclone.basicops.*;

public class DeployOneHost extends Thread {
	private String srcVMName;

	private String dstHostIp;
	private int numToDeploy;
	private int numStart;
	private int deployedVM = 0;
	private Context context;
	private UserInterface ui;
	private boolean isLinked;

	private CountDownLatch latch;
	private VCConnection conn;

	public DeployOneHost(String srcVMName, String dstHostIp, int numToDeploy,
			int numStart, boolean isLinked, CountDownLatch latch) {

		ui = UserInterface.getInstance(null);
		setUi(ui);
		setSrcVMName(srcVMName);
		setDstHostIp(dstHostIp);
		setNumToDeploy(numToDeploy);
		setNumStart(numStart);
		setIsLinked(isLinked);
		this.latch = latch;

		try {
			conn = new VCConnection(ui.getVcUrl(), ui.getUserName(),
					ui.getPassWord());

			conn.connect();

			this.setContext(conn.getContext());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {

		ManagementObjects managementObject = new ManagementObjects(ui, conn);

		if (getIsLinked() == false) {
			try {
				// CloneParam param = new CloneParam();
				// param.setDataCenter("Datacenter");
				// param.setResPool("cluster");
				// String firstVM = CreateVMName();
				// param.setCloneName(firstVM);
				// // Todo(Qinghe Jin: need to get datastore by hostip)
				//
				// param.setTargetIp(getDstHostIp());
				// String VmPath = ui.getDataCenter() + "/vm/" + getSrcVMName();
				// param.setVmPath(VmPath);
				// ToDo(linb): wheter it is a templat vm


				List<String> names = managementObject
						.getDatastoreNameByHostIp(dstHostIp);

				new CloneVM(srcVMName, CreateVMName(), dstHostIp, names.get(2),
						ui.getResourcePool(),ui.getDataCenter(), ui.getIsOn(), conn,managementObject).doClone();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			String vmName = srcVMName.split("/")[2];
			for (int i = 0; i < getNumToDeploy(); i++) {
				LinkedCloneVm linkedVm = new LinkedCloneVm(conn,
						managementObject.getVMByName(vmName), "snapshot",
						managementObject, CreateVMName(), vmName);
				linkedVm.linkedCloneVM();
			}

		}
		
		latch.countDown();
		
	}

	public Boolean getIsLinked() {
		return isLinked;
	}

	public void setIsLinked(Boolean isLinked) {
		this.isLinked = isLinked;
	}

	public String getSrcVMName() {
		return srcVMName;
	}

	public void setSrcVMName(String srcVMName) {
		this.srcVMName = srcVMName;
	}

	public String getDstHostIp() {
		return dstHostIp;
	}

	public void setDstHostIp(String dstHostIp) {
		this.dstHostIp = dstHostIp;
	}

	public int getNumToDeploy() {
		return numToDeploy;
	}

	public void setNumToDeploy(int numToDeploy) {
		this.numToDeploy = numToDeploy;
	}

	public int getNumStart() {
		return numStart;
	}

	public void setNumStart(int numStart) {
		this.numStart = numStart;
	}

	public int getDeployedVM() {
		return deployedVM;
	}

	public void setDeployedVM(int deployedVM) {
		this.deployedVM = deployedVM;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getdstHostIp() {
		return dstHostIp;
	}

	public void setdstHostIp(String dstHostIp) {
		this.dstHostIp = dstHostIp;
	}

	public UserInterface getUi() {
		return ui;
	}

	public void setUi(UserInterface ui) {
		this.ui = ui;
	}

	private String CreateVMName() {
		int numth = deployedVM + numStart;
		deployedVM++;
		return ui.getVmClonePrefix() + String.format("%03d", numth);
	}

}
