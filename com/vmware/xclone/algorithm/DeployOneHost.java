package com.vmware.xclone.CloningAlgorithms;

import java.util.List;

import javax.naming.Context;

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
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UID;

import javax.xml.ws.soap.SOAPFaultException;

import com.vmware.xclone.UserInterface;
import com.vmware.xclone.basicops.*;

public class DeployOneHost extends Thread {
	private String srcVMName;
	private String dstHostIp1;
	private int numToDeploy;
	private int numStart;
	private int deployedVM = 0;
	private Context context;
	private UserInterface ui;
	private int flage;

	public int getFlage() {
		return flage;
	}

	public void setFlage(int flage) {
		this.flage = flage;
	}

	public String getSrcVMName() {
		return srcVMName;
	}

	public void setSrcVMName(String srcVMName) {
		this.srcVMName = srcVMName;
	}

	public String getDstHostIp() {
		return dstHostIp1;
	}

	public void setDstHostIp(String dstHostIp) {
		this.dstHostIp1 = dstHostIp;
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

	public String getDstHostIp1() {
		return dstHostIp1;
	}

	public void setDstHostIp1(String dstHostIp1) {
		this.dstHostIp1 = dstHostIp1;
	}

	public UserInterface getUi() {
		return ui;
	}

	public void setUi(UserInterface ui) {
		this.ui = ui;
	}

	public void DeployOneHost(String srcVMName, String dstHostIp,
			int numToDeploy, int numStart, int flag) {

		ui = UserInterface.getInstance();
		setUi(ui);
		setSrcVMName(srcVMName);
		setDstHostIp(dstHostIp);
		setNumToDeploy(numToDeploy);
		setNumStart(numStart);
		setDeployedVM(0);
		setFlag(flag);

		try {
			VCConnection conn = new VCConnection(ui.getVcUrl(),
					ui.getUserName(), ui.getPassWord());

			conn.connect();

			self.setContext(conn.getContext);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {

		if (getFlag == 0) {
			try {
				CloneParam param = new CloneParam();
				param.setDataCenter("Datacenter");
				param.setResPool("cluster");
				String firstVM = CreateVMName();
				param.setCloneName(firstVM);
				// Todo(Qinghe Jin: need to get datastore by hostip)

				param.setTargetIp(getDstHostIp());
				String VmPath = ui.getDataCenter() + "/vm/" + getSrcVMName();
				param.setVmPath(VmPath);
				// ToDo(linb): wheter it is a templat vm

				VMClone t = new VMClone(getContext());
				t.setParam(param);
				List<String> names = t.getDataStore(getDstHostIp());
				param.setTargetDs(names.get(2));

				new VMClone(context).cloneVM(param);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			LinkedParam linkedParam = new LinkedParam();
			linkedParam.setVmName(srcVMName);
			linkedParam.setDataCenter(ui.getDataCenter());
			linkedParam.setDesc("linked clone on " + getDstHostIp());
			linkedParam.setSnapshotName("snapshotOn__" + getDstHostIp());
			if (ui.getIsOn() == true) {
				linkedParam.setPowerOn(true);
			}
			for (int i = 0; i < getNumToDeploy(); i++) {
				linkedParam.setCloneName(CreateVMName());
				VMLinkedClone linkedClone = new VMLinkedClone(context);
				linkedClone.linkedCloneVM(linkedParam);
			}

		}
	}

	private String CreateVMName() {
		deployedVM++;
		int numth = deployedVM + numStart;
		return ui.getVmClonePrefix() + String.format("%03d", numth);
	}

}
