// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
   
Parse.Cloud.define("getAdminID", function(request, response) {
  var query = new Parse.Query("Admin");
  query.find({
    success: function(results) {
        var adminId=-1;
        for(i=0;i<results.length;i++)
        {
            if(results[i].get("Name") == request.params.Name)
            {
                adminId = results[i].get("adm_id");
                break;
            }
        }
                
      response.success(adminId);
    },
    error: function() {
      response.error("Error retrieving ID");
    }
  });
});
          
          
Parse.Cloud.define("getAllDetails", function(request, response) {
  var query = new Parse.Query("Driver");
            
  query.find({
    success: function(results) {
    var driverResults = results;
    var adminName = request.params.Name;
    Parse.Cloud.run("getAdminID",{Name:adminName},{
                    success: function(results) {
       
                    var details = "";
                    var concatResponse = "";
                    var route_ids = [];
                    var index = 0;
                    var adminIDval = parseInt(results);
                    for (var i = 0; i < driverResults.length; ++i) {
                   
                        if(driverResults[i].get("admin_ids").indexOf(adminIDval) != -1)
                        {
                                details = details.concat(driverResults[i].get("Name")+" : "+driverResults[i].get("PhoneNumber")+" : "+driverResults[i].get("route_id")+" ; ");
                                route_ids[index++] = parseInt(driverResults[i].get("route_id"));
                        }
                           
                    }
                    Parse.Cloud.run("getVehiclesForCluster",{RouteIds:route_ids},{
                                    success: function(results) {
       
                                    concatResponse = results;
                                    Parse.Cloud.run("getContactDetails",{adminID:adminIDval},{
                                        success: function(results) {
                                            concatResponse = concatResponse.concat(" | " + results);
           
                                            Parse.Cloud.run("getAdminCluster",{Name:adminName},{
                                                success: function(results) {
               
                                                Parse.Cloud.run("getClusterParameters",{ClusterNumber:results.toString()},{
                                                    success: function(results) {
                                                    console.log("Input obtained : "+results.toString());
                                                    var clusters = results.toString().split(" # ");
                                                    var sources = [];
                                                    var routeNumArrays = [];
                                                    for(var x = 0; x<clusters.length-1;x++)
                                                    {
                                                        sources[x] = clusters[x].split(" : ")[0];
                                                        routeNumArrays[x] = clusters[x].split(" : ")[1];
                                                    }
                                                    console.log("Source obtained : "+sources[0]);
                                                    console.log("Route array obtained : "+routeNumArrays[0]);
                   
                                                    concatResponse = concatResponse.concat(" | " + (clusters.length-1));
                                                    Parse.Cloud.run("getAllDropPoints",{numClusters:(clusters.length-1),Source:sources,RouteNumArray:routeNumArrays},{
                                                        success: function(results) {
       
                                                        concatResponse = concatResponse.concat(" | " + results);
                                                        response.success(details + " | " + concatResponse);
   
                                                        },
                                                        error: function() {
                                                        response.error("Error retrieving details in getAllDroppoints");
                                                        }
                       
                                                    });
                   
   
                                                    },
                                                    error: function() {
                                                    response.error("Error retrieving details in getClusterParameters");
                                                    }
                   
                                                });
   
                                                },
                                                error: function() {
                                                response.error("Error retrieving details in getAdminCluster");
                                                }
               
                                            });
   
         
         
         
   
                                        },
                                        error: function() {
                                        response.error("Error retrieving details in contact");
                                        }
     
                                        });
   
                                    },
                                    error: function() {
                                    response.error("Error retrieving details in getVehiclesForCluster");
                                    }
                       
                                });
                       
   
         
                    },
                    error: function() {
                    response.error("Error retrieving details");
                    }
                });
   
                    },
     error: function() {
    response.error("Error getting admin ID");
    }
                       
    });
         
});
          
