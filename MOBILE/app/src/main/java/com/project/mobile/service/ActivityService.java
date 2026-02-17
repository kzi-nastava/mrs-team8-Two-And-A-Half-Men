package com.project.mobile.service;

import retrofit2.Call;
import retrofit2.http.PATCH;

public interface ActivityService {

    @PATCH("driver/working/start")
    Call<Object> startWorkingStatus();

    @PATCH("driver/working/stop")
    Call<Object> stopWorkingStatus();
}
