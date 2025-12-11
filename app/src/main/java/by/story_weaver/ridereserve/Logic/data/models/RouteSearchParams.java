package by.story_weaver.ridereserve.Logic.data.models;

public class RouteSearchParams {
    public String query;
    public String fromCity;
    public String toCity;
    public Double minPrice;
    public Double maxPrice;
    public Long date;

    public RouteSearchParams(String query, String fromCity, String toCity,
                             Double minPrice, Double maxPrice, Long date) {
        this.query = query;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.date = date;
    }

    public static RouteSearchParams simple(String query) {
        return new RouteSearchParams(query, null, null, null, null, null);
    }
}
