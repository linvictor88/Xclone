package com.vmware.xclone;

import com.vmware.xclone.basicops.*;
import java.util.List;

public class DeployOneHost extends Thread{
	private String hostIp;
	private int numPerHost;
	private int deployedVM = 0;
	private String firstVM;

	private Context context;

	public DeployOneHost(String hostIp, int numPerHost){
		this.hostIp = hostIp;
		this.numPerHost = numPerHost;


		try
		{
			 
			VCConnection  conn = new VCConnection("https://10.117.4.228/sdk","root","vmware");
			conn.connect();
			this.context =  conn.getContext();

		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public void setContext(Context context)
	{
		this.context = context;
	}

	private String CreateVMName(){
		deployedVM++;
		return "vm" + hostIp +"_dujizhi__"+ String.format("%03d",deployedVM);
	}


	public void run(){
		//vm clone
		//getVmByVMname()

		try
		{
			CloneParam param = new CloneParam();
			param.setDataCenter("Datacenter");
			param.setResPool("cluster");			
			firstVM=CreateVMName();
			param.setCloneName(firstVM);
			//Todo(Qinghe Jin: need to get datastore by hostip)

			param.setTargetIp(hostIp);
			param.setVmPath("Datacenter/vm/vm_clone");

			VMClone t = new VMClone(context);
			t.setParam(param);
			List<String> names =t.getDataStore(hostIp);
			param.setTargetDs(names.get(2));

			new VMClone(context).cloneVM(param); 
 
			LinkedParam linkedParam = new LinkedParam();
			linkedParam.setVmName(firstVM);
			linkedParam.setDataCenter("Datacenter");
			linkedParam.setDesc("linked clone to " + hostIp);
			linkedParam.setSnapshotName("snapshotOn__" + hostIp);
			linkedParam.setPowerOn(true);
			for (int i=0; i < numPerHost; i++){
				linkedParam.setCloneName(CreateVMName());
				VMLinkedClone linkedClone = new VMLinkedClone(context);
				linkedClone.linkedCloneVM(linkedParam);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
