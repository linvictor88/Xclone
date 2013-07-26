package com.vmware.vm;

import java.net.*;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.ws.BindingProvider;

import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
public class VCConnection {
	 
 	private static final ManagedObjectReference SVC_INST_REF = new ManagedObjectReference();
	private  ManagedObjectReference propCollectorRef;
	private  ManagedObjectReference rootRef;
	private  VimService vimService;
	private  VimPortType vimPort;
	private  ServiceContent serviceContent;
	private  final String SVC_INST_NAME = "ServiceInstance";
	private  String url;
	private  String userName;
	private  String password;
	private  boolean isConnected;
	
	
	private Context context;
	
	private static String[] meTree = {
		"ManagedEntity",
		"ComputeResource",
		"ClusterComputeResource",
		"Datacenter",
		"Folder",
		"HostSystem",
		"ResourcePool",
		"VirtualMachine"
	};
	private static String[] crTree = {
		"ComputeResource",
		"ClusterComputeResource"
	};
	private static String[] hcTree = {
		"HistoryCollector",
		"EventHistoryCollector",
		"TaskHistoryCollector"
	};

	
	
	
	public VCConnection(String url, String uname,String passwd )
	{
		this.url = url;
		this.userName = uname;
		this.password =passwd;
	}
	
	
	public  void connect()
			throws Exception {

		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
				return true;
			}
		};
		trustAllHttpsCertificates();
		HttpsURLConnection.setDefaultHostnameVerifier(hv);

		SVC_INST_REF.setType(SVC_INST_NAME);
		SVC_INST_REF.setValue(SVC_INST_NAME);

		vimService = new VimService();
		vimPort = vimService.getVimPort();
		Map<String, Object> ctxt =
				((BindingProvider) vimPort).getRequestContext();

		ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);

		serviceContent = vimPort.retrieveServiceContent(SVC_INST_REF);
		vimPort.login(serviceContent.getSessionManager(),
				userName,
				password, null);
		isConnected = true;

		propCollectorRef = serviceContent.getPropertyCollector();
		
		ManagedObjectReference  tmp = serviceContent.getPropertyCollector();
		rootRef = serviceContent.getRootFolder();
		
		context = new Context();
		context.setPropCollectorRef(propCollectorRef);
		context.setRootRef(rootRef);
		context.setServiceContent(serviceContent);
		context.setVimPort(vimPort);
		context.setVimService(vimService);
	}
 
	public ServiceContent getServiceContent()
	{
		return serviceContent;
	}
	
	public VimService getVimService()
	{
		return vimService;
	}
	
	public VimPortType getVimPort()
	{
		return vimPort;
	}
	
	
	public ManagedObjectReference getPropRef()
	{
		return propCollectorRef;
	}
	
	public ManagedObjectReference getRootRef()
	{
		return rootRef;
	}
  
	private static class TrustAllTrustManager implements javax.net.ssl.TrustManager,
	javax.net.ssl.X509TrustManager {

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;//
		}

		public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
				String authType)
						throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
				String authType)
						throws java.security.cert.CertificateException {
			return;
		}
	}

	private  void trustAllHttpsCertificates() throws Exception {
		// Create a trust manager that does not validate certificate chains:
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new TrustAllTrustManager();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
		javax.net.ssl.SSLSessionContext sslsc = sc.getServerSessionContext();
		sslsc.setSessionTimeout(0);
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	public   void disconnect()
			throws Exception {
		if (isConnected) {
			vimPort.logout(serviceContent.getSessionManager());
		}
		isConnected = false;
	}


	public Context getContext() {
		// TODO Auto-generated method stub
		return context;
	}



}
