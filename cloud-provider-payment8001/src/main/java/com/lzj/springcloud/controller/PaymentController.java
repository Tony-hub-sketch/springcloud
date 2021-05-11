package com.lzj.springcloud.controller;

import com.lzj.springcloud.entities.CommonResult;
import com.lzj.springcloud.entities.Payment;
import com.lzj.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class PaymentController {

    @Value("${server.port}") //Value绑定单一的属性值
    private String serverPort;

    @Resource
    private PaymentService paymentService;

    @Resource
    private DiscoveryClient discoveryClient;

    //传给前端JSON
    @PostMapping(value = "/payment/create")    //写操作POST
    public CommonResult create(@RequestBody Payment payment) {

        //由于在mapper.xml配置了useGeneratedKeys="true" keyProperty="id"，会将自增的id封装到实体类中
        int result = paymentService.create(payment);

        log.info("*****插入结果：" + result);

        if (result > 0) {
            return new CommonResult(200, "插入数据库成功,serverPort：" + serverPort, result);
        } else {
            return new CommonResult(444, "插入数据库失败", null);
        }
    }

    //传给前端JSON
    @GetMapping(value = "/payment/get/{id}")    //写操作POST
    public CommonResult getPaymentById(@PathVariable("id") Long id) {

        Payment payment = paymentService.getPaymentById(id);

        log.info("*****查询结果：" + payment);

        if (payment != null) {
            return new CommonResult(200, "查询数据库成功,serverPort：" + serverPort, payment);
        } else {
            return new CommonResult(444, "查询ID:" + id + "没有对应记录", null);
        }
    }

    @GetMapping(value = "/payment/discovery")
    public Object discovery() {
        List<String> services = discoveryClient.getServices();
        services.forEach(service -> {
            log.info("******element：" + service);
        });
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        instances.forEach(instance -> {
            log.info(instance.getInstanceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        });

        instances.stream().forEach(instance -> {
            log.info("test:{}****{}",instance.getInstanceId(),instance.getHost());
        });
        return this.discoveryClient;
    }
}
