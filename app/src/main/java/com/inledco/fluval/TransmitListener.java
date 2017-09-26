package com.inledco.fluval;

/**
 * Created by liruya on 2017/7/7.
 */

public interface TransmitListener
{
    void onReceive( String buffer );

    void onReceiveTimeout();

    void onReceiveError();
}
