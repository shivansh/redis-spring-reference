package com.sap.hyperscale.backingservices;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class BasicHostnameVerifier implements HostnameVerifier {
	
	private String redisHostname;
	private int port;
	
	public String getRedisHostname() {
		return redisHostname;
	}

	public void setRedisHostname(String redisHostname) {
		this.redisHostname = redisHostname;
	}

	@Override
	public boolean verify(String hostname, SSLSession session) {
		System.out.println("*************** ");
		System.out.println("*************** ");
		System.out.println("Redis URI is " + hostname + " :" + redisHostname);
		
		if (hostname.equals(redisHostname))
		{
			return true;
		}
		return false;
	}

	
}
