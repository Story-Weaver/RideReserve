package by.story_weaver.ridereserve.Logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import by.story_weaver.ridereserve.Logic.data.enums.BookingStatus;
import by.story_weaver.ridereserve.Logic.data.models.Booking;
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
        Booking booking = bookings.get(position);
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
        }

        public void bind(Booking booking) {
            tvBookingId.setText("Бронь #" + booking.getId());

            // Устанавливаем статус
            setStatusUI(booking.getStatus());

            // Заполняем данными
            tvRoute.setText("Информация о маршруте");
            tvDepartureTime.setText("Время отправления");
            tvPassenger.setText("Пассажир ID: " + booking.getPassengerId());
            tvServices.setText(getServicesText(booking));
            tvPrice.setText(formatPrice(booking.getPrice()));

            // Настройка видимости кнопок в зависимости от статуса
            setupActionButtons(booking.getStatus());

            // Обработчики кликов
            btnConfirm.setOnClickListener(v ->
                    actionListener.onConfirmBooking(booking.getId()));

            btnCancel.setOnClickListener(v ->
                    actionListener.onCancelBooking(booking.getId()));

            // Клик на всю карточку для деталей
            itemView.setOnClickListener(v ->
                    actionListener.onShowDetails(booking.getId()));
        }

        private void setStatusUI(BookingStatus status) {
            int bgResId;
            String statusText;

            switch (status) {
                case PENDING:
                    bgResId = R.drawable.bg_status_pending;
                    statusText = "ОЖИДАЕТ";
                    break;
                case CONFIRMED:
                    bgResId = R.drawable.bg_status_confirmed;
                    statusText = "ПОДТВЕРЖДЕНО";
                    break;
                case CANCELLED:
                    bgResId = R.drawable.bg_status_cancelled;
                    statusText = "ОТМЕНЕНО";
                    break;
                default:
                    bgResId = R.drawable.bg_status_pending;
                    statusText = "ОЖИДАЕТ";
            }

            tvStatus.setText(statusText);
            tvStatus.setBackgroundResource(bgResId);
        }

        private void setupActionButtons(BookingStatus status) {
            switch (status) {
                case PENDING:
                    btnConfirm.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.VISIBLE);
                    break;
                case CONFIRMED:
                    btnConfirm.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.VISIBLE);
                    break;
                default:
                    btnConfirm.setVisibility(View.GONE);
                    btnCancel.setVisibility(View.GONE);
            }
        }

        private String getServicesText(Booking booking) {
            StringBuilder services = new StringBuilder();
            if (booking.isChildSeatNeeded()) {
                services.append("Детское кресло");
            }
            if (booking.isHasPet()) {
                if (services.length() > 0) services.append(", ");
                services.append("Животное");
            }
            return services.length() > 0 ? services.toString() : "Нет дополнительных услуг";
        }

        private String formatPrice(double price) {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            return df.format(price) + " BYN";
        }
    }
}