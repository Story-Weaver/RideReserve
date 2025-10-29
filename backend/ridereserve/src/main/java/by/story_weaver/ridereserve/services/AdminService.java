package by.story_weaver.ridereserve.services;

import java.util.List;

import by.story_weaver.ridereserve.models.AdminStats;
import by.story_weaver.ridereserve.models.Trip;

public interface AdminService {
    public AdminStats getAdminStats();
    public List<Trip> getActiveTrips();
}