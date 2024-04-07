package com.atguigu.cloud.controller;

import com.atguigu.cloud.entities.PayDTO;
import com.atguigu.cloud.resp.ResultData;
import jakarta.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/consumer")
public class OrderController {
//    public static final String PaymentServer_URL = "http://localhost:8001";
    public static final String PaymentServer_URL = "http://cloud-payment-service";

    @Resource
    private RestTemplate restTemplate;

    @PostMapping(value = "/pay/add")
    public ResultData addOrder(@RequestBody PayDTO payDTO)
    {
        return restTemplate.postForObject(PaymentServer_URL + "/pay/add", payDTO, ResultData.class);
    }

    @DeleteMapping("/pay/del/{id}")
    public ResultData deleteOrder(@PathVariable("id") Integer id){
        return restTemplate.exchange(PaymentServer_URL+"/pay/del/"+id, HttpMethod.DELETE,null,ResultData.class).getBody();
    }

    @PutMapping("/pay/update")
    public ResultData updateOrder(@RequestBody PayDTO payDTO){
        return restTemplate.exchange(PaymentServer_URL+"/pay/update",HttpMethod.PUT,new HttpEntity<>(payDTO),ResultData.class).getBody();
    }

    @GetMapping("/pay/get/{id}")
    public ResultData getOrder(@PathVariable("id") Integer id){
        return restTemplate.getForObject(PaymentServer_URL+"/pay/get/"+id,ResultData.class,id);
    }

    @GetMapping
    public ResultData getAll(){
        return restTemplate.getForObject(PaymentServer_URL+"/pay/getAll",ResultData.class);
    }

    /**
     * 测试consul的配置中心和loadbalance 负载均衡
     * @return
     */
    @GetMapping(value = "/pay/get/info")
    private String getInfoByConsul()
    {
        return restTemplate.getForObject(PaymentServer_URL + "/pay/get/info", String.class);
    }

    @Resource
    private DiscoveryClient discoveryClient;

    /**
     * 获取注册中心中的服务列表
     * @return
     */
    @GetMapping("/discovery")
    public String discovery()
    {
        List<String> services = discoveryClient.getServices();
        for (String element : services) {
            System.out.println(element);
        }

        System.out.println("===================================");

        List<ServiceInstance> instances = discoveryClient.getInstances("cloud-payment-service");
        for (ServiceInstance element : instances) {
            System.out.println(element.getServiceId()+"\t"+element.getHost()+"\t"+element.getPort()+"\t"+element.getUri());
        }

        return instances.get(0).getServiceId()+":"+instances.get(0).getPort();
    }
}
