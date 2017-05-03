package com.example.greyson.test1.net;

import com.example.greyson.test1.entity.MyMarker;

import java.util.Map;

import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * This class is interface
 *
 * @author Greyson, Carson
 * @version 1.0
 */


public interface WSNetService3 {

    @POST("json?")
    Observable<MyMarker> postLocation(@QueryMap Map<String, String> params);

}