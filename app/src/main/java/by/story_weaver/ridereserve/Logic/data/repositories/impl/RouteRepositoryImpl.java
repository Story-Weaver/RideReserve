package by.story_weaver.ridereserve.Logic.data.repositories.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Collections;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.dao.RouteDao;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.repositories.interfaces.RouteRepository;

@Singleton
public class RouteRepositoryImpl implements RouteRepository {
    private final RouteDao dao;

    @Inject
    public RouteRepositoryImpl(RouteDao dao) { this.dao = dao; }

    @Override
    public void addRoute(Route route) { dao.addRoute(route); }

    @Override
    public void removeRoute(int id) { dao.removeRoute(id); }

    @Override
    public Route getRoute(long id) { return dao.getRoute(id); }

    @Override
    public List<Route> getAllRoutes() { return dao.getAllRoutes(); }

    @Override
    public List<Route> getRoutesByNumber(String num) {
        return dao.searchRoutesByNumber(num);
    }

    @Override
    public List<Route> geetRoutesByPoints(String from, String to) {
        return dao.searchRoutesByPoints(from, to);
    }
}
