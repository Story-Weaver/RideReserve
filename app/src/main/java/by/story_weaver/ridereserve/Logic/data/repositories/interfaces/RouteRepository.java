package by.story_weaver.ridereserve.Logic.data.repositories.interfaces;

import by.story_weaver.ridereserve.Logic.data.models.Route;

import java.util.List;

public interface RouteRepository {
    void addRoute(Route route);
    void removeRoute(int id);
    Route getRoute(int id);
    List<Route> getAllRoutes();
}
