package by.story_weaver.ridereserve.Logic.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

import by.story_weaver.ridereserve.Logic.data.enums.TripStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.data.models.Vehicle;
import by.story_weaver.ridereserve.R;

public class DriverTripsAdapter extends RecyclerView.Adapter<DriverTripsAdapter.TripViewHolder> {

    private List<Trip> trips;
    private List<Route> routes;
    private List<Vehicle> vehicles;
    private List<Booking> bookings;
    private OnTripClickListener listener;

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    public DriverTripsAdapter(List<Trip> trips, List<Route> routes, List<Vehicle> vehicles, List<Booking> bookings, OnTripClickListener listener) {
        this.trips = trips;
        this.bookings = bookings;
        this.routes = routes;
        this.vehicles = vehicles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_driver_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);

        Route route = findRouteById(trip.getRouteId());
        Vehicle vehicle = findVehicleById(trip.getVehicleId());

        holder.bind(trip, route, vehicle);
    }

    @Override
    public int getItemCount() {
        return trips != null ? trips.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateTrips(List<Trip> newTrips) {
        this.trips = newTrips;
        notifyDataSetChanged();
    }

    public void updateRoutes(List<Route> newRoutes) {
        this.routes = newRoutes;
        notifyDataSetChanged();
    }


    public void updateVehicles(List<Vehicle> newVehicles) {
        this.vehicles = newVehicles;
        notifyDataSetChanged();
    }

    public void updateAllData(List<Trip> newTrips, List<Route> newRoutes, List<Vehicle> newVehicles, List<Booking> newBookings) {
        this.trips = newTrips;
        this.routes = newRoutes;
        this.vehicles = newVehicles;
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    private Route findRouteById(long routeId) {
        if (routes == null) return null;
        for (Route route : routes) {
            if (route.getId() == routeId) {
                return route;
            }
        }
        return null;
    }

    private Vehicle findVehicleById(long vehicleId) {
        if (vehicles == null) return null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId() == vehicleId) {
                return vehicle;
            }
        }
        return null;
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTripId, tvTripStatus, tvDeparture, tvDestination;
        private TextView tvDateTime, tvPassengers, tvTotalPrice;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTripId = itemView.findViewById(R.id.tvTripId);
            tvTripStatus = itemView.findViewById(R.id.tvTripStatus);
            tvDeparture = itemView.findViewById(R.id.tvDeparture);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);

            setupClickListeners();
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onTripClick(trips.get(position));
                    }
                }
            });
        }

        public void bind(Trip trip, Route route, Vehicle vehicle) {
            tvTripId.setText("Поездка #" + trip.getId());
            tvDateTime.setText(formatDateTime(trip.getDepartureTime()));

            if (route != null) {
                tvDeparture.setText(route.getOrigin());
                tvDestination.setText(route.getDestination());
            } else {
                tvDeparture.setText("Неизвестно");
                tvDestination.setText("Неизвестно");
            }

            if (vehicle != null) {
                int currentPassengers = 0;

                for(Booking i: bookings){
                    if(i.getTripId() == trip.getId()){
                        currentPassengers++;
                    }
                }

                tvPassengers.setText(formatPassengers(currentPassengers, vehicle.getSeatsCount()));
                tvTotalPrice.setText(formatPrice(calculateTotalPrice(trip)));
            } else {
                tvPassengers.setText("0/0");
                tvTotalPrice.setText("0.00 BYN");
            }

            setupStatus(trip.getStatus());
        }

        private String formatDateTime(String dateTime) {
            return dateTime != null ? dateTime : "Не указано";
        }

        private String formatPassengers(int current, int max) {
            return String.format(Locale.getDefault(), "%d/%d", current, max);
        }

        private String formatPrice(double price) {
            return String.format(Locale.getDefault(), "%.2f BYN", price);
        }

        private double calculateTotalPrice(Trip trip) {
            double allPrice = 0;
            for(Booking i: bookings){
                if(i.getTripId() == trip.getId()){
                    allPrice += i.getPrice();
                }
            }
            return allPrice;
        }

        private void setupStatus(TripStatus status) {
            if (status == null) return;

            switch (status) {
                case SCHEDULED:
                    tvTripStatus.setText("Запланирована");
                    tvTripStatus.setBackgroundResource(R.drawable.bg_status_scheduled);
                    break;
                case IN_PROGRESS:
                    tvTripStatus.setText("В пути");
                    tvTripStatus.setBackgroundResource(R.drawable.bg_status_in_progress);
                    break;
                case COMPLETED:
                    tvTripStatus.setText("Завершена");
                    tvTripStatus.setBackgroundResource(R.drawable.bg_status_completed);
                    break;
                case CANCELLED:
                    tvTripStatus.setText("Отменена");
                    tvTripStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                    break;
                default:
                    tvTripStatus.setText("Неизвестно");
                    tvTripStatus.setBackgroundResource(R.drawable.bg_status_scheduled);
            }
        }
    }
}