package com.nwpu.media.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nwpu.media.mapper.MqMessageMapper;
import com.nwpu.media.model.po.MqMessage;
import com.nwpu.media.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yfh
 */
@Slf4j
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {

}
