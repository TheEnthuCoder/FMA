package com.gsysk.mapUtils;

/**
 * Created by lenovo on 04-03-2015.
 */
public class RouteMarker
{
    public String name;
    public int routeNum;
    public int sequenceNum;
    public float latitude;
    public float longitude;

    public RouteMarker(String [] parts)
    {
        this.name = parts[0];
        this.routeNum = Integer.parseInt(parts[1]);
        this.sequenceNum = Integer.parseInt(parts[2]);
        this.latitude = Float.parseFloat(parts[3]);
        this.longitude = Float.parseFloat(parts[4]);

    }
}
