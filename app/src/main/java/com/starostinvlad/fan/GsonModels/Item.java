package com.starostinvlad.fan.GsonModels;

public class Item {

    int type;

    Object object;

    public Item(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }
}
