package com.chen.test.controller;

import com.chen.test.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author CSY
 * @version v1.0
 * @date 2023/5/8 9:21
 */
@RestController
public class TestController {
    @Resource
    private TestService testService;

    @GetMapping("/")
    public String findPost() {
        String result = testService.findPost();
        return result;
    }
}
