package com.example.bysj;

import android.content.Intent;

public class MyCommodity {
    private String picturePath,name,state;
    private Integer num,numNow,id;
    private Double price;

    @Override
    public String toString() {
        return "MyCommodity{" +
                "picturePath='" + picturePath + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", price=" + price +
                ", num=" + num +
                ", numNow=" + numNow +
                ", id=" + id +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MyCommodity(Integer id, String picturePath, String name, String state, Double price, Integer num, Integer numNow) {
        this.picturePath = picturePath;
        this.id = id;
        this.name = name;
        this.state = state;
        this.price = price;
        this.num = num;
        this.numNow = numNow;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getNumNow() {
        return numNow;
    }

    public void setNumNow(Integer numNow) {
        this.numNow = numNow;
    }
}
