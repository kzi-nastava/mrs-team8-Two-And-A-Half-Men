package com.project.backend.service;

import com.project.backend.models.Driver;

public interface IActivityDriver {

    public boolean isTakingWork(Driver driver);
    public boolean workingHours(Driver driver , int hours);
    public void deActivateDriver(Driver driver);
    public void activateDriver(Driver driver);
    public void cancelRides(Driver driver);
    public boolean isDriving(Driver driver);

}
