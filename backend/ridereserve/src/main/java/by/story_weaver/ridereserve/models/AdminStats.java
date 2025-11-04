package by.story_weaver.ridereserve.models;

public class AdminStats {
    public long totalUsers;
    public long totalRoutes;
    public long totalTrips;
    public long totalBookings;
    public long totalVehicles;
    public long totalDrivers;
    public long activeTrips;
    public long scheduledTrips;
    public long confirmedBookings;
    public long cancelledBookings;
    
    public AdminStats(){}

    public AdminStats(long totalUsers, long totalRoutes, long totalTrips, long totalBookings, long totalVehicles, long totalDrivers, long activeTrips, long scheduledTrips, long confirmedBookings, long cancelledBookings) {
        this.totalUsers = totalUsers;
        this.totalRoutes = totalRoutes;
        this.totalTrips = totalTrips;
        this.totalBookings = totalBookings;
        this.totalVehicles = totalVehicles;
        this.totalDrivers = totalDrivers;
        this.activeTrips = activeTrips;
        this.scheduledTrips = scheduledTrips;
        this.confirmedBookings = confirmedBookings;
        this.cancelledBookings = cancelledBookings;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalRoutes() {
        return totalRoutes;
    }

    public void setTotalRoutes(long totalRoutes) {
        this.totalRoutes = totalRoutes;
    }

    public long getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(long totalTrips) {
        this.totalTrips = totalTrips;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(long totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public long getActiveTrips() {
        return activeTrips;
    }

    public void setActiveTrips(long activeTrips) {
        this.activeTrips = activeTrips;
    }

    public long getScheduledTrips() {
        return scheduledTrips;
    }

    public void setScheduledTrips(long scheduledTrips) {
        this.scheduledTrips = scheduledTrips;
    }

    public long getConfirmedBookings() {
        return confirmedBookings;
    }

    public void setConfirmedBookings(long confirmedBookings) {
        this.confirmedBookings = confirmedBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }
}
