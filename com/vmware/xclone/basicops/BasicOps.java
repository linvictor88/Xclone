package com.vmware.xclone.basicops;

public class  CloneVM {

private CloneParam param;

public CloneVM(CloneParam param) {
	this.param = param;
};

    public boolean DoClone();

}


public class LinkedCloneVM {

private LinkedParam param;

public LinkedCloneVM();

public boolean DoLinkedClone();

}


public class PoweroffVM {

}


public class PoweronVM {

}


public class DeleteVM {
}