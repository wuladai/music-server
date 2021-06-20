package com.mobisys.music.controller;


import com.alibaba.fastjson.JSONObject;
import com.mobisys.music.domain.Singer;
import com.mobisys.music.domain.Song;
import com.mobisys.music.service.SongService;
import com.mobisys.music.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 歌曲管理controller
 */
@RestController
@RequestMapping("/song")
public class SongController {

    @Autowired
    private SongService songService;

    /**
     * 添加歌曲
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object addSong(HttpServletRequest request, @RequestParam("file") MultipartFile mpFile) {
        JSONObject jsonObject = new JSONObject();
        String singerId = request.getParameter("singerId").trim();
        String name = request.getParameter("name").trim();
        String introduction = request.getParameter("introduction").trim();
        String pic = "/img/songPic/tubiao.jpg";        //默认图片
        String lyric = request.getParameter("lyric").trim();
        if (mpFile.isEmpty()) {
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "上传歌曲不能为空");
            return jsonObject;
        }


        //文件名： 当前时间到毫秒+文件名
        String fileName = System.currentTimeMillis() + mpFile.getOriginalFilename();
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "song";

        //如果文件路径不存在，新增该路径
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdir();
        }

        File dest = new File(filePath + System.getProperty("file.separator") + fileName);   //实际文件保存位置
        /**
         * 存储到数据库里的相对文件地址，这个地址可以被springboot识别，因为添加了资源映射
         */
        String storeUrlPath = "/song/" + fileName;
        try {
            mpFile.transferTo(dest);
            Song song = new Song();
            song.setSingerId(Integer.parseInt(singerId));
            song.setName(name);
            song.setIntroduction(introduction);
            song.setPic(pic);
            song.setLyric(lyric);
            song.setUrl(storeUrlPath);
            boolean flag = songService.insert(song);
            if (flag) {
                jsonObject.put(Consts.CODE, 1);
                jsonObject.put(Consts.MSG, "上传歌曲成功");
                jsonObject.put("song", storeUrlPath);
                return jsonObject;
            }else{
                jsonObject.put(Consts.CODE, 0);
                jsonObject.put(Consts.MSG, "上传歌曲失败");
                return jsonObject;
            }

        } catch (IOException e) {
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "上传歌曲文件的controller中出现异常：" + e.getMessage());
        } finally {
            return jsonObject;
        }
    }


    /**
     * 根据歌手Id查询歌曲
     */
    @RequestMapping(value = "/singer/detail", method = RequestMethod.GET)
    public Object songOfSingerId(HttpServletRequest request) {

        String singerId = request.getParameter("singerId");
        return songService.songOfSingerId(Integer.parseInt(singerId));
    }

    /**
     * 更新歌曲，在更新之前需要删除已经存在的歌曲文件（数据库+文件夹）
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Object updateSong(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String id = request.getParameter("id").trim();
        String name = request.getParameter("name").trim();
        String introduction = request.getParameter("introduction").trim();
        String lyric = request.getParameter("lyric").trim();

        Song song = new Song();
        song.setId(Integer.parseInt(id));
        song.setName(name);
        song.setIntroduction(introduction);
        song.setLyric(lyric);

        // 修改歌手
        boolean flag = songService.update(song);
        if (flag) {
            jsonObject.put(Consts.CODE, 1);
            jsonObject.put(Consts.MSG, "修改歌曲成功");
        } else {
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "修改歌曲失败");
        }
        return jsonObject;
    }

    /**
     * 删除歌曲,(数据库记录和本地文件记录)
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public Object deleteSong(HttpServletRequest request) {
        String id = request.getParameter("id").trim();
        // 在/song目录下删除真实文件
        Song song = songService.selectByPrimaryKey(Integer.parseInt(id));
        String songName = song.getUrl().substring("/song/".length(), song.getUrl().length());         // /song/songName
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "song"
                            + System.getProperty("file.separator") + songName;
        File delFile = new File(filePath);
        if(delFile.exists()){
            delFile.delete();
            System.out.println("删除本地文件"+songName+"成功");
        }
        // 在数据库中删除歌曲记录
        boolean flag = songService.delete(Integer.parseInt(id));
        return flag;

    }

    /**
     * 更新歌曲图片,将图片插入到System.getProperty("user.dir")/img/songPic/
     */
    @RequestMapping(value = "/updateSongPic", method = RequestMethod.POST)
    public Object updateSongPic(@RequestParam("file")MultipartFile avatorFile, @RequestParam("id")int id) {
        JSONObject jsonObject = new JSONObject();
        if(avatorFile.isEmpty()){
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "上传歌曲头像为空");
            return jsonObject;
        }

        //文件名： 当前时间到毫秒+文件名
        String fileName = System.currentTimeMillis() + avatorFile.getOriginalFilename();    //避免因重复提交相同图片而覆盖
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "img"
                + System.getProperty("file.separator") + "songPic";
        //如果该文件保存目录不存在，则创建它
        File dicFile = new File(filePath);
        if(!dicFile.exists()){
            dicFile.mkdir();
        }

        File dest = new File(filePath + System.getProperty("file.separator") + fileName);   //头像文件的真实地址
        String storeAvatorPath = "/img/songPic/" + fileName;      //存储到数据库的相对文件地址

        try {
            /**
             * 上传文件
             */
            avatorFile.transferTo(dest);    //插入到实际文件夹
            //上传图片文件成功就更新数据库
            Song song = new Song();
            song.setId(id);
            song.setPic(storeAvatorPath);
            boolean update = songService.update(song);
            if(update){
                jsonObject.put(Consts.CODE, 1);
                jsonObject.put(Consts.MSG, "上传歌曲头像成功");
                jsonObject.put("pic", storeAvatorPath);
                return jsonObject;
            }else{
                jsonObject.put(Consts.CODE, 0);
                jsonObject.put(Consts.MSG, "上传歌曲头像失败");
                return jsonObject;
            }

        } catch (IOException e) {
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "上传歌曲图片的controller中出现异常：" + e.getMessage());
        }finally {
            return jsonObject;
        }
    }


    /**
     * 更新歌曲文件
     */
    @RequestMapping(value = "/updateSongUrl", method = RequestMethod.POST)
    public Object updateSongUrl(@RequestParam("file")MultipartFile avatorFile, @RequestParam("id")int id) {
        JSONObject jsonObject = new JSONObject();
        if(avatorFile.isEmpty()){
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "上传歌曲头像为空");
            return jsonObject;
        }

        //文件名： 当前时间到毫秒+文件名
        String fileName = System.currentTimeMillis() + avatorFile.getOriginalFilename();    //避免因重复提交相同图片而覆盖
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "song";

        //如果该文件保存目录不存在，则创建它
        File dicFile = new File(filePath);
        if(!dicFile.exists()){
            dicFile.mkdir();
        }

        File dest = new File(filePath + System.getProperty("file.separator") + fileName);   //头像文件的真实地址
        String storeAvatorPath = "/song/" + fileName;      //存储到数据库的相对文件地址

        try {
            /**
             * 上传文件
             */
            avatorFile.transferTo(dest);    //插入到实际文件夹
            //更新歌曲文件成功就更新数据库
            Song song = new Song();
            song.setId(id);
            song.setUrl(storeAvatorPath);
            boolean update = songService.update(song);
            if(update){
                jsonObject.put(Consts.CODE, 1);
                jsonObject.put(Consts.MSG, "更新歌曲成功");
                jsonObject.put("avator", storeAvatorPath);
                return jsonObject;
            }else{
                jsonObject.put(Consts.CODE, 0);
                jsonObject.put(Consts.MSG, "更新歌曲失败");
                return jsonObject;
            }

        } catch (IOException e) {
            jsonObject.put(Consts.CODE, 0);
            jsonObject.put(Consts.MSG, "更新歌曲的controller中出现异常：" + e.getMessage());
        }finally {
            return jsonObject;
        }
    }


    /**
     * 根据歌曲Id查询歌曲对象
     */
    @RequestMapping(value = "/songOfSongId", method = RequestMethod.GET)
    public Object songOfSongId(HttpServletRequest request) {

        String songId = request.getParameter("songId").trim();
        return songService.selectByPrimaryKey(Integer.parseInt(songId));
    }


    /**
     * 根据歌曲名查询歌曲对象
     */
    @RequestMapping(value = "/songOfSongName", method = RequestMethod.GET)
    public Object songOfSongName(HttpServletRequest request) {

        String songName = request.getParameter("songName").trim();
        return songService.songOfName(songName);
    }

    /**
     * 查询所有歌曲
     */
    @RequestMapping(value = "/allSong", method = RequestMethod.GET)
    public Object allSong(HttpServletRequest request) {
        System.out.println("================执行了==============");
        return songService.allSong();
    }

}




























