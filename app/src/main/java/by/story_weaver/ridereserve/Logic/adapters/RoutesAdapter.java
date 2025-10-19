package by.story_weaver.ridereserve.Logic.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import by.story_weaver.ridereserve.Logic.data.models.Route;
import by.story_weaver.ridereserve.R;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.RouteViewHolder> {
    private List<Route> routes;
    private final OnRouteClickListener listener;

    public RoutesAdapter(List<Route> routes, OnRouteClickListener listener) {
        this.routes = routes;
        this.listener = listener;
    }

    public void updateRoutes(List<Route> newRoutes) {
        this.routes = newRoutes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        holder.bind(routes.get(position));
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRouteName, tvRoutePoints;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
            tvRoutePoints = itemView.findViewById(R.id.tvRoutePoints);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onRouteClick(routes.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Route route) {
            tvRouteName.setText(route.getName());
            tvRoutePoints.setText(route.getOrigin() + " → " + route.getDestination());
        }
    }

    public interface OnRouteClickListener {
        void onRouteClick(Route route);
    }
}