Parse.Cloud.define("getContactDetails", function(request, response) {
  var query = new Parse.Query("user");
            
  query.find({
    success: function(results) {
      var details = "";
      for (var i = 0; i < results.length; ++i) {
           if(results[i].get("adm_id").indexOf(request.params.adminID) != -1)
                details = details.concat(results[i].get("Name")+" : "+results[i].get("PhoneNumber")+" : "+results[i].get("drppnt_id")+" ; ");
      }
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving user details");
    }
  });
});
          
          
Parse.Cloud.define("getNumRoutes", function(request, response) {
  var query = new Parse.Query("Routes");
  query.find({
    success: function(results) {
                
      response.success(results.length+1);
    },
    error: function() {
      response.error("Error retrieving user details");
    }
  });
});
   
   
Parse.Cloud.define("getDropPointsInRoutes", function(request, response) {
  var query = new Parse.Query("Routes");
  query.find({
    success: function(results) {
             var details = "";
             var routeNumbers = request.params.RouteNumArray.split(",");
             for(var j = 0; j < routeNumbers.length; j++)
             {
                 for(var i = 0; i < results.length; i++)
                {
                    if(results[i].get("route_id") == routeNumbers[j])
                    {
                        details = details.concat(results[i].get("DropPoints"));
                        break;
                    }
                }
                if(j == routeNumbers.length - 1)
                    break;
                details = details.concat("*");
             }
               
                
             response.success(request.params.curSource+":"+details);
         
    },
    error: function() {
      response.error("Error retrieving user details in inner 2");
    }
  });
});
    
 Parse.Cloud.define("getAllDropPoints", function(request, response) {
  var query = new Parse.Query("DropPoints");
      
     
  query.find({
    success: function(results) {
       
    var promises = [];
    var megaDetails = [];
    var sources = request.params.Source;
    var routeNumArray = request.params.RouteNumArray;
    var outerResults = [];
    outerResults =  results;
       
    for(var y = 0; y<request.params.numClusters;y++)
    {
           
        var j=0;
           
           
        console.log("In iteration : "+y);
           
                
               
          
                
            promises.push(Parse.Cloud.run("getDropPointsInRoutes",{curSource:sources[y],RouteNumArray:routeNumArray[y]},{
                    success: function(results) {
   
                       
                       
                   
                       
                       
   
                    },
                    error: function(results, error) {
                    response.error("Error retrieving user details inner");
                   
                   
                    }
                }));               
    }
         
        
    Parse.Promise.when(promises).then(function() {
        // all done
        for(var y = 0; y<arguments.length;y++)
        {
            var details = [];
                    megaDetails[y] = "";
                    for(var i =0;i<outerResults.length;i++)
                    {           
                        var curSource = arguments[y].toString().split(":")[0];
                        var curRoutes = arguments[y].toString().split(":")[1];
                        console.log("Source is : "+curSource);
                        if(outerResults[i].get("Name") == curSource)
                        {
                       
                            details[0] = outerResults[i].get("Name") +" / "+outerResults[i].get("Route") +" / "+outerResults[i].get("SequenceNum")+" / "+outerResults[i].get("Latitude")+ 
                            " / "+ outerResults[i].get("Longitude") + " / "+ outerResults[i].get("drpnt_id") +" ";
                            break;
                        }
                    }
                       
                    var dropPointsArrayGrp = curRoutes.split("*");
                           
                        for(j=0;j<dropPointsArrayGrp.length;j++)
                        {
                            details[j+1] =" ";
                            dropPointsArray = dropPointsArrayGrp[j].split(",");
                            for(var x = 0; x < dropPointsArray.length;x++)
                            {
                                for (var i = 0; i < outerResults.length; ++i) 
                                {
                                   
                                    if(outerResults[i].get("droppoint_id") == ("d"+dropPointsArray[x]))
                                    {
                                        details[j+1] = details[j+1].concat(outerResults[i].get("Name") +" / "+outerResults[i].get("Route") +" / "+outerResults[i].get("SequenceNum")+" / "+
                                        outerResults[i].get("Latitude")+ " / "+ outerResults[i].get("Longitude") + " / "+ outerResults[i].get("drpnt_id")+ " / "+ outerResults[i].get("Status")+ " ; ");
                                        break;
                                    }
                                }
                       
                       
                            details[j+1] = details[j+1].concat(" ");
                            }
                           
                       
                        }   
                       
                       
                    if(y!=arguments.length-1)
                    {
                        megaDetails[y] = details.toString()+".";        
                    }
                    else
                    {
                        megaDetails[y] = details.toString();        
                    }
                       
           
           
        }
   
        response.success(megaDetails.toString());
        }, function(err) {
        // error
        response.error("Error retrieving routeid details details");
        });  
        
        
    },
    error: function() {
      response.error("Error retrieving user details");
    }
  });
});
   
   
          
