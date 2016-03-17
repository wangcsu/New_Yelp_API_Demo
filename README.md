# New_Yelp_API_Demo

Testing the new Yelp android clientlib, get 20 nearest restaurants with ratings from Yelp. Also add runtime permission 
check for ACCESS_COARSE_LOCATION
The new Yelp android client makes oauth easier and build query strings easier, but I found some issues while developing this demo app:

#1 Gradle failed after adding 'com.yelp.clientlib:yelp-android:1.0.0' dependency, solve the issue by adding 
packagingOption {
  exclude 'META-INF/NOTICE'
  exclude 'META-INF/LICENSE'
  exclude 'META-INF/LICENSE.txt'
} 
in build.gradle(Module: app) file. 

#2 Also, the method total() in SearchResponse doesn't work as described in the documentation. Returns the number of all results 
instead of what the limit param indicated.
