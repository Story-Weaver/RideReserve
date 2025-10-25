package by.story_weaver.ridereserve.Logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.Booking;
import by.story_weaver.ridereserve.Logic.data.models.User;
import by.story_weaver.ridereserve.R;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {

    private List<User> passengers;
    private List<Booking> bookings;

    public PassengerAdapter(List<User> passengers) {
        this.passengers = passengers;
    }

    public void updatePassengers(List<User> newPassengers, List<Booking> newBookings) {
        this.passengers = newPassengers;
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passenger, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        User passenger = passengers.get(position);
        Booking booking = bookings != null && position < bookings.size() ? bookings.get(position) : null;
        holder.bind(passenger, booking);
    }

    @Override
    public int getItemCount() {
        return passengers != null ? passengers.size() : 0;
    }

    static class PassengerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPassengerName, tvPassengerPhone;

        public PassengerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPassengerName = itemView.findViewById(R.id.tvPassengerName);
            tvPassengerPhone = itemView.findViewById(R.id.tvPassengerPhone);
        }

        public void bind(User passenger, Booking booking) {
            tvPassengerName.setText(passenger.getFullName());
            tvPassengerPhone.setText(passenger.getPhone());
        }

    }
}