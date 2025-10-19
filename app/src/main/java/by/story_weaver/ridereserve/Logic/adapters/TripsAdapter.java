package by.story_weaver.ridereserve.Logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.Trip;
import by.story_weaver.ridereserve.R;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripViewHolder> {
    private List<Trip> trips;
    private final OnTripClickListener listener;

    public TripsAdapter(List<Trip> trips, OnTripClickListener listener) {
        this.trips = trips;
        this.listener = listener;
    }

    public void updateTrips(List<Trip> newTrips) {
        this.trips = newTrips;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.bind(trips.get(position));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDepartureTime, tvArrivalTime, tvPrice;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvArrivalTime = itemView.findViewById(R.id.tvArrivalTime);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onTripClick(trips.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Trip trip) {
            tvDepartureTime.setText(trip.getDepartureTime());
            tvArrivalTime.setText(trip.getArrivalTime());
            tvPrice.setText(String.format("%.2f ₽", trip.getPrice()));
        }
    }

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }
}