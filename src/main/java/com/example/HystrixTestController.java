package com.example;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;

@RestController
public class HystrixTestController {

    private RestTemplate restTemplate = new RestTemplate();
	
    static private String URL1 = "http://localhost:8090/getNames1";
    static private String URL2 = "http://localhost:8090/getNames2";

    static private String key = "test";
    
   
	private class TestHystrixCommand<T> extends HystrixCommand<List<String>> {

			private String url;
		
	        protected TestHystrixCommand(HystrixCommandGroupKey group) {
	            super(Setter.withGroupKey(group).andCommandPropertiesDefaults(
	                    HystrixCommandProperties.Setter()
	                            /* just sets the instance default, cannot be changed during runtime. */
	                            .withExecutionTimeoutInMilliseconds(5000)
	                            .withCircuitBreakerRequestVolumeThreshold(3)
	                            .withCircuitBreakerEnabled(true)
	                            .withCircuitBreakerSleepWindowInMilliseconds(5500)));
	        }

	        @SuppressWarnings("unchecked")
			@Override
	        protected List<String> run() throws Exception {
	            try {
	                return restTemplate.getForObject(url, List.class);
	            } catch (Exception e) {
	                throw e;
	            }
	        }

	        @SuppressWarnings("unchecked")
			@Override
	        protected List<String> getFallback() {
	            return Collections.EMPTY_LIST;
	        }

	        protected void setURL(String URL) {
	        	this.url = URL;
	        }
	    }
	
	
	@RequestMapping("/getNames1")
	public @ResponseBody List<String> getNames1() {
        final TestHystrixCommand<List<String>> command = new TestHystrixCommand<>(HystrixCommandGroupKey.Factory.asKey(key));
	    command.setURL(URL1);
		return command.execute();
	}
	
	@RequestMapping("/getNames2")
	public @ResponseBody List<String> getNames2() {
        final TestHystrixCommand<List<String>> command = new TestHystrixCommand<>(HystrixCommandGroupKey.Factory.asKey(key));
        command.setURL(URL2);
		return command.execute();
	}
}
