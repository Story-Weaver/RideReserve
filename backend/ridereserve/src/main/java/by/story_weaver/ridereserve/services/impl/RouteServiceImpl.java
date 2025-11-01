package by.story_weaver.ridereserve.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.story_weaver.ridereserve.models.Route;
import by.story_weaver.ridereserve.repositories.RouteRepository;
import by.story_weaver.ridereserve.services.RouteService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RouteServiceImpl implements RouteService{

    @Autowired
    private RouteRepository routeRepository;

    @Override
    public List<Route> getAllRoutes() {
        try {
            return routeRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Route getRouteById(long id) {
        try {
            return routeRepository.getRouteById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Route createRoute(Route route) {
        try {
            return routeRepository.save(route);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Route updateRoute(Route route) {
        try {
            return routeRepository.save(route);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteRoute(long id) {
        try {
            routeRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    public List<String> getAllCities() {
        try {
            List<String> origins = routeRepository.findDistinctOrigins();
            List<String> destinations = routeRepository.findDistinctDestinations();
            Set<String> cities = new HashSet<>();
            if (origins != null) cities.addAll(origins);
            if (destinations != null) cities.addAll(destinations);
            // Сортировка (по желанию)
            List<String> sorted = new ArrayList<>(cities);
            sorted.sort(String::compareTo);
            return sorted;
        } catch (Exception e) {
            // лог (не глушим)
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public List<Route> searchRoutesByNumber(String text) {
        try {
            return routeRepository.findByNameContaining(text);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Route> searchRoutesByPoints(String from, String to) {
        try {
            return routeRepository.findByOriginAndDestination(from, to);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<String> getRoutePoints() {
        return null;
        //TODO
    }
    
}
