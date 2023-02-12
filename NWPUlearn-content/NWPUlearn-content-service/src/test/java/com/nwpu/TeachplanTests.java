package com.nwpu;

import com.nwpu.content.model.dto.TeachplanDto;
import com.nwpu.content.service.TeachplanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author yfh
 * @version 1.0
 * @description
 * @date 2023/2/11
 */
@SpringBootTest
public class TeachplanTests {
    @Autowired
    TeachplanService teachplanService;

    @Test
    void tesetTreeNodeGet(){
        List<TeachplanDto> result = teachplanService.findTeachplayTree(74L);
        System.out.println(result);
    }
}
