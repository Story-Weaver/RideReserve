package by.story_weaver.ridereserve.Logic.data;

import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.Logic.data.models.*;

import java.util.ArrayList;
import java.util.List;

public class DataSeeder {

    public SeederData generateTestData() {
        SeederData data = new SeederData();

        // Генерируем пользователей (водителей и пассажиров)
        data.users = generateUsers();

        // Генерируем маршруты
        data.routes = generateRoutes();

        // Генерируем транспорт
        data.vehicles = generateVehicles();

        // Генерируем места в транспорте
        data.seats = generateSeats(data.vehicles);

        // Генерируем рейсы (используем ID водителей из таблицы пользователей)
        data.trips = generateTrips(data.routes, data.vehicles, data.users);

        // Генерируем бронирования (используем ID пассажиров из таблицы пользователей)
        data.bookings = generateBookings(data.trips, data.seats, data.users);

        return data;
    }

    private List<User> generateUsers() {
        List<User> users = new ArrayList<>();

        // Водители
        users.add(new User(4L, "ivanov@mail.com", "password123", "Иванов Иван Иванович", "+375291234567", 1, UserRole.DRIVER));
        users.add(new User(5L, "petrov@mail.com", "password123", "Петров Петр Петрович", "+375292345678", 1, UserRole.DRIVER));
        users.add(new User(6L, "sidorov@mail.com", "password123", "Сидоров Алексей Владимирович", "+375293456789", 1, UserRole.DRIVER));
        users.add(new User(7L, "smirnov@mail.com", "password123", "Смирнов Дмитрий Олегович", "+375294567890", 1, UserRole.DRIVER));

        // Пассажиры
        users.add(new User(8L, "passenger1@mail.com", "password123", "Анна Ковалева", "+375295678901", 0, UserRole.PASSENGER));
        users.add(new User(9L, "passenger2@mail.com", "password123", "Сергей Новиков", "+375296789012", 0, UserRole.PASSENGER));
        users.add(new User(10L, "passenger3@mail.com", "password123", "Мария Волкова", "+375297890123", 0, UserRole.PASSENGER));
        users.add(new User(11L, "passenger4@mail.com", "password123", "Александр Белов", "+375298901234", 0, UserRole.PASSENGER));
        users.add(new User(12L, "passenger5@mail.com", "password123", "Екатерина Морозова", "+375299012345", 0, UserRole.PASSENGER));
        users.add(new User(13L, "passenger6@mail.com", "password123", "Дмитрий Павлов", "+375291123456", 0, UserRole.PASSENGER));
        users.add(new User(14L, "passenger7@mail.com", "password123", "Ольга Семенова", "+375292234567", 0, UserRole.PASSENGER));
        users.add(new User(15L, "passenger8@mail.com", "password123", "Михаил Федоров", "+375293345678", 0, UserRole.PASSENGER));
        users.add(new User(16L, "passenger9@mail.com", "password123", "Наталья Алексеева", "+375294456789", 0, UserRole.PASSENGER));
        users.add(new User(17L, "passenger10@mail.com", "password123", "Андрей Козлов", "+375295567890", 0, UserRole.PASSENGER));
        users.add(new User(18L, "passenger11@mail.com", "password123", "Ирина Лебедева", "+375296678901", 0, UserRole.PASSENGER));
        users.add(new User(19L, "passenger12@mail.com", "password123", "Владимир Егоров", "+375297789012", 0, UserRole.PASSENGER));
        users.add(new User(20L, "passenger13@mail.com", "password123", "Татьяна Орлова", "+375298890123", 0, UserRole.PASSENGER));

        return users;
    }

    private List<Route> generateRoutes() {
        List<Route> routes = new ArrayList<>();

        routes.add(new Route(1L, "Маршрут 107", "Москва", "Санкт-Петербург",1400, "1ч 30м",
                "[\"Москва\", \"Тверь\", \"Вышний Волочек\", \"Новгород\", \"Санкт-Петербург\"]"));

        routes.add(new Route(2L, "Маршрут 205", "Москва", "Казань", 100, "30м",
                "[\"Москва\", \"Владимир\", \"Нижний Новгород\", \"Чебоксары\", \"Казань\"]"));

        routes.add(new Route(3L, "Маршрут 301", "Москва", "Сочи", 1800, "2ч 30м",
                "[\"Москва\", \"Тула\", \"Воронеж\", \"Ростов-на-Дону\", \"Краснодар\", \"Сочи\"]"));

        routes.add(new Route(4L, "Маршрут 412", "Минск", "Вильнюс", 400, "1ч",
                "[\"Минск\", \"Молодечно\", \"Ошмяны\", \"Вильнюс\"]"));

        return routes;
    }

