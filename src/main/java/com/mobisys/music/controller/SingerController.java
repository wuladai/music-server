package com.mobisys.music.controller;


import com.alibaba.fastjson.JSONObject;
import com.mobisys.music.domain.Singer;
import com.mobisys.music.domain.Song;
import com.mobisys.music.service.SingerService;
import com.mobisys.music.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 歌手控制类
 */
@RestController
@RequestMapping(value = "/singer")
public class SingerController {

    @Autowired
    private SingerService singerService;

    /**
     * 添加歌手
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object addSinger(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String name = request.getParameter("name").trim();
        String sex = request.getParameter("sex").trim();
        String pic = request.getParameter("pic").trim();
        String birth = request.getParameter("birth").trim();
        String location = request.getParameter("location").trim();
        String introduction = request.getParameter("introduction").trim();

        //转换时间为Date格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = new Date();
        try {
            birthDate = dateFormat.parse(birth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Singer singer = new Singer();
        singer.setName(name);
        singer.setSex(new Byte(sex));
        singer.setPic(pic);
        singer.setBirth(birthDate);
        singer.setLocation(location);
        singer.setIntroduction(introduction);

        // 添加歌手
        boolean flag = singerService.insert(singer);
        if (flag) {
            jsonObject.put(Consts.CODE, 1);
            jsonObject.put(Consts.MSG, "添加歌手成功");
        } else {
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "添加歌手失败");
        }

        return jsonObject;
    }


    /**
     * 修改歌手
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Object updateSinger(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String id = request.getParameter("id").trim();
        String name = request.getParameter("name").trim();
        String sex = request.getParameter("sex").trim();
//        String pic = request.getParameter("pic").trim();
        String birth = request.getParameter("birth").trim();
        String location = request.getParameter("location").trim();
        String introduction = request.getParameter("introduction").trim();

        //转换时间为Date格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date birthDate = new Date();
        try {
            birthDate = dateFormat.parse(birth);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Singer singer = new Singer();
        singer.setId(Integer.parseInt(id));
        singer.setName(name);
        singer.setSex(new Byte(sex));
//        singer.setPic(pic);
        singer.setBirth(birthDate);
        singer.setLocation(location);
        singer.setIntroduction(introduction);

        // 修改歌手
        boolean flag = singerService.update(singer);
        if (flag) {
            jsonObject.put(Consts.CODE, 1);
            jsonObject.put(Consts.MSG, "修改歌手成功");
        } else {
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "修改歌手失败");
        }

        return jsonObject;
    }

    /**
     * 删除歌手（数据库记录和本地文件记录）
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Object deleteSinger(HttpServletRequest request) {
        String id = request.getParameter("id").trim();
        // 在/img/singerPic目录下删除真实文件
        Singer singer = singerService.SelectByPrimaryKey(Integer.parseInt(id));

        String picName = singer.getPic().substring("/img/singerPic/".length(), singer.getPic().length());
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "img"
                + System.getProperty("file.separator") + "singerPic" + System.getProperty("file.separator") + picName;
        File delFile = new File(filePath);
        if(delFile.exists()){
            delFile.delete();
            System.out.println("删除本地文件"+picName+"成功");
        }
        // 删除歌手数据库记录
        boolean flag = singerService.delete(Integer.parseInt(id));
        return flag;

    }

    /**
     * 根据主键ID查询单个对象
     */
    @RequestMapping(value = "/SelectByPrimaryKey", method = RequestMethod.GET)
    public Object SelectByPrimaryKey(HttpServletRequest request) {
        String id = request.getParameter("id").trim();

        return singerService.SelectByPrimaryKey(Integer.parseInt(id));
    }

    /**
     * 查询所有歌手
     */
    @RequestMapping(value = "/allSinger", method = RequestMethod.GET)
    public Object allSinger(HttpServletRequest request) {

        return singerService.allSinger();
    }

    /**
     * 根据歌手名模糊查询
     */
    @RequestMapping(value = "/singerOfName", method = RequestMethod.GET)
    public Object singerOfName(HttpServletRequest request) {
        String name = request.getParameter("name").trim();
        //模糊查询
        return singerService.singerOfName("%" + name + "%");
    }

    /**
     * 根据歌手性别模糊查询
     */
    @RequestMapping(value = "/singerOfSex", method = RequestMethod.GET)
    public Object singerOfSex(HttpServletRequest request) {
        String sex = request.getParameter("sex").trim();
        //模糊查询
        return singerService.singerOfSex(Integer.parseInt(sex));
    }



    /**
     * 更新歌手图片,将图片插入到System.getProperty("user.dir")/img/singerPic/
     */
    @RequestMapping(value = "/updateSingerPic", method = RequestMethod.POST)
    public Object updateSingerPic(@RequestParam("file")MultipartFile avatorFile, @RequestParam("id")int id) {
        JSONObject jsonObject = new JSONObject();
        if(avatorFile.isEmpty()){
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "上传歌手头像为空");
            return jsonObject;
        }

        //文件名： 当前时间到毫秒+文件名
        String fileName = System.currentTimeMillis() + avatorFile.getOriginalFilename();    //避免因重复提交相同图片而覆盖
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "img"
                                                         + System.getProperty("file.separator") + "singerPic";
        //如果该文件保存目录不存在，则创建它
        File dicFile = new File(filePath);
        if(!dicFile.exists()){
            dicFile.mkdir();
        }

        File dest = new File(filePath + System.getProperty("file.separator") + fileName);   //头像文件的真实地址
        String storeAvatorPath = "/img/singerPic/" + fileName;      //存储到数据库的相对文件地址

        try {
            /**
             * 上传文件
             */
            avatorFile.transferTo(dest);    //插入到实际文件夹
            //上传图片文件成功就更新数据库
            Singer singer = new Singer();
            singer.setId(id);
            singer.setPic(storeAvatorPath);
            boolean update = singerService.update(singer);
            if(update){
                jsonObject.put(Consts.CODE, 1);
                jsonObject.put(Consts.MSG, "上传歌手头像成功");
                jsonObject.put("pic", storeAvatorPath);
                return jsonObject;
            }else{
                jsonObject.put(Consts.CODE, 0);
                jsonObject.put(Consts.MSG, "上传歌手头像失败");
                return jsonObject;
            }

        } catch (IOException e) {
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "上传歌手头像的controller中出现异常：" + e.getMessage());
        }finally {
            return jsonObject;
        }
    }

}
