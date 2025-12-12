package by.story_weaver.ridereserve.Logic.utils;

import java.util.EnumMap;
import java.util.Map;

import by.story_weaver.ridereserve.Logic.data.enums.*;

public class TranslateStatus {
    private static final Map<BookingStatus, String> BOOKING_STATUS_NAMES = new EnumMap<>(BookingStatus.class);
    private static final Map<TripStatus, String> TRIP_STATUS_NAMES = new EnumMap<>(TripStatus.class);
    private static final Map<UserRole, String> USER_ROLE_NAMES = new EnumMap<>(UserRole.class);

    static {
        BOOKING_STATUS_NAMES.put(BookingStatus.PENDING, "Ожидает подтверждения");
        BOOKING_STATUS_NAMES.put(BookingStatus.CONFIRMED, "Подтверждён");
        BOOKING_STATUS_NAMES.put(BookingStatus.CANCELLED, "Отменён");
        BOOKING_STATUS_NAMES.put(BookingStatus.COMPLETED, "Завершён");

        TRIP_STATUS_NAMES.put(TripStatus.SCHEDULED, "Запланирован");
        TRIP_STATUS_NAMES.put(TripStatus.IN_PROGRESS, "В пути");
        TRIP_STATUS_NAMES.put(TripStatus.COMPLETED, "Завершён");
        TRIP_STATUS_NAMES.put(TripStatus.CANCELLED, "Отменён");

        USER_ROLE_NAMES.put(UserRole.PASSENGER, "Пассажир");
        USER_ROLE_NAMES.put(UserRole.DRIVER, "Водитель");
        USER_ROLE_NAMES.put(UserRole.ADMIN, "Администратор");
        USER_ROLE_NAMES.put(UserRole.GUEST, "Гость");
    }

    private TranslateStatus() {}

    public static String get(BookingStatus status) {
        return BOOKING_STATUS_NAMES.getOrDefault(status, status.name());
    }

    public static String get(TripStatus status) {
        return TRIP_STATUS_NAMES.getOrDefault(status, status.name());
    }

    public static String get(UserRole role) {
        return USER_ROLE_NAMES.getOrDefault(role, role.name());
    }
}
