package net.joedoe;

@SuppressWarnings("unused")
public class Weather {
    private Wind wind;
    private int visibility;

    @Override
    public String toString() {
        return "Weather{" +
                wind +
                ", visibility=" + visibility + " meter" +
                '}';
    }

    static class Wind {
        float speed;
        int deg;

        @Override
        public String toString() {
            return "wind{" +
                    "speed=" + speed + " meter/sec" +
                    ", deg=" + deg + "°" +
                    '}';
        }
    }
}
