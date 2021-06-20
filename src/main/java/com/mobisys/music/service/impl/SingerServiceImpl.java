package com.mobisys.music.service.impl;

import com.mobisys.music.dao.AdminMapper;
import com.mobisys.music.dao.SingerMapper;
import com.mobisys.music.domain.Singer;
import com.mobisys.music.service.AdminService;
import com.mobisys.music.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 歌手service实现类
 */
@Service
public class SingerServiceImpl implements SingerService {

    @Autowired
    private SingerMapper singerMapper;




    /**
     * 增加
     *
     * @param singer
     */
    @Override
    public boolean insert(Singer singer) {
        return singerMapper.insert(singer) > 0;
    }

    /**
     * 修改
     *
     * @param singer
     */
    @Override
    public boolean update(Singer singer) {
        return singerMapper.update(singer) > 0;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public boolean delete(Integer id) {
        return singerMapper.delete(id) > 0;
    }

    /**
     * 根据主键ID查询单个对象
     *
     * @param id
     */
    @Override
    public Singer SelectByPrimaryKey(Integer id) {
        return singerMapper.SelectByPrimaryKey(id);
    }

    /**
     * 查询所有歌手
     */
    @Override
    public List<Singer> allSinger() {
        return singerMapper.allSinger();
    }

    /**
     * 根据歌手名模糊查询
     *
     * @param name
     */
    @Override
    public List<Singer> singerOfName(String name) {
        return singerMapper.singerOfName(name);
    }

    /**
     * 根据歌手性别模糊查询
     *
     * @param sex
     */
    @Override
    public List<Singer> singerOfSex(Integer sex) {
        return singerMapper.singerOfSex(sex);
    }
}
