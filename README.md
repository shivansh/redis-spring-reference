# redis-spring-reference
A reference app for consuming the Redis, hyperscale option (_redis-cache_ service) using a Spring Boot application

# Using Spring Auto-reconfiguration
Since the _redis-cache_ service provisions Redis clusters with SSL enabled, it is not possible to use Spring Auto reconfiguration to establish a connection to an AWS Elasticache Redis or Azure Redis instance.
> See: https://github.com/cloudfoundry/java-buildpack-auto-reconfiguration/issues/77.

It is therefore necessary to disable spring auto-reconf ([reference](https://github.com/shivansh/redis-spring-reference/blob/main/manifest.yml#L11)) and instantiate `RedisConnectionFactory` as shown in [RedisConfig.java](src/main/java/com/sap/hyperscale/backingservices/RedisConfig.java)

# Using Lettuce or Jedis library
Use the appropriate method in [RedisConfig.java](src/main/java/com/sap/hyperscale/backingservices/RedisConfig.java) to use either one of the client libraries.

# Build
```
mvn clean install
```

# Deploy to CloudFoundry
- Change the [`application.properties`](src/main/resources/application.properties) file to specify the target infrastructure (`aws` or `azure`). This is then used to configure the Redis connection by taking into consideration any IaaS based differences.
- Specify the redis-cache service instance name in the `manifest.yml` file.
- `cf push -f manifest.yml`

# Test the app
```
$ curl https://app-url/set?key=name&value=james
$ curl https://app-url/find?key=name
$ curl https://app-url/update?key=name&value=bond
$ curl https://app-url/delete?key=name
```
