package com.nwpu.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nwpu.media.model.po.MediaProcess;

import java.util.List;

/**
 * <p>
 *  服务类：媒资文件处理业务方法
 * </p>
 *
 * @author yfh
 * @since 2023-02-21
 */
public interface MediaProcessService extends IService<MediaProcess> {
    /**
     * @description 获取待处理任务
     * @param shardIndex 分片序号
     * @param shardTotal 分片总数
     * @param count 获取记录数
     * @return java.util.List<com.xuecheng.media.model.po.MediaProcess>
     * @author yfh
     */
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * @description 保存任务结果
     * @param taskId  任务id
     * @param status 任务状态
     * @param fileId  文件id
     * @param url url
     * @param errorMsg 错误信息
     * @return void
     * @author yfh
     */
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);

    /**
     *  开启一个任务
     * @param id 任务id
     * @return true开启任务成功，false开启任务失败
     */
    public boolean startTask(long id);
}
