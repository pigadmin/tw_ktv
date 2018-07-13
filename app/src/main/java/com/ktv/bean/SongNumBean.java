package com.ktv.bean;

import java.io.Serializable;
import java.util.List;

public class SongNumBean implements Serializable {

    public String totalCount;
    public String pageSize;
    public String totalPage;
    public String currPage;
    public List<SongLargeBean> list;

    /**
     * 解析歌曲大类列表Bean
     */
    public static class SongLargeBean implements Serializable{
        public String id;
        public String name;
        public String path;
        public String ngPath;

        public SongLargeBean() {

        }
    }
}
