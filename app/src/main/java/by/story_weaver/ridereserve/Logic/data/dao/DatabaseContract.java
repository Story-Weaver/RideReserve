package by.story_weaver.ridereserve.Logic.data.dao;

public final class DatabaseContract {

    private DatabaseContract() {}

    public static final class Users {
        public static final String TABLE_NAME = "users";
        public static final String COL_ID = "id";
        public static final String COL_EMAIL = "email";
        public static final String COL_PASSWORD = "password";
        public static final String COL_FULL_NAME = "full_name";
        public static final String COL_PHONE = "phone";
        public static final String COL_ROLE = "role";
        public static final String COL_IN_SYSTEM = "status";
    }

    public static final class Routes {
        public static final String TABLE_NAME = "routes";

        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_ORIGIN = "origin";
        public static final String COL_DESTINATION = "destination";
        public static final String COL_STOPS_JSON = "stops_json";
    }

    public static final class Vehicles {
        public static final String TABLE_NAME = "vehicles";

        public static final String COL_ID = "id";
        public static final String COL_PLATE_NUMBER = "plate_number";
        public static final String COL_MODEL = "model";
        public static final String COL_SEATS_COUNT = "seats_count";
    }

    public static final class Seats {
        public static final String TABLE_NAME = "seats";

        public static final String COL_ID = "id";
        public static final String COL_VEHICLE_ID = "vehicle_id";
        public static final String COL_SEAT_NUMBER = "seat_number";
        public static final String COL_TAG = "tag";
    }

    public static final class Trips {
        public static final String TABLE_NAME = "trips";

        public static final String COL_ID = "id";
        public static final String COL_ROUTE_ID = "route_id";
        public static final String COL_VEHICLE_ID = "vehicle_id";
        public static final String COL_DRIVER_ID = "driver_id";
        public static final String COL_DEPARTURE_TIME = "departure_time";
        public static final String COL_ARRIVAL_TIME = "arrival_time";
        public static final String COL_STATUS = "status";
        public static final String COL_PRICE = "price";
    }

    public static final class Bookings {
        public static final String TABLE_NAME = "bookings";

        public static final String COL_ID = "id";
        public static final String COL_TRIP_ID = "trip_id";
        public static final String COL_PASSENGER_ID = "passenger_id";
        public static final String COL_SEAT_NUMBER = "seat_number";
        public static final String COL_CHILD_SEAT_NEEDED = "child_seat_needed";
        public static final String COL_HAS_PET = "has_pet";
        public static final String COL_STATUS = "status";
        public static final String COL_PRICE = "price";
    }
}
