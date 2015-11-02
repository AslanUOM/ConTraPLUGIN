package com.aslan.contra.wsclient;

/**
 * Created by gobinath on 11/2/15.
 */
public abstract class ServiceClient<S, F> {
    private OnResponseListener<S, F> onResponseListener;

    public void setOnResponseListener(OnResponseListener listener) {
        this.onResponseListener = listener;
    }

    public OnResponseListener<S, F> getOnResponseListener() {
        return this.onResponseListener;
    }
}
