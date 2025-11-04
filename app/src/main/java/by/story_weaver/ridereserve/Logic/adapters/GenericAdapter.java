package by.story_weaver.ridereserve.Logic.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import by.story_weaver.ridereserve.Logic.data.models.*;
import by.story_weaver.ridereserve.Logic.data.enums.UserRole;
import by.story_weaver.ridereserve.R;

public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.Holder> {

    public enum Type { USER, ROUTE, TRIP, VEHICLE, BOOKING }

    public interface ItemListener {
        void onEditObject(Object item);
        void onDeleteObject(Object item);
        void onPromoteToDriver(Object item);
        void onDemoteToPassenger(Object item);
    }

    private Type type = Type.USER;
    private final ItemListener listener;

    private List<User> users = new ArrayList<>();
    private List<User> usersOrig = new ArrayList<>();

    private List<Route> routes = new ArrayList<>();
    private List<Route> routesOrig = new ArrayList<>();

    private List<Trip> trips = new ArrayList<>();
    private List<Trip> tripsOrig = new ArrayList<>();

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Vehicle> vehiclesOrig = new ArrayList<>();

    private List<Booking> bookings = new ArrayList<>();
    private List<Booking> bookingsOrig = new ArrayList<>();

    public GenericAdapter(ItemListener listener) {
        this.listener = listener;
    }

    public void setType(Type t) {
        this.type = t;
        notifyDataSetChanged();
    }

    public void setUsers(List<User> items) {
        // Фильтруем пользователей: не показываем админов
        List<User> filteredUsers = new ArrayList<>();
        if (items != null) {
            for (User user : items) {
                if (user.getRole() != UserRole.ADMIN) {
                    filteredUsers.add(user);
                }
            }
        }
        usersOrig = filteredUsers;
        users = new ArrayList<>(usersOrig);
        notifyDataSetChanged();
    }

    public void setRoutes(List<Route> items) {
        routesOrig = items != null ? new ArrayList<>(items) : new ArrayList<>();
        routes = new ArrayList<>(routesOrig);
        notifyDataSetChanged();
    }

    public void setTrips(List<Trip> items) {
        tripsOrig = items != null ? new ArrayList<>(items) : new ArrayList<>();
        trips = new ArrayList<>(tripsOrig);
        notifyDataSetChanged();
    }

    public void setVehicles(List<Vehicle> items) {
        vehiclesOrig = items != null ? new ArrayList<>(items) : new ArrayList<>();
        vehicles = new ArrayList<>(vehiclesOrig);
        notifyDataSetChanged();
    }

    public void setBookings(List<Booking> items) {
        bookingsOrig = items != null ? new ArrayList<>(items) : new ArrayList<>();
        bookings = new ArrayList<>(bookingsOrig);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        if (q.isEmpty()) {
            users = new ArrayList<>(usersOrig);
            routes = new ArrayList<>(routesOrig);
            trips = new ArrayList<>(tripsOrig);
            vehicles = new ArrayList<>(vehiclesOrig);
            bookings = new ArrayList<>(bookingsOrig);
            notifyDataSetChanged();
            return;
        }

        switch (type) {
            case USER:
                List<User> fu = new ArrayList<>();
                for (User u : usersOrig) {
                    if (u == null) continue;
                    if ((u.getFullName() != null && u.getFullName().toLowerCase().contains(q))
                            || (u.getEmail() != null && u.getEmail().toLowerCase().contains(q))
                            || (u.getPhone() != null && u.getPhone().toLowerCase().contains(q))
                            || String.valueOf(u.getId()).contains(q)) {
                        fu.add(u);
                    }
                }
                users = fu;
                break;
            case ROUTE:
                List<Route> fr = new ArrayList<>();
                for (Route r : routesOrig) {
                    if (r == null) continue;
                    if ((r.getName() != null && r.getName().toLowerCase().contains(q))
                            || (r.getOrigin() != null && r.getOrigin().toLowerCase().contains(q))
                            || (r.getDestination() != null && r.getDestination().toLowerCase().contains(q))
                            || String.valueOf(r.getId()).contains(q)) {
                        fr.add(r);
                    }
                }
                routes = fr;
                break;
            case TRIP:
                List<Trip> ft = new ArrayList<>();
                for (Trip t : tripsOrig) {
                    if (t == null) continue;
                    if ((t.getDepartureTime() != null && t.getDepartureTime().toLowerCase().contains(q))
                            || (t.getArrivalTime() != null && t.getArrivalTime().toLowerCase().contains(q))
                            || String.valueOf(t.getId()).contains(q)
                            || String.valueOf(t.getPrice()).contains(q)) {
                        ft.add(t);
                    }
                }
                trips = ft;
                break;
            case VEHICLE:
                List<Vehicle> fv = new ArrayList<>();
                for (Vehicle v : vehiclesOrig) {
                    if (v == null) continue;
                    if ((v.getModel() != null && v.getModel().toLowerCase().contains(q))
                            || (v.getPlateNumber() != null && v.getPlateNumber().toLowerCase().contains(q))
                            || String.valueOf(v.getId()).contains(q)) {
                        fv.add(v);
                    }
                }
                vehicles = fv;
                break;
            case BOOKING:
                // пока оставляем пустым
                break;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_generic, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        switch (type) {
            case USER: return users.size();
            case ROUTE: return routes.size();
            case TRIP: return trips.size();
            case VEHICLE: return vehicles.size();
            case BOOKING: return bookings.size();
            default: return 0;
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSub;
        ImageButton btnMore;

        Holder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSub = itemView.findViewById(R.id.tvSub);
            btnMore = itemView.findViewById(R.id.btnMore);
        }

        void bind(int pos) {
            final Object obj;
            switch (type) {
                case USER:
                    User u = users.get(pos);
                    tvTitle.setText(u.getFullName() != null ? u.getFullName() : "#" + u.getId());
                    // Добавляем отображение роли пользователя
                    String roleText = u.getEmail() != null ? u.getEmail() : "";
                    if (u.getRole() != null) {
                        roleText += " (" + getRoleDisplayName(u.getRole()) + ")";
                    }
                    tvSub.setText(roleText);
                    obj = u;
                    break;
                case ROUTE:
                    Route r = routes.get(pos);
                    tvTitle.setText(r.getName() != null ? r.getName() : "#" + r.getId());
                    tvSub.setText((r.getOrigin() != null ? r.getOrigin() : "") + " → " + (r.getDestination() != null ? r.getDestination() : ""));
                    obj = r;
                    break;
                case TRIP:
                    Trip t = trips.get(pos);
                    tvTitle.setText("Trip #" + t.getId());
                    tvSub.setText("Dep: " + (t.getDepartureTime() != null ? t.getDepartureTime() : "") +
                            " · Arr: " + (t.getArrivalTime() != null ? t.getArrivalTime() : ""));
                    obj = t;
                    break;
                case VEHICLE:
                    Vehicle v = vehicles.get(pos);
                    tvTitle.setText(v.getModel() != null ? v.getModel() : "#" + v.getId());
                    tvSub.setText(v.getPlateNumber() != null ? v.getPlateNumber() : "");
                    obj = v;
                    break;
                case BOOKING:
                    obj = bookings.get(pos); // пусто пока
                    tvTitle.setText("Booking #" + pos);
                    tvSub.setText("");
                    break;
                default:
                    obj = null;
                    tvTitle.setText("");
                    tvSub.setText("");
                    break;
            }

            final Object finalObj = obj;
            btnMore.setOnClickListener(view -> {
                PopupMenu pm = new PopupMenu(view.getContext(), view);
                pm.getMenu().add("Редактировать");
                pm.getMenu().add("Удалить");

                // Для пользователей добавляем соответствующий пункт меню в зависимости от роли
                if (type == Type.USER && finalObj instanceof User) {
                    User user = (User) finalObj;
                    if (user.getRole() == UserRole.DRIVER) {
                        pm.getMenu().add("Сделать пассажиром");
                    } else if (user.getRole() == UserRole.PASSENGER) {
                        pm.getMenu().add("Сделать водителем");
                    }
                    // Админов не показываем, так как они отфильтрованы в setUsers
                }

                pm.setOnMenuItemClickListener((MenuItem item) -> {
                    String title = item.getTitle().toString();
                    if (title.equals("Редактировать")) {
                        listener.onEditObject(finalObj);
                    } else if (title.equals("Удалить")) {
                        listener.onDeleteObject(finalObj);
                    } else if (title.equals("Сделать водителем")) {
                        listener.onPromoteToDriver(finalObj);
                    } else if (title.equals("Сделать пассажиром")) {
                        listener.onDemoteToPassenger(finalObj);
                    }
                    return true;
                });
                pm.show();
            });
        }
    }

    private String getRoleDisplayName(UserRole role) {
        if (role == null) return "Неизвестно";
        switch (role) {
            case ADMIN: return "Администратор";
            case DRIVER: return "Водитель";
            case PASSENGER: return "Пассажир";
            default: return "Неизвестно";
        }
    }
}