package com.mobisys.music.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 定位各种文件的实际位置
 */

@Configuration
public class FileConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 定位歌手头像图片地址
         */
        registry.addResourceHandler("/img/singerPic/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + System.getProperty("file.separator") + "img"
                                              + System.getProperty("file.separator") + "singerPic" + System.getProperty("file.separator"));


        /**
         * 定位歌曲图片地址
         */
        registry.addResourceHandler("/img/songPic/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + System.getProperty("file.separator") + "img"
                        + System.getProperty("file.separator") + "songPic" + System.getProperty("file.separator"));


        /**
         * 定位歌单图片地址
         */
        registry.addResourceHandler("/img/songListPic/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + System.getProperty("file.separator") + "img"
                        + System.getProperty("file.separator") + "songListPic" + System.getProperty("file.separator"));

        /**
         * 定位歌曲文件地址
         */
        registry.addResourceHandler("/song/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + System.getProperty("file.separator") +
                                                                                "song" + System.getProperty("file.separator"));

        /**
         * 前端用户头像地址
         */
        registry.addResourceHandler("/avatorImages/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + System.getProperty("file.separator") +
                        "avatorImages" + System.getProperty("file.separator"));

        /**
         * 前端用户默认头像地址
         */
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + System.getProperty("file.separator") +
                        "img" + System.getProperty("file.separator"));
    }
}
