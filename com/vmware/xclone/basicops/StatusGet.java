package com.vmware.xclone.basicops;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.vim25.*;
import com.vmware.xclone.*;
import com.vmware.xclone.basicops.VCConnection;
import com.vmware.xclone.managementobjects.*;


public class StatusGet extends Thread {
    private UserInterface ui;
	private int numStart;
	private int num;
	private  VCConnection conn;

    public UserInterface getUi() {
		return ui;
	}
	public void setUi(UserInterface ui) {
		this.ui = ui;
	}
	public int getNumStart() {
		return numStart;
	}
	public void setNumStart(int numStart) {
		this.numStart = numStart;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public StatusGet(UserInterface ui, int numStart, int num)
    {
		try {
			conn = new VCConnection(ui.getVcUrl(), ui.getUserName(),
					ui.getPassWord());

			conn.connect();

		} catch (Exception e) {
			e.printStackTrace();
		}
    	setUi(ui);
    	setNumStart(numStart);
    	setNum(num);
    }
	public void run()
	{
		int i,j;
		int col = 10;
	    int row = getNum()/col + 1;
	    String rep[][] = new String[row][col];
	    for(i=0;i<row;i++)
	    	for(j=0;j<col;j++)
	    	{
	    		rep[i][j] = "(^_^)         ";
	    	}
		boolean check=true;
		ManagementObjects  item = new ManagementObjects(conn);
		try {
	        while(check == true)
	        {
				Thread.sleep(1000);
	        	check = false;
	        	for(i=getNumStart(); i<getNumStart() + getNum(); i++)
	        	{
					String vmName = ui.getVmClonePrefix() + String.format("%03d", i);
					ManagedObjectReference x = item.getVMByName(vmName);
					GuestInfo ob = (GuestInfo)item.getDynamicProperty(x, "guest");
					if(ob == null || ob.getIpAddress() == null)
					{
						check = true;
					}
					else
					{
						int m = i/col;
						int n = i - m*col;
						rep[m][n] = ob.getIpAddress() + "   ";
					}
		//				//<ManagedObjectReference> results = item.getVm();
		//				List<String> names  = item.getVMNames();
		//		
		//		
		//				for(String name:names)
		//				{
		//					ManagedObjectReference x =  item.getVMByName(name);
		//					GuestInfo ob =   (GuestInfo)item.getDynamicProperty(x, "guest");
		//				 
		//					
		//					System.out.println("name:"+name+";"+ob.getIpAddress());
		//				}
		        }
	        	for(i=0;i<row;i++)
	        	{
	        		for(j=0;j<col;j++)
	        		{
	        			if ((i*col  + j) >= getNum())
	        				break;
	        			System.out.print(rep[i][j]);
	        		}
    				System.out.println("\n");
	        	}
	        }
		   } catch (Exception e) {
		e.printStackTrace();
	}
	}
	
}