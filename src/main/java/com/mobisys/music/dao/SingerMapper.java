package com.mobisys.music.dao;

import com.mobisys.music.domain.Singer;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 歌手dao
 */
@Repository
public interface SingerMapper {

    /**
     * 增加
     */
    public int insert(Singer singer);

    /**
     * 修改
     */
    public int update(Singer singer);

    /**
     * 删除
     */
    public int delete(Integer id);

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
