package by.story_weaver.ridereserve.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.AdminStats;
import by.story_weaver.ridereserve.models.Trip;
import by.story_weaver.ridereserve.services.AdminService;

@Service
public class AdminServiceImpl implements AdminService {
    
    @Override
    public AdminStats getAdminStats(){
        return null;
    }

    @Override
    public List<Trip> getActiveTrips(){
        return null;
    }
}
