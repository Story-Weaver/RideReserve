package by.story_weaver.ridereserve.Logic.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.Logic.utils.TranslateStatus;
import by.story_weaver.ridereserve.R;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private List<Route> routeList;
    private List<Trip> tripList;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public BookingAdapter(List<Booking> bookingList, List<Route> routeList, List<Trip> tripList) {
        this.bookingList = bookingList != null ? bookingList : new ArrayList<>();
        this.routeList = routeList != null ? routeList : new ArrayList<>();
        this.tripList = tripList != null ? tripList : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_card, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        if (bookingList == null || position < 0 || position >= bookingList.size()) {
            return;
        }

        Booking currentBooking = bookingList.get(position);
        Trip currentTrip = findTripById(currentBooking != null ? currentBooking.getTripId() : null);
        Route currentRoute = findRouteByTrip(currentTrip);

        holder.bind(currentBooking, currentTrip, currentRoute);
    }

    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    // безопасное обновление списков
    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapterBookings(List<Booking> newBookings) {
        this.bookingList = newBookings != null ? new ArrayList<>(newBookings) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapterRoutes(List<Route> newRoute) {
        this.routeList = newRoute != null ? new ArrayList<>(newRoute) : new ArrayList<>();
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateAdapterTrips(List<Trip> newTrips) {
        this.tripList = newTrips != null ? new ArrayList<>(newTrips) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public Booking getBookingAt(int position) {
        if (bookingList != null && position >= 0 && position < bookingList.size()) {
            return bookingList.get(position);
        }
        return null;
    }

    // Вспомогательные поиски — защищены от null
    private Trip findTripById(Long tripId) {
        if (tripId == null || tripList == null) return null;
        for (Trip t : tripList) {
            if (t != null && Objects.equals(tripId, t.getId())) {
                return t;
            }
        }
        return null;
    }

    private Route findRouteByTrip(Trip trip) {
        if (trip == null || routeList == null) return null;
        Long routeId = trip.getRouteId();
        if (routeId == null) return null;
        for (Route r : routeList) {
            if (r != null && Objects.equals(routeId, r.getId())) {
                return r;
            }
        }
        return null;
    }

    // ViewHolder
    public class BookingViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView tvRouteNumber;
        private TextView tvStatus;
        private TextView tvRoute;
        private TextView tvDateTime;
        private TextView tvPassengers;
        private TextView tvPrice;
        private CheckBox checkChild;
        private CheckBox checkPet;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            initializeViews(itemView);
            setupClickListeners();
        }

        private void initializeViews(View itemView) {
            cardView = itemView.findViewById(R.id.cardView);
            tvRouteNumber = itemView.findViewById(R.id.tvRouteNumber);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDateTime = itemView.findViewById(R.id.trDateTime);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);
            tvPrice = itemView.findViewById(R.id.trPrice);
            checkChild = itemView.findViewById(R.id.checkChild);
            checkPet = itemView.findViewById(R.id.checkPet);
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (itemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemLongClick(position);
                        return true;
                    }
                }
                return false;
            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(Booking booking, Trip trip, Route route) {
            String routeName = route != null && route.getName() != null ? route.getName() : "—";
            String destination = route != null && route.getDestination() != null ? route.getDestination() : "—";
            String arrivalTime = trip != null && trip.getArrivalTime() != null ? trip.getArrivalTime() : "—";
            String passengers = booking != null ? String.valueOf(booking.getSeatNumber()) : "—";
            double price = booking != null ? booking.getPrice() : 0.0;
            BookingStatus status = booking != null ? booking.getStatus() : null;

            tvRouteNumber.setText("Маршрут: " + routeName);
            tvStatus.setText(status != null ? TranslateStatus.get(status) : "UNKNOWN");
            tvRoute.setText(destination);
            tvDateTime.setText(arrivalTime);
            checkChild.setChecked(booking != null && booking.isChildSeatNeeded());
            checkPet.setChecked(booking != null && booking.isHasPet());
            //tvPassengers.setText("Пассажиры: " + passengers);
            tvPrice.setText(String.format(Locale.getDefault(), "%.2f BYN", price));

            setupStatusAppearance(status);
        }

        private void setupStatusAppearance(BookingStatus status) {
            int backgroundColorRes;
            int textColorRes;

            if (status == null) {
                backgroundColorRes = R.color.gray;
                textColorRes = android.R.color.black;
            } else {
                switch (status) {
                    case CONFIRMED:
                        backgroundColorRes = R.color.green;
                        textColorRes = R.color.white;
                        break;
                    case PENDING:
                        backgroundColorRes = R.color.orange;
                        textColorRes = R.color.white;
                        break;
                    case CANCELLED:
                        backgroundColorRes = R.color.red;
                        textColorRes = R.color.white;
                        break;
                    case COMPLETED:
                        backgroundColorRes = R.color.gray_700;
                        textColorRes = R.color.white;
                        break;
                    default:
                        backgroundColorRes = R.color.gray;
                        textColorRes = android.R.color.black;
                        break;
                }
            }

            int bg = ContextCompat.getColor(itemView.getContext(), backgroundColorRes);
            int txt = ContextCompat.getColor(itemView.getContext(), textColorRes);
            tvStatus.setBackgroundColor(bg);
            tvStatus.setTextColor(txt);
        }
    }
}
