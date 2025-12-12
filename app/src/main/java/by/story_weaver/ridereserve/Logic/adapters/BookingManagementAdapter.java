package by.story_weaver.ridereserve.Logic.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.utils.TranslateStatus;
import by.story_weaver.ridereserve.R;
import com.google.android.material.button.MaterialButton;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BookingManagementAdapter extends RecyclerView.Adapter<BookingManagementAdapter.BookingViewHolder> {

    private List<Booking> bookings = new ArrayList<>();
    private final BookingActionListener actionListener;

    public BookingManagementAdapter(BookingActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public interface BookingActionListener {
        void onConfirmBooking(long bookingId);
        void onCancelBooking(long bookingId);
        void onShowDetails(long bookingId);
    }

    public void submitList(List<Booking> newBookings) {
        this.bookings = newBookings != null ? new ArrayList<>(newBookings) : new ArrayList<>();
        notifyDataSetChanged();
        Log.d("BookingAdapter", "Submitted list with " + bookings.size() + " items");
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        if (position < 0 || position >= bookings.size()) {
            Log.e("BookingAdapter", "Invalid position: " + position);
            return;
        }

        Booking booking = bookings.get(position);
        Log.d("BookingAdapter", "Binding position " + position + ", booking #" + booking.getId() + ", status: " + booking.getStatus());
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBookingId, tvStatus, tvRoute, tvDepartureTime,
                tvPassenger, tvServices, tvPrice;
        private final MaterialButton btnConfirm, btnCancel;
        private final LinearLayout llCardContent;

        public BookingViewHolder(View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvPassenger = itemView.findViewById(R.id.tvPassenger);
            tvServices = itemView.findViewById(R.id.tvServices);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            llCardContent = itemView.findViewById(R.id.llCardContent); // Добавьте этот ID в макет
        }

        public void bind(Booking booking) {
            if (booking == null) {
                Log.e("BookingAdapter", "Booking is null at position " + getAdapterPosition());
                return;
            }

            // Сначала делаем всю карточку видимой
            itemView.setVisibility(View.VISIBLE);
            if (llCardContent != null) {
                llCardContent.setVisibility(View.VISIBLE);
            }

            tvBookingId.setText("Бронь #" + booking.getId());

            // Устанавливаем статус
            setStatusUI(booking.getStatus());

            // Заполняем данными
            tvRoute.setText("Маршрут ID: " + booking.getTripId());
            tvDepartureTime.setText("Время отправления");
            tvPassenger.setText("Пассажир ID: " + booking.getPassengerId());
            tvServices.setText(getServicesText(booking));
            tvPrice.setText(formatPrice(booking.getPrice()));

            // Настройка видимости кнопок в зависимости от статуса
            setupActionButtons(booking.getStatus(), booking.getId());

            // Обработчики кликов
            btnConfirm.setOnClickListener(v -> {
                Log.d("BookingAdapter", "Confirm booking: " + booking.getId());
                actionListener.onConfirmBooking(booking.getId());
            });

            btnCancel.setOnClickListener(v -> {
                Log.d("BookingAdapter", "Cancel booking: " + booking.getId());
                actionListener.onCancelBooking(booking.getId());
            });

            // Клик на всю карточку для деталей
            itemView.setOnClickListener(v -> {
                Log.d("BookingAdapter", "Show details: " + booking.getId());
                actionListener.onShowDetails(booking.getId());
            });
        }

        private void setStatusUI(BookingStatus status) {
            int bgResId;
            String statusText;

            if (status == null) {
                status = BookingStatus.PENDING;
            }

            switch (status) {
                case PENDING:
                    bgResId = R.drawable.bg_status_pending;
                    statusText = TranslateStatus.get(status);
                    break;
                case CONFIRMED:
                    bgResId = R.drawable.bg_status_confirmed;
                    statusText = TranslateStatus.get(status);
                    break;
                case CANCELLED:
                    bgResId = R.drawable.bg_status_cancelled;
                    statusText = TranslateStatus.get(status);
                    break;
                case COMPLETED:
                    bgResId = R.drawable.bg_status_completed;
                    statusText = TranslateStatus.get(status);
                    break;
                default:
                    bgResId = R.drawable.bg_status_pending;
                    statusText = TranslateStatus.get(status);
            }

            tvStatus.setText(statusText);
            tvStatus.setBackgroundResource(bgResId);
        }

        private void setupActionButtons(BookingStatus status, long bookingId) {
            Log.d("BookingAdapter", "Setup buttons for booking " + bookingId + ", status: " + status);

            if (status == null) {
                status = BookingStatus.PENDING;
            }

            switch (status) {
                case PENDING:
                    btnConfirm.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnConfirm.setEnabled(true);
                    btnCancel.setEnabled(true);
                    break;
                case CONFIRMED:
                    btnConfirm.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                    btnCancel.setEnabled(true);
                    break;
                case CANCELLED:
                case COMPLETED:
                    btnConfirm.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
                    break;
                default:
                    btnConfirm.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
            }

            // Логирование для отладки
            Log.d("BookingAdapter", "Button visibility - Confirm: " +
                    (btnConfirm.getVisibility() == View.VISIBLE) +
                    ", Cancel: " + (btnCancel.getVisibility() == View.VISIBLE));
        }

        private String getServicesText(Booking booking) {
            StringBuilder services = new StringBuilder();

            Boolean childSeat = booking.isChildSeatNeeded();
            Boolean hasPet = booking.isHasPet();

            if (childSeat != null && childSeat) {
                services.append("Детское кресло");
            }
            if (hasPet != null && hasPet) {
                if (services.length() > 0) services.append(", ");
                services.append("Животное");
            }

            return services.length() > 0 ? services.toString() : "Нет дополнительных услуг";
        }

        private String formatPrice(Double price) {
            if (price == null) {
                return "0.00 BYN";
            }
            DecimalFormat df = new DecimalFormat("#,##0.00");
            return df.format(price) + " BYN";
        }
    }
}