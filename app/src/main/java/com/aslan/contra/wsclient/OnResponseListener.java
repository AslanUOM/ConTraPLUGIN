package com.aslan.contra.wsclient;

/**
 * Created by gobinath on 11/2/15.
 */
public interface OnResponseListener<T> {
    public void onResponseReceived(T result);

    public Class getType();
}
