package jp.ac.u_tokyo.constellationmatching;

/**
 * Created by luning on 2017/07/09.
 */

public class Star {
    String name;
    double azimuth;
    double altitude;
    String constellation;


    public Star(String name, String constellation, double azimuth, double altitude) {
        this.name = name;
        this.constellation = constellation;
        this.azimuth = azimuth;
        this.altitude = altitude;
    }

    public Star(String name, double azimuth, double altitude) {
        this.name = name;
        this.azimuth = azimuth;
        this.altitude = altitude;
    }

    public Star(String name, String constellation) {
        this.name = name;
        this.constellation = constellation;
    }

    @Override
    public String toString() {
        return String.format("%s: azimuth %f, altitude %f", name, azimuth, altitude);
    }

    public String getName() {
        return name;
    }

    public String getConstellation() {
        return constellation;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public double getAltitude() {
        return altitude;
    }


}