Parse.Cloud.define("verifyLogin", function(request, response) {
  var query = new Parse.Query("Login");
  query.equalTo("username", request.params.Name);
  query.find({
    success: function(results) {
      var username = request.params.Name;
      var password = request.params.Password;
     var found = false;
      var index = -1;
      if(results.length==0)
      {
            response.success("Unregistered username");
      }
      else
      {
            for(var i=0;i<results.length;i++)
            {
                if(results[i].get("password")==password)
                { 
                    found = true;
                    index = i;
                    break;
                              
                }
            }
               
            if(found)
            {
                found = false;
                response.success("Success:"+results[index].get("Role")+":"+results[index].get("loginid"));
            }
            else
            { 
                          
                response.success("Wrong password");
            }
      }
                   
    },
    error: function() {
      response.error("Could not verify with cloud data");
    }
  });
});
          
Parse.Cloud.define("getAdminCluster", function(request, response) {
  var query = new Parse.Query("Admin");
  query.find({
    success: function(results) {
        var clusterDetails="";
        for(i=0;i<results.length;i++)
        {
            if(results[i].get("Name") == request.params.Name)
            {
                clusterDetails = clusterDetails.concat(results[i].get("clusterAccess"));
                break;
            }
        }
                
      response.success(clusterDetails);
    },
    error: function() {
      response.error("Error retrieving user details");
    }
  });
});
          
Parse.Cloud.define("getClusterParameters", function(request, response) {
  var query = new Parse.Query("Cluster");
  query.find({
    success: function(results) {
        var clusterDetails="";
        var clusterNumbers = request.params.ClusterNumber.split(",");
                  
        for(var j=0;j<clusterNumbers.length;j++)
        {
            for(i=0;i<results.length;i++)
            {
                if(results[i].get("Number") == parseInt(clusterNumbers[j]))
                {
                    clusterDetails = clusterDetails.concat(results[i].get("Source")+" : "+results[i].get("Routes")+ " # ");
                    break;
                }
            }
        }
                  
                  
                
      response.success(clusterDetails);
    },
    error: function() {
      response.error("Error retrieving user details");
    }
  });
});
         
Parse.Cloud.define("getAdminDetailsToDriver", function(request, response) {
  var query = new Parse.Query("Admin");
  query.equalTo("clusterAccess",request.params.clusterAccess);
            
  query.find({
    success: function(results) {
      var details = "";
      for (var i = 0; i < results.length; ++i) {
        details = details.concat(results[i].get("Name")+" : "+results[i].get("contact_no")+" : "+results[i].get("clusterAccess")+" ; ");
      }
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving admin details");
    }
  });
});
         
         
Parse.Cloud.define("getRouteAssignedToVehicle", function(request, response) {
  var query = new Parse.Query("routesassigned");
  query.equalTo("driver_id",request.params.driver_id);
            
  query.find({
    success: function(results) {
      var details = "";
      for (var i = 0; i < results.length; ++i) {
        details = details.concat(results[i].get("driver_id")+" : "+results[i].get("vehicle_id")+" : "+results[i].get("route_id")+" ; ");
      }
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving routes_assigned_to_vehicle details");
    }
  });
});
        
Parse.Cloud.define("getDriverLocation", function(request, response) {
  var query = new Parse.Query("vehiclelocation");
  query.equalTo("vehicleid",request.params.vehicleid);
            
  query.find({
    success: function(results) {
             
              
      response.success(results[0].get("vehicleid")+" : "+results[0].get("latitude")+" : "+results[0].get("longitude"));
    },
    error: function() {
      response.error("Error retrieving driver details");
    }
  });
});
       
       
Parse.Cloud.define("getVehicleDetails", function(request, response) {
  var query = new Parse.Query("vehicle");
  query.equalTo("vehicle_id",request.params.vehicle_id);
            
  query.find({
    success: function(results) {
      var details = "";
      for (var i = 0; i < results.length; ++i) {
        details = details.concat(results[i].get("reg_no")+" : "+results[i].get("vehicle_id")+" : "+results[i].get("Capacity")+" : "+results[i].get(
"vehicle_name")+" ; ");
      }
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving vehicle details details");
    }
  });
});
    
    
Parse.Cloud.define("getdriveridforuser", function(request, response) {
  var query = new Parse.Query("user");
  query.equalTo("loginid",request.params.loginid);
            
  query.find({
    success: function(results) {
      var details = "";
      for (var i = 0; i < results.length; ++i) {
        details = details.concat(results[i].get("d_id")+" : "+results[i].get("adm_id")+" ; ");
      }
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving d_id details details");
    }
  });
});
    
    
Parse.Cloud.define("getdriverforuser", function(request, response) {
  var query = new Parse.Query("Driver");
  query.equalTo("driver_id",request.params.driver_id);
            
  query.find({
    success: function(results) {
      var details = "";
      for (var i = 0; i < results.length; ++i) {
        details = details.concat(results[i].get("Name")+" : "+results[i].get("PhoneNumber")+" ; ");
      }
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving driver details details");
    }
  });
});
    
    
    
    
    
