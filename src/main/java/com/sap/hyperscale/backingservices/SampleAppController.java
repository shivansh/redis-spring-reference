package com.sap.hyperscale.backingservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleAppController {

	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@RequestMapping("/set")
	public String set(@RequestParam("key") String key, @RequestParam("value") String value) {
		redisTemplate.opsForValue().set(key, value);
		return key + "=" + value;
	}

	@RequestMapping("/find")
	public Object find(@RequestParam("key") String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@RequestMapping("/update")
	public Object update(@RequestParam("key") String key, @RequestParam("value") String value) {
		redisTemplate.opsForValue().set(key, value);
		return key + "=" + value;
	}

	@RequestMapping("/delete")
	public boolean delete(@RequestParam("key") String key) {
		return redisTemplate.delete(key);
	}
}
