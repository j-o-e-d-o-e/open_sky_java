/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package net.joedoe;

import org.opensky.api.OpenSkyApi;
import org.opensky.api.OpenSkyApi.BoundingBox;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class App {
    private OpenSkyApi api = new OpenSkyApi();
    private OpenSkyStates states = null;
    private BoundingBox box;
    private Logger logger = Logger.getLogger(App.class.getName());
    private int minHeading, maxHeading, maxAltitude;

    @SuppressWarnings("WeakerAccess")
    public void getPlanes() {
        loadProps();
        try {
            states = api.getStates(0, null, box);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (states == null) return;
        List<StateVector> planes = (List<StateVector>) states.getStates();
        if (planes == null || planes.size() == 0) {
            logger.info("No planes found.");
            return;
        }
        logger.info("Number of all planes: " + planes.size());
        planes = planes.stream().filter(this::include).collect(Collectors.toList());
        if (planes.size() == 0) {
            logger.info("No planes met requirements.");
            return;
        }
        logger.info("Number of valid planes: " + planes.size());
        for (StateVector plane : planes) {
            long timestamp = System.currentTimeMillis();
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            String message = "Date: " + date + " / " +
                    "Icao 24: " + plane.getIcao24() + " / " +
                    "Altitude: " + Math.round(plane.getGeoAltitude()) + " m" + " / " +
                    "Velocity: " + Math.round(plane.getVelocity() * 60 * 60 / 1000) + " km/h" + " / " +
                    "Heading: " + plane.getHeading() + "°";
            logger.info(message);
        }
    }

    private boolean include(StateVector plane) {
        return plane.getHeading() > minHeading && plane.getHeading() < maxHeading && plane.getGeoAltitude() < maxAltitude;
    }

    private void loadProps() {
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/application.properties"));
        } catch (IOException | NullPointerException e) {
            if (e instanceof NullPointerException) {
                logger.info("Change path of properties-file in App.java to: /application-default.properties");
                System.exit(1);
            }
            e.printStackTrace();
        }
        float minLatitude = Float.parseFloat(props.getProperty("minLatitude"));
        float maxLatitude = Float.parseFloat(props.getProperty("maxLatitude"));
        float minLongitude = Float.parseFloat(props.getProperty("minLongitude"));
        float maxLongitude = Float.parseFloat(props.getProperty("maxLongitude"));
        box = new BoundingBox(minLatitude, maxLatitude, minLongitude, maxLongitude);

        minHeading = Integer.parseInt(props.getProperty("minHeading"));
        maxHeading = Integer.parseInt(props.getProperty("maxHeading"));
        maxAltitude = Integer.parseInt(props.getProperty("maxGeoAltitude"));
    }

    public static void main(String[] args) {
        new App().getPlanes();
    }
}