Parse.Cloud.define("getadminstouser", function(request, response) {
  var query = new Parse.Query("Admin");
    
  var a_ids = request.params.adminIdArray;
  var adminIdArray = a_ids.split(",");
    
  query.find({
    success: function(results) {
      var details = "";
      var j=0;
      for (var i = 0; i < results.length; ++i) {
        for(j=0; j<adminIdArray.length; j++){
          if(adminIdArray[j]==(results[i].get("adm_id"))){
            details = details.concat(results[i].get("Name")+" : "+results[i].get("contact_no")+" ; ");
         }
        }
      }  
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving admin details");
    }
  });
});
    
    
Parse.Cloud.define("getadminstodriver", function(request, response) {
  var query = new Parse.Query("Admin");
     
  var a_ids = request.params.adminIdArray;
  var adminIdArray = a_ids.split(",");
     
  query.find({
    success: function(results) {
      var details = "";
      var j=0;
      for (var i = 0; i < results.length; ++i) {
        for(j=0; j<adminIdArray.length; j++){
          if(adminIdArray[j]==(results[i].get("adm_id"))){
            details = details.concat(results[i].get("Name")+" : "+results[i].get("contact_no")+" ; ");
         }
        }
      }  
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving admin details");
    }
  });
});
    
    
Parse.Cloud.define("getuserstodriver", function(request, response) {
  var query = new Parse.Query("user");
  var u_ids = request.params.uidsArray;
  var userIdArray = u_ids.split(",");
  query.find({
    success: function(results) {
      var details = "";
      var j=0;
      for (var i = 0; i < results.length; ++i) {
        for(j=0; j<userIdArray.length; j++){
          if(userIdArray[j]==(results[i].get("u_idno"))){
            details = details.concat(results[i].get("Name")+" : "+results[i].get("PhoneNumber")+" ; ");
         }
        }
      }  
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving user details");
    }
  });
});
    
    
    
Parse.Cloud.define("getdetailstoDriver", function(request, response) {
  var query = new Parse.Query("Driver");
  query.equalTo("loginid",request.params.loginid);
            
  query.find({
    success: function(results) {
      var details = "";
      for (var i = 0; i < results.length; ++i) {
        details = details.concat(results[i].get("users")+" : "+results[i].get("admin_ids")+" : "+results[i].get("r_droppoints")+" : "+results[i].get(
"route_id")+" : "+results[i].get("cluster_id")+" ; ");
      }
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving d_id details");
    }
  });
});
    
    
Parse.Cloud.define("getdriversRoute", function(request, response) {
  var query = new Parse.Query("DropPoints");
      
  var dp_ids = request.params.dpidsArray;
  var src = request.params.srcName;
  var dpIdArray = dp_ids.split(",");
  query.find({
    success: function(results) {
      var details = "";
      var temp = "";
      var j=0;
      for (var i = 0; i < results.length; ++i) {
        if(src==results[i].get("Name")){
                temp = temp.concat("#Success : ");
                temp = temp.concat(results[i].get("Name")+" : "+results[i].get("Latitude")+" : "+results[i].get("Longitude")+" ; ");
        }else{
          for(var seq=1;seq<=dpIdArray.length; seq++){
            for(j=0; j<dpIdArray.length; j++){
              if(dpIdArray[j]==(results[i].get("drpnt_id")) && seq==(results[i].get("SequenceNum"))){
        
                details = details.concat(results[i].get("Name")+" : "+results[i].get("Latitude")+" : "+results[i].get("Longitude")+" ; "); 
              }
           }
          }
        }
      }
  
      details = details.concat(temp);  
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving user details");
    }
  });
});
    
Parse.Cloud.define("getSourceNametoDriver", function(request, response) {
  var query = new Parse.Query("Cluster");
  query.equalTo("Number",request.params.cluster_id);
            
  query.find({
    success: function(results) {
      var details = "";
      for (var i = 0; i < results.length; ++i) {
        details = details.concat(results[i].get("Source")+" ; ");
      }
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving routeid details details");
    }
  });
});
   
