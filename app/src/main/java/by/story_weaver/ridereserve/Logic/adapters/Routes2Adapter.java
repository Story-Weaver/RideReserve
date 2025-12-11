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
import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.R;

public class Routes2Adapter extends RecyclerView.Adapter<Routes2Adapter.RouteViewHolder> {

    private List<Route> routes;
    private OnRouteClickListener listener;

    public interface OnRouteClickListener {
        void onBookClick(Route route);
        void onRouteClick(Route route);
    }

    public Routes2Adapter(List<Route> routes, OnRouteClickListener listener) {
        this.routes = routes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route2, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routes.get(position);
        holder.bind(route);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void clear(){
        routes.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return routes != null ? routes.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateRoutes(List<Route> newRoutes) {
        this.routes = newRoutes;
        notifyDataSetChanged();
    }

    public class RouteViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRouteName, tvDeparture, tvDestination;
        private TextView tvDuration, tvDistance, tvPrice;
        private Button btnBook;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvDeparture = itemView.findViewById(R.id.tvDeparture);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnBook = itemView.findViewById(R.id.btnBook);

            setupClickListeners();
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRouteClick(routes.get(position));
                    }
                }
            });

            btnBook.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onBookClick(routes.get(position));
                    }
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void bind(Route route) {
            tvRouteName.setText(route.getName());
            tvDeparture.setText(route.getOrigin());
            tvDestination.setText(route.getDestination());

            tvDuration.setText(route.getTime());
            tvDistance.setText(route.getDistance()+"");
        }

        private String formatDuration(int durationMinutes) {
            int hours = durationMinutes / 60;
            int minutes = durationMinutes % 60;
            return String.format("%dч %02dм", hours, minutes);
        }

        private String formatDistance(double distance) {
            return String.format("%.0f км", distance);
        }

        private String formatPrice(double price) {
            return String.format("%.2f BYN", price);
        }

        private void setupButtonText(String status) {
            if ("active".equals(status)) {
                btnBook.setText("Забронировать");
                btnBook.setEnabled(true);
            } else {
                btnBook.setText("Недоступно");
                btnBook.setEnabled(false);
            }
        }
    }
}