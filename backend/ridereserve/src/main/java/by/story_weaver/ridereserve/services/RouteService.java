package by.story_weaver.ridereserve.services;

import java.util.List;

import by.story_weaver.ridereserve.models.Route;

public interface RouteService {
    public List<Route> getAllRoutes();
    public Route getRouteById(long id);
    public Route createRoute(Route route);
    public Route updateRoute(Route route);
    public boolean deleteRoute(long id);

    public List<Route> searchRoutesByNumber(String text);
    public List<Route> searchRoutesByPoints(String from, String to);
    public List<String> getRoutePoints();
}
