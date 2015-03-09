package com.gsysk.mapUtils;

/**
 * Created by lenovo on 04-03-2015.
 */
public class RouteFormulator {

    private RouteMarker source;
    private RouteMarker destination;
    private RouteMarker [][]dropPoints;
    private int numRoutes = 1;


    public RouteFormulator(String routeDetails)
    {
     //   System.out.println(routeDetails);
        String [] parts = routeDetails.split(" , ");

        //Source Details
        String []sourceParts = parts[0].split(" / ");
        source = new RouteMarker(sourceParts);
        //display(source);
        //Drop Points

        dropPoints = new RouteMarker[parts.length-2][];
        numRoutes = parts.length-2;
        for(int i=0;i<parts.length-2;i++)
        {
            String [] dropPointParts = parts[i+1].split(" ; ");
            dropPoints[i] = new RouteMarker[dropPointParts.length];

            for(int j=0;j<dropPointParts.length;j++)
            {
                String [] dropPointSubParts = dropPointParts[j].split(" / ");
                dropPoints[i][j]  = new RouteMarker(dropPointSubParts);
              //  display(dropPoints[i][j]);
            }
        }



        //Destination Details
        String []dstParts = parts[parts.length-1].split(" / ");
        destination = new RouteMarker(dstParts);
       // display(destination);



    }

    public int getNumberOfRoutes()
    {
        return(numRoutes);
    }

    public RouteMarker getSource()
    {
        return source;
    }

    public RouteMarker getDestination()
    {
        return destination;
    }

    public RouteMarker[][]  getDropPoints()
    {
        return dropPoints;
    }

    private void display(RouteMarker r)
    {
        System.out.println("-----------------------------------");
        System.out.println("Name : "+r.name);
        System.out.println("RouteNum : "+r.routeNum);
        System.out.println("SequenceNum : "+r.sequenceNum);
        System.out.println("Latitude : "+r.latitude);
        System.out.println("Longitude : "+r.longitude);
    }
}
