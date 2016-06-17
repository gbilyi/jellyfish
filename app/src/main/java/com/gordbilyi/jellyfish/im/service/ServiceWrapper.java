package com.gordbilyi.jellyfish.im.service;

/**
 * Created by gordbilyi on 5/26/16.
 */
public interface ServiceWrapper {

    /**
     * Bind to the associated service
     */
    void bind();

    /**
     * Unbind from the associated service
     */
    void unbind();

    /**
     * Invoke whatever default functionality implemented by associated service
     */
    boolean invoke();
}
