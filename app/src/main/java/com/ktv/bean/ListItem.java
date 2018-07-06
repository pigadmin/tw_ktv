package com.ktv.bean;

import java.io.Serializable;

public class ListItem implements Serializable {
    private int id;

    private String songnumber;

    private int singerid;

    private String name;

    private String nameVietnam;

    private String namejapanese;

    private String namezhuyin;

    private String namepinyin;

    private String path;

    private int lanId;

    private String label;

    private String singerName;

    private String lanName;

    private String fileName;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setSongnumber(String songnumber) {
        this.songnumber = songnumber;
    }

    public String getSongnumber() {
        return this.songnumber;
    }

    public void setSingerid(int singerid) {
        this.singerid = singerid;
    }

    public int getSingerid() {
        return this.singerid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setNameVietnam(String nameVietnam) {
        this.nameVietnam = nameVietnam;
    }

    public String getNameVietnam() {
        return this.nameVietnam;
    }

    public void setNamejapanese(String namejapanese) {
        this.namejapanese = namejapanese;
    }

    public String getNamejapanese() {
        return this.namejapanese;
    }

    public void setNamezhuyin(String namezhuyin) {
        this.namezhuyin = namezhuyin;
    }

    public String getNamezhuyin() {
        return this.namezhuyin;
    }

    public void setNamepinyin(String namepinyin) {
        this.namepinyin = namepinyin;
    }

    public String getNamepinyin() {
        return this.namepinyin;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setLanId(int lanId) {
        this.lanId = lanId;
    }

    public int getLanId() {
        return this.lanId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSingerName() {
        return this.singerName;
    }

    public void setLanName(String lanName) {
        this.lanName = lanName;
    }

    public String getLanName() {
        return this.lanName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }
}