Parse.Cloud.define("getVehiclesForCluster", function(request, response) {
  var query = new Parse.Query("vehicle");
     
            
  query.find({
    success: function(results) {
      var details = "";
      var routeIds = request.params.RouteIds;
         
      for(var j = 0; j<  routeIds.length;j++)
      {
             for (var i = 0; i < results.length; ++i) {
                
                    if(results[i].get("RouteNum") == routeIds[j])
                    {
                        details = details.concat(results[i].get("vehicle_name")+" : "+results[i].get("reg_no")+" : "+results[i].get("Capacity")+" : "+results[i].get("RouteNum")+" ; ");
                        break;
                    }
                   
            }
      }
        
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving routeid details details");
    }
  });
});
   
Parse.Cloud.define("dummySum", function(request, response) {
  var query = new Parse.Query("dummy");
     
            
  query.find({
    success: function(results) {
      var details = "";
      var promises = [];
      for (var i = 0; i < results.length; ++i) {
               
            promises.push(Parse.Cloud.run("dummyVal",{index:i},{
                success: function(results) {
   
               
                console.log("Index "+results+" obtained.");
                details = details.concat(""+results+ " , ");
                },
                error: function(results, error) {
                response.error(errorMessageMaker("running chained function",error));
                }
            }));
               
                
         
      }
         
      Parse.Promise.when(promises).then(function() {
        // all done
        response.success(details);
        }, function(err) {
        // error
        response.error("Error retrieving routeid details details");
        });
         
        
         
    },
    error: function() {
      response.error("Error retrieving routeid details details");
    }
  });
});
   
Parse.Cloud.define("dummyVal", function(request, response) {
  var query = new Parse.Query("dummy");
     
            
  query.find({
    success: function(results) {
        
      response.success(results[request.params.index].get("value"));
    },
    error: function() {
      response.error("Error retrieving routeid details details");
    }
  });
});
   
   
   
Parse.Cloud.afterSave("DropPoints", function(request) {
       
    
    Parse.Push.send({
        channels : ["CHANNEL_1", "CHANNEL_5"],
        data: {
            alert: "Map Updates",
            title : "Trigger Update"
        }
    }, {
        success: function() {
            // Push was successful
        },
        error: function(error) {
            console.log(error);
            // Handle error
        }
    });
});
   
Parse.Cloud.afterSave("Routes", function(request) {
       
    
    Parse.Push.send({
        channels : ["CHANNEL_1", "CHANNEL_5"],
        data: {
            alert: "Map Updates",
            title : "Trigger Update"
        }
    }, {
        success: function() {
            // Push was successful
        },
        error: function(error) {
            console.log(error);
            // Handle error
        }
    });
});
   
   
   
Parse.Cloud.afterSave("user", function(request) {
       
    
    Parse.Push.send({
        channels : ["CHANNEL_1", "CHANNEL_5"],
        data: {
            alert: "Update",
            title : "Trigger Update"
        }
    }, {
        success: function() {
            // Push was successful
        },
        error: function(error) {
            console.log(error);
            // Handle error
        }
    });
});
   
Parse.Cloud.afterSave("Driver", function(request) {
       
    
    Parse.Push.send({
        channels : ["CHANNEL_1", "CHANNEL_5"],
        data: {
            alert: "Update",
            title : "Trigger Update"
        }
    }, {
        success: function() {
            // Push was successful
        },
        error: function(error) {
            console.log(error);
            // Handle error
        }
    });
});
   
Parse.Cloud.afterSave("vehicle", function(request) {
       
    
    Parse.Push.send({
        channels : ["CHANNEL_1", "CHANNEL_5"],
        data: {
            alert: "Update",
            title : "Trigger Update"
        }
    }, {
        success: function() {
            // Push was successful
        },
        error: function(error) {
            console.log(error);
            // Handle error
        }
    });
});

Parse.Cloud.define("getDriverLocationForRoutes", function(request, response) {
  var query = new Parse.Query("vehiclelocation");

           
  query.find({
    success: function(results) {
         var routeNumArray = request.params.RouteNumbers.split(" , ");   
		 var details="";
		for(var i=0;i<routeNumArray.length;i++)
		{
			for(var j=0;j<results.length;j++)
			{
				if(results[j].get("RouteNum")==parseInt(routeNumArray[i]))
				{
					if(i==routeNumArray.length-1)
					{
						details = details.concat(results[j].get("latitude")+" : "+results[j].get("longitude"));
					}
					else
					{
						details = details.concat(results[j].get("latitude")+" : "+results[j].get("longitude")+" ; ");
					}
					
					
					break;
				}
				
			}
		}
      response.success(details);
    },
    error: function() {
      response.error("Error retrieving driver details");
    }
  });
});