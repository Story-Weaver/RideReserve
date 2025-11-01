package by.story_weaver.ridereserve.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    try {
        List<Route> routes = routeService.getAllRoutes();
        return routes != null ? ResponseEntity.ok(routes) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    @GetMapping("/cities")
public ResponseEntity<List<String>> getAllCities() {
    try {
        List<String> cities = routeService.getAllCities();
        return ResponseEntity.ok(cities);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/{id}")
public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        Route route = routeService.getRouteById(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/search/number")
public ResponseEntity<List<Route>> searchRoutesByNumber(@RequestParam String text) {
    try {
        if (text == null) return ResponseEntity.badRequest().build();
        List<Route> routes = routeService.searchRoutesByNumber(text);
        return routes != null ? ResponseEntity.ok(routes) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/search/points")
public ResponseEntity<List<Route>> searchRoutesByPoints(
        @RequestParam String from,
        @RequestParam String to) {
    try {
        if (from == null || to == null) return ResponseEntity.badRequest().build();
        List<Route> routes = routeService.searchRoutesByPoints(from, to);
        return routes != null ? ResponseEntity.ok(routes) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@GetMapping("/get/points")
public ResponseEntity<List<String>> getRoutePoints(
        @RequestParam String from,
        @RequestParam String to) {
    try {
        // original service method had no params; keep safe call
        List<String> points = routeService.getRoutePoints();
        return points != null ? ResponseEntity.ok(points) : ResponseEntity.ok(List.of());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PostMapping
public ResponseEntity<Route> createRoute(@RequestBody Route route) {
    try {
        if (route == null) return ResponseEntity.badRequest().build();
        Route createdRoute = routeService.createRoute(route);
        return createdRoute != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(createdRoute)
                : ResponseEntity.badRequest().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@PutMapping("/")
public ResponseEntity<Route> updateRoute(@RequestBody Route route) {
    try {
        if (route == null || route.getId() == null) return ResponseEntity.badRequest().build();
        Route updatedRoute = routeService.updateRoute(route);
        return updatedRoute != null ? ResponseEntity.ok(updatedRoute) : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
    try {
        if (id == null) return ResponseEntity.badRequest().build();
        boolean deleted = routeService.deleteRoute(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

}