    private List<Vehicle> generateVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();

        vehicles.add(new Vehicle(1L, "А123ВС77", "Mercedes Sprinter", 16));
        vehicles.add(new Vehicle(2L, "В456DE77", "Ford Transit", 12));
        vehicles.add(new Vehicle(3L, "С789FG77", "Volkswagen Crafter", 18));
        vehicles.add(new Vehicle(4L, "Е321ХК77", "Peugeot Traveller", 8));

        return vehicles;
    }

    private List<Seat> generateSeats(List<Vehicle> vehicles) {
        List<Seat> seats = new ArrayList<>();
        long seatId = 1L;

        for (Vehicle vehicle : vehicles) {
            for (int i = 1; i <= vehicle.getSeatsCount(); i++) {
                String tag = determineSeatTag(i, vehicle.getSeatsCount());
                seats.add(new Seat(seatId++, vehicle.getId(), i, tag));
            }
        }

        return seats;
    }

    private String determineSeatTag(int seatNumber, int totalSeats) {
        if (seatNumber == 1) return "driver";
        if (seatNumber <= 4) return "front";
        if (seatNumber > totalSeats - 4) return "back";
        if (seatNumber % 2 == 0) return "window";
        return "aisle";
    }

    private List<Trip> generateTrips(List<Route> routes, List<Vehicle> vehicles, List<User> users) {
        List<Trip> trips = new ArrayList<>();

        List<User> drivers = getUsersByRole(users, UserRole.DRIVER);

        // Рейсы для маршрута Москва - СПб
        trips.add(new Trip(1L, 4, drivers.get(0).getId(), "2024-01-15 08:00", "2024-01-15 14:30", TripStatus.SCHEDULED, 14));
        trips.add(new Trip(1L, 5, drivers.get(1).getId(), "2024-01-15 14:00", "2024-01-15 20:30", TripStatus.SCHEDULED, 14));
        trips.add(new Trip(1L, 6, drivers.get(2).getId(), "2024-01-16 09:00", "2024-01-16 15:30", TripStatus.SCHEDULED, 14));

        // Рейсы для маршрута Москва - Казань
        trips.add(new Trip(2L, 4, drivers.get(0).getId(), "2024-01-15 10:00", "2024-01-15 17:00", TripStatus.SCHEDULED, 14));
        trips.add(new Trip(2L, 7, drivers.get(3).getId(), "2024-01-16 07:30", "2024-01-16 14:30", TripStatus.SCHEDULED, 14));

        // Рейсы для маршрута Москва - Сочи
        trips.add(new Trip(3L, 6, drivers.get(2).getId(), "2024-01-15 06:00", "2024-01-15 18:00", TripStatus.IN_PROGRESS, 14));
        trips.add(new Trip(3L, 4, drivers.get(0).getId(), "2024-01-16 05:30", "2024-01-16 17:30", TripStatus.SCHEDULED, 14));

        // Рейсы для маршрута Минск - Вильнюс
        trips.add(new Trip(4L, 7, drivers.get(3).getId(), "2024-01-15 12:00", "2024-01-15 15:30", TripStatus.COMPLETED, 14));
        trips.add(new Trip(4L, 5, drivers.get(1).getId(), "2024-01-16 11:00", "2024-01-16 14:30", TripStatus.SCHEDULED, 14));

        return trips;
    }

    private List<Booking> generateBookings(List<Trip> trips, List<Seat> seats, List<User> users) {
        List<Booking> bookings = new ArrayList<>();

        List<User> passengers = getUsersByRole(users, UserRole.PASSENGER);

        // Бронирования для разных рейсов
        bookings.add(new Booking(1L, 1L, passengers.get(0).getId(), 2, false, false, BookingStatus.CONFIRMED, 1500.00));
        bookings.add(new Booking(2L, 1L, passengers.get(1).getId(), 3, true, false, BookingStatus.CONFIRMED, 1500.00));
        bookings.add(new Booking(3L, 1L, passengers.get(2).getId(), 5, false, true, BookingStatus.PENDING, 1500.00));

        bookings.add(new Booking(4L, 2L, passengers.get(3).getId(), 1, false, false, BookingStatus.CONFIRMED, 1400.00));
        bookings.add(new Booking(5L, 2L, passengers.get(4).getId(), 4, true, true, BookingStatus.CONFIRMED, 1400.00));

        bookings.add(new Booking(6L, 3L, passengers.get(5).getId(), 6, false, false, BookingStatus.PENDING, 1600.00));

        bookings.add(new Booking(7L, 4L, passengers.get(6).getId(), 2, false, false, BookingStatus.COMPLETED, 1200.00));
        bookings.add(new Booking(8L, 4L, passengers.get(7).getId(), 3, false, false, BookingStatus.COMPLETED, 1200.00));

        bookings.add(new Booking(9L, 6L, passengers.get(8).getId(), 1, false, false, BookingStatus.CONFIRMED, 2500.00));
        bookings.add(new Booking(10L, 6L, passengers.get(9).getId(), 4, true, false, BookingStatus.CONFIRMED, 2500.00));
        bookings.add(new Booking(11L, 6L, passengers.get(10).getId(), 5, false, false, BookingStatus.CONFIRMED, 2500.00));

        bookings.add(new Booking(12L, 8L, passengers.get(11).getId(), 2, false, false, BookingStatus.COMPLETED, 800.00));
        bookings.add(new Booking(13L, 8L, passengers.get(12).getId(), 3, false, true, BookingStatus.CANCELLED, 800.00));

        return bookings;
    }

    private List<User> getUsersByRole(List<User> users, UserRole role) {
        List<User> filteredUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == role) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    // Вложенный класс для хранения всех сгенерированных данных
    public static class SeederData {
        public List<User> users;
        public List<Route> routes;
        public List<Vehicle> vehicles;
        public List<Seat> seats;
        public List<Trip> trips;
        public List<Booking> bookings;

        public SeederData() {
            this.users = new ArrayList<>();
            this.routes = new ArrayList<>();
            this.vehicles = new ArrayList<>();
            this.seats = new ArrayList<>();
            this.trips = new ArrayList<>();
            this.bookings = new ArrayList<>();
        }

        public void printStatistics() {
            System.out.println("=== СГЕНЕРИРОВАННЫЕ ТЕСТОВЫЕ ДАННЫЕ ===");
            System.out.println("Пользователи: " + users.size());
            System.out.println("  - Водители: " + countUsersByRole(UserRole.DRIVER));
            System.out.println("  - Пассажиры: " + countUsersByRole(UserRole.PASSENGER));
            System.out.println("Маршруты: " + routes.size());
            System.out.println("Транспортные средства: " + vehicles.size());
            System.out.println("Места: " + seats.size());
            System.out.println("Рейсы: " + trips.size());
            System.out.println("Бронирования: " + bookings.size());

            // Статистика по статусам бронирований
            long confirmed = bookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
            long pending = bookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count();
            long completed = bookings.stream().filter(b -> b.getStatus() == BookingStatus.COMPLETED).count();
            long cancelled = bookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();

            System.out.println("\nСтатистика бронирований:");
            System.out.println("Подтвержденные: " + confirmed);
            System.out.println("Ожидающие: " + pending);
            System.out.println("Завершенные: " + completed);
            System.out.println("Отмененные: " + cancelled);

            // Статистика по дополнительным услугам
            long withChildSeat = bookings.stream().filter(Booking::isChildSeatNeeded).count();
            long withPets = bookings.stream().filter(Booking::isHasPet).count();

            System.out.println("\nДополнительные услуги:");
            System.out.println("Детские кресла: " + withChildSeat);
            System.out.println("Перевозка животных: " + withPets);
        }

        private long countUsersByRole(UserRole role) {
            return users.stream().filter(u -> u.getRole() == role).count();
        }

        // Методы для получения конкретных пользователей по роли
        public List<User> getDrivers() {
            return getUsersByRole(UserRole.DRIVER);
        }

        public List<User> getPassengers() {
            return getUsersByRole(UserRole.PASSENGER);
        }

        private List<User> getUsersByRole(UserRole role) {
            List<User> filtered = new ArrayList<>();
            for (User user : users) {
                if (user.getRole() == role) {
                    filtered.add(user);
                }
            }
            return filtered;
        }
    }
}