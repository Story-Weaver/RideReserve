package by.story_weaver.ridereserve.models;

public class AdminStats {
    public int totalUsers;
    public int totalRoutes;
    public int totalTrips;
    public int totalBookings;
    public int totalVehicles;
    public int totalDrivers;
    public int activeTrips;
    public int scheduledTrips;
    public int confirmedBookings;
    public int cancelledBookings;

    public AdminStats(int totalUsers, int totalRoutes, int totalTrips, int totalBookings, int totalVehicles, int totalDrivers, int activeTrips, int scheduledTrips, int confirmedBookings, int cancelledBookings) {
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

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalRoutes() {
        return totalRoutes;
    }

    public void setTotalRoutes(int totalRoutes) {
        this.totalRoutes = totalRoutes;
    }

    public int getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(int totalTrips) {
        this.totalTrips = totalTrips;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(int totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public int getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(int totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public int getActiveTrips() {
        return activeTrips;
    }

    public void setActiveTrips(int activeTrips) {
        this.activeTrips = activeTrips;
    }

    public int getScheduledTrips() {
        return scheduledTrips;
    }

    public void setScheduledTrips(int scheduledTrips) {
        this.scheduledTrips = scheduledTrips;
    }

    public int getConfirmedBookings() {
        return confirmedBookings;
    }

    public void setConfirmedBookings(int confirmedBookings) {
        this.confirmedBookings = confirmedBookings;
    }

    public int getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(int cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }
}
