package com.coming.customer.ui.map;

import android.net.Uri;
import android.util.Log;

import com.huawei.hms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Ronak Doshi on 30/5/17.
 */

public class StaticMap {

    public static final String MARKER_URL = "http://132.148.17.145/~hlinkserver/karwa/assets/uploads/marker/red_pin.png";
    private static final String BASE_URL_SCHEME = "https";
    private static final String BASE_URL_AUTHORITY = "maps.googleapis.com";
    private static final String BASE_URL_PATH_1 = "maps";
    private static final String BASE_URL_PATH_2 = "api";
    private static final String BASE_URL_PATH_3 = "staticmap";
    private Uri.Builder builder;
    private String finalUrl;

    public StaticMap() {
        builder = new Uri.Builder();
        builder.scheme(BASE_URL_SCHEME)
                .authority(BASE_URL_AUTHORITY)
                .appendPath(BASE_URL_PATH_1)
                .appendPath(BASE_URL_PATH_2)
                .appendPath(BASE_URL_PATH_3);
    }

    public StaticMap center(LatLng latLng) {
        builder.appendQueryParameter("center", latLng.latitude + "," + latLng.longitude);
        return this;
    }

    public StaticMap center(String address) {
        builder.appendQueryParameter("center", address);
        return this;
    }

    public StaticMap zoom(int zoom) {
        builder.appendQueryParameter("zoom", String.valueOf(zoom));
        return this;
    }

    public StaticMap size(int height, int width) {
        builder.appendQueryParameter("size", height + "x" + width);
        return this;
    }

    public StaticMap mapType(MapType maptype) {
        builder.appendQueryParameter("maptype", maptype.getValue());
        return this;
    }

    public StaticMap marker(MarkerStyle marker) {
        builder.appendQueryParameter("markers", marker.build());
        return this;
    }

    public StaticMap key(String key) {
        builder.appendQueryParameter("key", key);
        return this;
    }

    public StaticMap scale(int scale) {
        builder.appendQueryParameter("scale", String.valueOf(scale));
        return this;
    }

    public StaticMap format(ImageFormat format) {
        builder.appendQueryParameter("format", format.getValue());
        return this;
    }

    public StaticMap language(String language) {
        builder.appendQueryParameter("language", language);
        return this;
    }

    public StaticMap region(String region) {
        builder.appendQueryParameter("region", region);
        return this;
    }

    public StaticMap path(PathStyle path) {
        builder.appendQueryParameter("path", path.build());
        return this;
    }

    public StaticMap visible(String visible) {
        builder.appendQueryParameter("visible", visible);
        return this;
    }

    public StaticMap style(String style) {
        builder.appendQueryParameter("style", style);
        return this;
    }

    public String create() throws IllegalArgumentException {
        finalUrl = builder.build().toString();
        Log.d("Static Map URL", finalUrl);

        if (!finalUrl.contains("size"))
            throw new IllegalArgumentException("Enter size of the image!");

        if (!finalUrl.contains("center"))
            throw new IllegalArgumentException("Enter location to display!");

        return finalUrl;
    }

    public enum MapType {
        ROADMAP("roadmap"),
        SATELLITE("satellite"),
        TERRAIN("terrain"),
        HYBRID("hybrid");

        protected String sValue;

        private MapType(String sValue) {
            this.sValue = sValue;
        }

        protected String getValue() {
            return sValue;
        }
    }

    public enum ImageFormat {
        PNG("png"),
        PNG8("png8"),
        PNG32("png32"),
        GIF("gif"),
        JPG("jpg"),
        JPG_BASELINE("jpg-baseline");

        protected String sValue;

        private ImageFormat(String sValue) {
            this.sValue = sValue;
        }

        protected String getValue() {
            return sValue;
        }
    }

    public static class MarkerStyle {

        private static StringBuilder stringBuilder;

        public MarkerStyle() {
            stringBuilder = new StringBuilder();
        }

        public MarkerStyle icon(String url) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("icon:");
            stringBuilder.append(url);
            return this;
        }

