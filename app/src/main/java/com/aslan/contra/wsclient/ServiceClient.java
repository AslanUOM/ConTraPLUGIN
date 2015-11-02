package com.aslan.contra.wsclient;

/**
 * Created by gobinath on 11/2/15.
 */
public abstract class ServiceClient<T> {
    private OnResponseListener<T> onResponseListener;

    public void setOnResponseListener(OnResponseListener<T> listener) {
        this.onResponseListener = listener;
    }

    public OnResponseListener<T> getOnResponseListener() {
        return this.onResponseListener;
    }
}
