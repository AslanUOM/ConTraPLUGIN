package com.aslan.contra.commons;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by gobinath on 10/29/15.
 */
public class App implements Serializable {
    private String title;
    private Drawable icon;
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
