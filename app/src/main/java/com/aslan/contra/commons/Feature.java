package com.aslan.contra.commons;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by gobinath on 10/29/15.
 */
public class Feature implements Serializable {
    private String name;
    private String description;
    private Drawable icon;
    private String[] permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }
}
