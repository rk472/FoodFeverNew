package com.studio.smarters.foodfever;

/**
 * Created by daduc on 02-04-2018.
 */

public class Items {

    int price;
    String desc;
    String availability;

    public Items(int price, String desc, String availability) {
        this.price = price;
        this.desc = desc;
        this.availability = availability;
    }

    public Items() {
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

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
