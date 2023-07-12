package com.nwpu.media.controller;

import com.nwpu.media.service.MqMessageHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yfh
 */
@Slf4j
@RestController
@RequestMapping("mqMessageHistory")
public class MqMessageHistoryController {

    @Autowired
    private MqMessageHistoryService  mqMessageHistoryService;
}
