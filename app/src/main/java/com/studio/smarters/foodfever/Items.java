package com.studio.smarters.foodfever;

/**
 * Created by daduc on 02-04-2018.
 */

public class Items {

    int price;
    String desc;

    public Items() {
    }

    public Items(int price, String desc) {
        this.price = price;
        this.desc = desc;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
