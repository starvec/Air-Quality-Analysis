package net.starvec;

public class LocationTools 
{
	final private static double EARTH_RADIUS = 6371.0088;
	
	public static float getDistFromCoords(float latA, float lonA, float latB, float lonB)
    {
        double deltaLat = latB * Math.PI / 180.0 - latA * Math.PI / 180.0;
        double deltaLon = lonB * Math.PI / 180.0 - lonA * Math.PI / 180.0;
        // a, b, and c hold no significance by themselves
        // they just serve to break up a much larger equation into smaller pieces
        double a = Math.sin(deltaLat/2.0) * Math.sin(deltaLat/2.0);
        double b = Math.cos(latA * Math.PI / 180.0) * Math.cos(latB * Math.PI / 180.0) *
                Math.sin(deltaLon/2.0) * Math.sin(deltaLon/2.0);
        double c = 2 * Math.atan2(Math.sqrt(a+b), Math.sqrt(1-(a+b)));
        return (float) (EARTH_RADIUS * c * 1000);
    }
}
