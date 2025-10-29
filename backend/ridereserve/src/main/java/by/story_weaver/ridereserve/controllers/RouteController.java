package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import by.story_weaver.ridereserve.models.Route;
import by.story_weaver.ridereserve.services.RouteService;
import lombok.AllArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@AllArgsConstructor
public class RouteController {
    
    @Autowired
    private RouteService routeService;
    
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        List<Route> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        Route route = routeService.getRouteById(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/search/number")
    public ResponseEntity<List<Route>> searchRoutesByNumber(@RequestParam String text) {
        List<Route> routes = routeService.searchRoutesByNumber(text);
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/search/points")
    public ResponseEntity<List<Route>> searchRoutesByPoints(
            @RequestParam String from, 
            @RequestParam String to) {
        List<Route> routes = routeService.searchRoutesByPoints(from, to);
        return ResponseEntity.ok(routes);
    }
    
    @GetMapping("/get/points")
    public ResponseEntity<List<String>> getRoutePoints(
            @RequestParam String from, 
            @RequestParam String to) {
        List<String> routes = routeService.getRoutePoints();
        return ResponseEntity.ok(routes);
    }
    @PostMapping
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        Route createdRoute = routeService.createRoute(route);
        return ResponseEntity.ok(createdRoute);
    }
    
    @PutMapping("/")
    public ResponseEntity<Route> updateRoute(@RequestBody Route route) {
        Route updatedRoute = routeService.updateRoute(route);
        return updatedRoute != null ? ResponseEntity.ok(updatedRoute) : ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        boolean deleted = routeService.deleteRoute(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}