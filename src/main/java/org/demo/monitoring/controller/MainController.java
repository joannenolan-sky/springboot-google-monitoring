package org.demo.monitoring.controller;

import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping(value = "/hello")
    public String index() {
        logger.info("Hello has been received");
        return "Hello World";
    }
    @GetMapping(value = "/country")
    public String getCountry(@RequestParam(defaultValue = "AU") String country) {
        MDC.put("country", country);
        logger.info(String.format("Logging with MDC Country:[%s]",country));

        MDC.clear();
        return String.format("Country is %s", country);
    }



}
