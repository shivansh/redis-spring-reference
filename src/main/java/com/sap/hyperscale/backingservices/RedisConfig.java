package com.sap.hyperscale.backingservices;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DnsResolvers;
import io.pivotal.cfenv.core.CfEnv;

import com.sap.hyperscale.backingservices.MySocketAddressResolver;

@Configuration
public class RedisConfig {

	@Autowired
	private ApplicationContext context;
	
	@Value("${infraType}")
	private String infraType;

	public boolean getClusterMode(CfEnv cfEnv) {
		boolean clusterMode = Boolean.parseBoolean(cfEnv.findCredentialsByTag("cache").getString("cluster_mode"));
		return clusterMode;
	}

	/*
	 * Returns Lettuce Client config for the IaaS and cluster/non-cluster Redis.
	 */
	private LettuceClientConfiguration getLettuceClientConfiguration(String redisHost, String redisPort,boolean clusterMode) {
		LettuceClientConfiguration clientConfig = null;
		System.out.println("Preparing Lettuce Client Config for " + infraType);
		if ("aws".equalsIgnoreCase(infraType)) {
			clientConfig = LettuceClientConfiguration.builder().useSsl().build();
		}else if("azure".equalsIgnoreCase(infraType)) {
			if (clusterMode) {
			    // for cluster mode enabled MS Azure Redis, force Lettuce to use hostname for SSL verification
			    // ref: https://docs.microsoft.com/en-us/azure/azure-cache-for-redis/cache-how-to-premium-vnet#when-trying-to-connect-to-my-azure-cache-for-redis-in-a-vnet-why-am-i-getting-an-error-stating-the-remote-certificate-is-invalid
				// ref: https://gitter.im/lettuce-io/Lobby
				MySocketAddressResolver myresolver = new MySocketAddressResolver(DnsResolvers.JVM_DEFAULT);
				myresolver.setRedisHostname(redisHost);
				myresolver.setPort(Integer.parseInt(redisPort));
				ClientResources clientResources = ClientResources.builder().socketAddressResolver(myresolver).build();
				clientConfig = LettuceClientConfiguration.builder().useSsl().and().clientResources(clientResources).build();		
			}else {
				clientConfig = LettuceClientConfiguration.builder().useSsl().build();
			}
		}
		return clientConfig;
	}
	
	/**
	 * Lettuce based
	 * 
	 * @return
	 */
	@Bean	
	public RedisConnectionFactory getClusterRedisConnectionFactory() {
		CfEnv cfEnv = new CfEnv();
		String redisHost = cfEnv.findCredentialsByTag("cache").getHost();
		String redisPort = cfEnv.findCredentialsByTag("cache").getPort();
		String redisPassword = cfEnv.findCredentialsByTag("cache").getPassword();
        boolean clusterMode = getClusterMode(cfEnv);
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(redisHost, redisPort,clusterMode);
		
		if (clusterMode) {
			RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(
					Arrays.asList(redisHost + ":" + redisPort));
			clusterConfiguration.setPassword(redisPassword);
			LettuceConnectionFactory lcf = new LettuceConnectionFactory(clusterConfiguration, clientConfig);
			return lcf;
		} else {
			RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost,
					Integer.parseInt(redisPort));
			configuration.setPassword(redisPassword);
			LettuceConnectionFactory lcf = new LettuceConnectionFactory(configuration, clientConfig);
			return lcf;
		}
	}
	

	/*
	 * Returns Jedis Client config for the IaaS and cluster/non-cluster Redis.
	 */
	/*
	private JedisClientConfiguration getJedisClientConfiguration(boolean clusterMode) {
		System.out.println("Preparing Jedis Client Config for " + infraType);
		JedisClientConfiguration clientConfig = null;
		if ("aws".equalsIgnoreCase(infraType)) {
			clientConfig = JedisClientConfiguration.builder().useSsl().build();
		}else if("azure".equalsIgnoreCase(infraType)) {
			if(clusterMode) {
				//TODO - figure out how to set SSL verification to hostname in Jedis
			}else {
				clientConfig = JedisClientConfiguration.builder().useSsl().build();
			}
		}
		return clientConfig;
	}
	*/
	
	/**
	 * Jedis based.
	 * @return
	 */
	/*
	@Bean
	public RedisConnectionFactory getClusterRedisConnectionFactory() {
		CfEnv cfEnv = new CfEnv();
		String redisHost = cfEnv.findCredentialsByTag("cache").getHost();
		String redisPort = cfEnv.findCredentialsByTag("cache").getPort();
		String redisPassword = cfEnv.findCredentialsByTag("cache").getPassword();
		boolean clusterMode = getClusterMode(cfEnv);
		JedisConnectionFactory jcf;
		JedisClientConfiguration clientConfig = getJedisClientConfiguration(clusterMode);
		if (clusterMode) {
			RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(
					Arrays.asList(redisHost + ":" + redisPort));
			clusterConfiguration.setPassword(redisPassword);
			jcf = new JedisConnectionFactory(clusterConfiguration, clientConfig);
		} else {
			RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisHost,
					Integer.parseInt(redisPort));
			configuration.setPassword(redisPassword);
			jcf = new JedisConnectionFactory(configuration, clientConfig);
		}
		return jcf;
	}
	*/

	@Bean
	public StringRedisTemplate getRedisTemplate() {
		return new StringRedisTemplate(context.getBean(RedisConnectionFactory.class));
	}
}