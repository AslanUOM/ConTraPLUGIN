package com.aslan.contra.wsclient;

/**
 * Created by gobinath on 11/2/15.
 */
public interface OnResponseListener<S, F> {
    public void onSucced(S result);

    public void onFailed(F result);
}
