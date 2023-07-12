package com.nwpu.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nwpu.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author yfh
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {
    /**
     * @param shardTotal 分片总数
     * @param shardindex 分片序号
     * @param count      任务数
     * @return java.util.List<com.nwpu.media.model.po.MediaProcess>
     * @description 根据分片参数获取待处理任务（id % 分片总数 = 被分配的序号）
     * @author yfh
     */
//    TODO： 修改数据库media_process结构
    @Select("SELECT t.* FROM media_process t WHERE t.id % #{shardTotal} = #{shardindex} and (t.status = '1' or t.status = '3') and t.fail_count < 3 limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardindex") int shardindex, @Param("count") int count);

    /**
     * 开启一个任务
     * @param id 任务id
     * @return 更新记录数
     */
    @Update("update media_process m set m.status='4' where (m.status='1' or m.status='3') and m.fail_count<3 and m.id=#{id}")
    int startTask(@Param("id") long id);

//    @Select("SELECT t.* FROM media_process t WHERE t.id % #{shardTotal} = #{shardindex} and (t.status = '1' or t.status = '3') limit #{count}")
//    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardindex") int shardindex, @Param("count") int count);
}
