package com.sap.hyperscale.backingservices;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.DnsResolver;
import io.lettuce.core.resource.SocketAddressResolver;

public class MySocketAddressResolver extends SocketAddressResolver {

	private String redisHostname;
	private int port;
	
	public String getRedisHostname() {
		return redisHostname;
	}

	public void setRedisHostname(String redisHostname) {
		this.redisHostname = redisHostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	protected MySocketAddressResolver(DnsResolver dnsResolver) {
		super(dnsResolver);
	}

	@Override
	public SocketAddress resolve(RedisURI redisURI) {
		
		System.out.println("*************** ");
		System.out.println("*************** ");
		System.out.println("Redis URI is " + redisURI.toString());
		return new InetSocketAddress(redisHostname,port);
	}
}