        public MarkerStyle size(MarkerSize markerSize) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("size:");
            stringBuilder.append(markerSize.getValue());
            return this;
        }

        public MarkerStyle color(MarkerColor markerColor) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("color:");
            stringBuilder.append(markerColor.getValue());
            return this;
        }

        public MarkerStyle color(String color) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("color:");
            stringBuilder.append(color.replace("#", "0x"));
            return this;
        }

        public MarkerStyle anchor(int x, int y) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("anchor:");
            stringBuilder.append(x).append(",").append(y);
            return this;
        }

        public MarkerStyle anchor(MarkerAnchor markerAnchor) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("anchor:");
            stringBuilder.append(markerAnchor.getValue());
            return this;
        }

        public MarkerStyle location(LatLng... latLngs) {
            for (LatLng latLng : latLngs) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("|");
                }
                stringBuilder.append(latLng.latitude).append(",").append(latLng.longitude);
            }
            return this;
        }

        public MarkerStyle location(List<LatLng> latLngs) {
            for (LatLng latLng : latLngs) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("|");
                }
                stringBuilder.append(latLng.latitude).append(",").append(latLng.longitude);
            }
            return this;
        }

        public String build() {
            return stringBuilder.toString();
        }

        public enum MarkerSize {
            TINY("tiny"),
            MID("mid"),
            SMALL("small");

            protected String sValue;

            private MarkerSize(String sValue) {
                this.sValue = sValue;
            }

            protected String getValue() {
                return sValue;
            }
        }

        public enum MarkerColor {
            BLACK("black"),
            BROWN("brown"),
            GREEN("green"),
            PURPLE("purple"),
            YELLOW("yellow"),
            BLUE("blue"),
            GRAY("gray"),
            ORANGE("orange"),
            RED("red"),
            WHITE("white");

            protected String sValue;

            private MarkerColor(String sValue) {
                this.sValue = sValue;
            }

            protected String getValue() {
                return sValue;
            }
        }

        public enum MarkerAnchor {
            TOP("top"),
            BOTTOM("bottom"),
            LEFT("left"),
            RIGHT("right"),
            CENTER("center"),
            TOPLEFT("topleft"),
            TOPRIGHT("topright"),
            BOTTOMLEFT("bottomleft"),
            BOTTOMRIGHT("bottomright");

            protected String sValue;

            private MarkerAnchor(String sValue) {
                this.sValue = sValue;
            }

            protected String getValue() {
                return sValue;
            }
        }
    }

    public static class PathStyle {

        private static StringBuilder stringBuilder;

        public PathStyle() {
            stringBuilder = new StringBuilder();
        }

        public PathStyle weight(int weight) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("weight:");
            stringBuilder.append(weight);
            return this;
        }

        public PathStyle color(PathColor pathColor) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("color:");
            stringBuilder.append(pathColor.getValue());
            return this;
        }

        public PathStyle color(String color) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("color:");
            stringBuilder.append(color.replace("#", "0x"));
            return this;
        }

        public PathStyle fillColor(PathColor pathColor) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("fillcolor:");
            stringBuilder.append(pathColor.getValue());
            return this;
        }

        public PathStyle fillColor(String color) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("fillcolor:");
            stringBuilder.append(color.replace("#", "0x"));
            return this;
        }

        public PathStyle geodesic(boolean isGeodesic) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("geodesic:");
            stringBuilder.append(isGeodesic ? "true" : "false");
            return this;
        }

        public PathStyle location(LatLng... latLngs) {
            for (LatLng latLng : latLngs) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("|");
                }
                stringBuilder.append(latLng.latitude).append(",").append(latLng.longitude);
            }
            return this;
        }

        public PathStyle location(List<LatLng> latLngs) {
            for (LatLng latLng : latLngs) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("|");
                }
                stringBuilder.append(latLng.latitude).append(",").append(latLng.longitude);
            }
            return this;
        }

        public PathStyle location(String encodedPolyline) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("enc:");
            stringBuilder.append(encodedPolyline);
            return this;
        }

        public String build() {
            return stringBuilder.toString();
        }

        public enum PathColor {
            BLACK("black"),
            BROWN("brown"),
            GREEN("green"),
            PURPLE("purple"),
            YELLOW("yellow"),
            BLUE("blue"),
            GRAY("gray"),
            ORANGE("orange"),
            RED("red"),
            WHITE("white");

            protected String sValue;

            private PathColor(String sValue) {
                this.sValue = sValue;
            }

            protected String getValue() {
                return sValue;
            }
        }
    }

    public static class MapStyle {

        private static StringBuilder stringBuilder;

        public MapStyle() {
            stringBuilder = new StringBuilder();
        }

        public MapStyle feature(Feature feature) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("feature:");
            stringBuilder.append(feature.getValue());
            return this;
        }

        public MapStyle element(Element element) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("element:");
            stringBuilder.append(element.getValue());
            return this;
        }

        public MapStyle visibility(Visibility visibility) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("visibility:");
            stringBuilder.append(visibility);
            return this;
        }

        public MapStyle color(Color color) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("color:");
            stringBuilder.append(color.getValue());
            return this;
        }

        public MapStyle color(String color) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("color:");
            stringBuilder.append(color.replace("#", "0x"));
            return this;
        }

        public MapStyle hue(String hue) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("hue:");
            stringBuilder.append(hue.replace("#", "0x"));
            return this;
        }

        public MapStyle lightness(float lightness) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("lightness:");
            stringBuilder.append(lightness);
            return this;
        }

        public MapStyle saturation(float saturation) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("saturation:");
            stringBuilder.append(saturation);
            return this;
        }

        public MapStyle gamma(float gamma) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("gamma:");
            stringBuilder.append(gamma);
            return this;
        }

        public MapStyle invertLightness(boolean invertLightness) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("invert_lightness:");
            stringBuilder.append(invertLightness ? "true" : "false");
            return this;
        }

        public MapStyle weight(int weight) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("|");
            }
            stringBuilder.append("weight:");
            stringBuilder.append(weight);
            return this;
        }

        public String build() {
            return stringBuilder.toString();
        }

        public enum Feature {
            ALL("all"),
            ADMINISTRATIVE("administrative"),
            ADMINISTRATIVE_COUNTRY("administrative.country"),
            ADMINISTRATIVE_LAND_PARCEL("administrative.land_parcel"),
            ADMINISTRATIVE_LOCALITY("administrative.locality"),
            ADMINISTRATIVEWATER_NEIGHBORHOOD("administrative.neighborhood"),
            ADMINISTRATIVE_PROVINCE("administrative.province"),
            LANDSCAPE("landscape"),
            LANDSCAPE_MAN_MADE("landscape.man_made"),
            LANDSCAPE_NATURAL("landscape.natural"),
            LANDSCAPE_LANDCOVER("landscape.natural.landcover"),
            LANDSCAPE_TERRAIN("landscape.natural.terrain"),
            POI("poi"),
            POI_ATTRACTION("poi.attraction"),
            POI_BUSINESS("poi.business"),
            POI_GOVERNMENT("poi.government"),
            POI_MEDICAL("poi.medical"),
            POI_PARK("poi.park"),
            POI_PLACE_OF_WORSHIP("poi.place_of_worship"),
            POI_SCHOOL("poi.school"),
            POI_SPORTS_COMPLEX("poi.sports_complex"),
            ROAD("road"),
            ROAD_ARTERIAL("road.arterial"),
            ROAD_HIGHWAY("road.highway"),
            ROAD_HIGHWAY_CONTROLLED_ACCESS("road.highway.controlled_access"),
            ROAD_LOCAL("road.local"),
            TRANSIT("transit"),
            TRANSIT_LINE("transit.line"),
            TRANSIT_STATION("transit.station"),
            TRANSIT_STATION_AIRPORT("transit.station.airport"),
            TRANSIT_STATION_BUS("transit.station.bus"),
            TRANSIT_STATION_RAIL("transit.station.rail"),
            WATER("water");

            protected String sValue;

            private Feature(String sValue) {
                this.sValue = sValue;
            }

            protected String getValue() {
                return sValue;
            }
        }

        public enum Element {
            ALL("all"),
            GEOMETRY("geometry"),
            GEOMETRY_FILL("geometry.fill"),
            GEOMETRY_STROKE("geometry.stroke"),
            LABELS("labels"),
            LABELS_ICON("labels.icon"),
            LABELS_TEXT("labels.text"),
            LABELS_TEXT_FILL("labels.text.fill"),
            LABELS_TEXT_STROKE("labels.text.stroke");

            protected String sValue;

            private Element(String sValue) {
                this.sValue = sValue;
            }

            protected String getValue() {
                return sValue;
            }
        }

        public enum Visibility {
            ON("on"),
            OFF("off"),
            SIMPLIFIED("simplified");

            protected String sValue;

            private Visibility(String sValue) {
                this.sValue = sValue;
            }

            protected String getValue() {
                return sValue;
            }
        }

        public enum Color {
            BLACK("black"),
            BROWN("brown"),
            GREEN("green"),
            PURPLE("purple"),
            YELLOW("yellow"),
            BLUE("blue"),
            GRAY("gray"),
            ORANGE("orange"),
            RED("red"),
            WHITE("white");

            protected String sValue;

            private Color(String sValue) {
                this.sValue = sValue;
            }

            protected String getValue() {
                return sValue;
            }
        }
    }
}
