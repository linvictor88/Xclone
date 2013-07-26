package com.vmware.xclone;

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

public class Xclones {

	private UserInterface ui;
	private ManagementObjects mo;
	private BasicOps bo;
	private CloningAlgorithms cAlgorithm;

	private String DeployVMInCluster()
	{
		
	}

	public static void main(String[] args) {
		try {
			String initMsg = ui.initParams(args);
			if (initMsg== null) {
				vmStatus status = new vmStatus(mo.vmClonedList);
				status.run();
				
				String deployMsg = DeployVMInCluster();
				if (deployMsg != null) {
					//return error msg to UI

				} else {
					//return OK
				}
			} esle {
				// send error msg to UI

			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

class vmStatus extends Thread{
	public Map<String,Boolean> statusMap;

	public vmStatus(List<String> vmList)
	{
		statusMap = new HashMap<String,Boolean>();
		if (vmList != null) {
			for(String vm : vmList){
				statusMap.put(vm,false);
			}
		}
	}

	public void run()
	{
		boolean check = true;
		int onNum =0;
		try{
			while(check) {
				Thread.sleep(3000);

				for(Map.Entry<String,Boolean> s : statusMap.entrySet()) {
					if(s.getValue()) {
						onNum++;
						//sent poweron msg to UI

					}
				}

				if (onNum == statusMap.size()){
					check = false;
				}			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}