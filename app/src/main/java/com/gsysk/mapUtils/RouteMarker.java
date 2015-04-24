package com.gsysk.mapUtils;

/**
 * Created by lenovo on 04-03-2015.
 */
public class RouteMarker implements Comparable<RouteMarker>
{
    public String name;
    public int routeNum;
    public int sequenceNum;
    public float latitude;
    public float longitude;
    public int dropPointID;
    public String status;

    public RouteMarker(String [] parts)
    {
        try
        {
            this.name = parts[0];
            this.routeNum = Integer.parseInt(parts[1]);
            this.sequenceNum = Integer.parseInt(parts[2]);
            this.latitude = Float.parseFloat(parts[3]);
            this.longitude = Float.parseFloat(parts[4]);
            this.dropPointID = Integer.parseInt(parts[5]);
            this.status = parts[6];
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }

    }

    @Override
    public int compareTo(RouteMarker o) {
    return Integer.compare(sequenceNum, o.sequenceNum);
}
}
