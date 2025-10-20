package by.story_weaver.ridereserve.Logic.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.Logic.data.models.Trip;
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
        this.bookingList = bookingList;
        this.routeList = routeList;
        this.tripList = tripList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаем view из XML-макета
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_card, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        // Получаем данные для текущей позиции
        Booking currentBooking = bookingList.get(position);
        Trip currentTrip = null;
        Route currentRoute = null;
        for(int i = 0; i < tripList.size(); i++){
            if(currentBooking.getTripId() == tripList.get(i).getId()){
                currentTrip = tripList.get(i);
            }
        }
        for(int i = 0; i < routeList.size(); i++){
            if(currentTrip.getRouteId() == routeList.get(i).getId()){
                currentRoute = routeList.get(i);
            }
        }

        holder.bind(currentBooking, currentTrip, currentRoute);
    }

    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    // Обновление списка бронирований
    @SuppressLint("NotifyDataSetChanged")
    public void updateBookings(List<Booking> newBookings) {
        bookingList.clear();
        this.bookingList = newBookings;
        notifyDataSetChanged(); // Уведомляем адаптер об изменениях
    }

    // Получение брони по позиции
    public Booking getBookingAt(int position) {
        if (position >= 0 && position < bookingList.size()) {
            return bookingList.get(position);
        }
        return null;
    }

    // ViewHolder класс - отвечает за отображение одного элемента
    public class BookingViewHolder extends RecyclerView.ViewHolder {

        // Объявляем все View из макета
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemLongClick(position);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(Booking booking, Trip trip, Route route) {
            tvRouteNumber.setText("Маршрут № " + route.getName());
            tvStatus.setText(booking.getStatus().toString());
            tvRoute.setText(route.getDestination());
            tvDateTime.setText(trip.getArrivalTime());
            checkChild.setChecked(booking.isChildSeatNeeded());
            checkPet.setChecked(booking.isHasPet());
            tvPassengers.setText("Пассажиры: " + booking.getSeatNumber());
            tvPrice.setText(String.format(Locale.getDefault(), "%.2f BYN", booking.getPrice()));

            setupStatusAppearance(booking.getStatus());
        }

        private void setupStatusAppearance(BookingStatus status) {
            int backgroundColor;
            int textColor;

            switch (status) {
                case CONFIRMED:
                    backgroundColor = R.color.green;
                    textColor = R.color.white;
                    break;
                case PENDING:
                    backgroundColor = R.color.orange;
                    textColor = R.color.white;
                    break;
                case CANCELLED:
                    backgroundColor = R.color.red;
                    textColor = R.color.white;
                    break;
                case COMPLETED:
                    backgroundColor = R.color.gray_700;
                    textColor = R.color.white;
                default:
                    backgroundColor = R.color.gray;
                    textColor = android.R.color.black;
                    break;
            }

            tvStatus.setBackgroundColor(itemView.getContext().getResources().getColor(backgroundColor));
            tvStatus.setTextColor(itemView.getContext().getResources().getColor(textColor));
        }
    }
}