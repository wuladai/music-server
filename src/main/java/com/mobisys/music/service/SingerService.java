package com.mobisys.music.service;

import com.mobisys.music.domain.Singer;

import java.util.List;

/**
 * 管理员service接口
 */
public interface SingerService {
    /**
     * 增加
     */
    public boolean insert(Singer singer);

    /**
     * 修改
     */
    public boolean update(Singer singer);

    /**
     * 删除
     */
    public boolean delete(Integer id);

    /**
     * 根据主键ID查询单个对象
     */
    public Singer SelectByPrimaryKey(Integer id);

    /**
     * 查询所有歌手
     */
    public List<Singer> allSinger();

    /**
     * 根据歌手名模糊查询
     */
    public List<Singer> singerOfName(String name);

    /**
     * 根据歌手性别模糊查询
     */
    public List<Singer> singerOfSex(Integer sex);
}
