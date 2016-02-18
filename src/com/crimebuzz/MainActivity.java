package com.crimebuzz;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import twitter4j.Place;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends FragmentActivity  implements OnMapReadyCallback {

	GoogleMap googleMap;
	MarkerOptions markerOptions;
	LatLng latLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		setContentView(R.layout.activity_main2);
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

		if (mapFragment==null) {
			Log.e("TAG", "mapFragment is null");
			Toast.makeText(this, "null mapFragment", Toast.LENGTH_LONG).show();
			mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("googlemap");
			if (mapFragment==null) {
				Log.e("TAG", "mapFragment is null again");
				Toast.makeText(this, "null mapFragment again", Toast.LENGTH_LONG).show();
			}
		} 


		if (mapFragment != null) {
			mapFragment.getMapAsync(this);
			googleMap = mapFragment.getMap();

			// Getting reference to btn_find of the layout activity_main
			Button btn_find = (Button) findViewById(R.id.btn_find);

			// Defining button click event listener for the find button
			OnClickListener findClickListener = new OnClickListener() {			
				@Override
				public void onClick(View v) {
					// Getting reference to EditText to get the user input location
					EditText etLocation = (EditText) findViewById(R.id.et_location);

					// Getting user input location
					String location = etLocation.getText().toString();

					if(location!=null && !location.equals("")){
						new GeocoderTask().execute(location);
					}
				}
			};

			// Setting button click event listener for the find button
			btn_find.setOnClickListener(findClickListener);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMapReady(GoogleMap map) {

		map.setMyLocationEnabled(true);
		Map<String, String[]> hashMap1;
		try {

			hashMap1 = new updateTwitterStatus().execute("").get();
			Log.e("TAG","hash size: "+hashMap1.size());
			for(String key : hashMap1.keySet()){
				Bitmap.Config conf = Bitmap.Config.ARGB_8888;
				Bitmap bmp = Bitmap.createBitmap(150, 150, conf);
				Canvas canvas1 = new Canvas(bmp);

				// paint defines the text color, // stroke width, size
				Paint color = new Paint();
				color.setTextSize(35);
				color.setColor(Color.BLACK);
				try {
					if (hashMap1.get(key)[3] == null) {
						canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.twitter_pin), 0, 0, color);
					} else {
						URL url = new URL(hashMap1.get(key)[3]);
						Log.e("TAG","url is:"+url);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();   
						conn.setDoInput(true);
						conn.connect();
						InputStream is = conn.getInputStream();						
						//modify canvas
//						canvas1.drawBitmap(BitmapFactory.decodeStream(is), 0, 0, color);
						//canvas1.drawText("User Name!", 30, 40, color);
					}

					LatLng latlng = new LatLng(Double.parseDouble(hashMap1.get(key)[1]), Double.parseDouble(hashMap1.get(key)[2]));
					map.addMarker(new MarkerOptions()
							.icon(BitmapDescriptorFactory.fromBitmap(bmp)).anchor(0.5f, 1)
							.title(hashMap1.get(key)[0])
							.snippet(key)
							.position(latlng))
					//.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.twitter_pin))
					;

					map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

				}
				catch(Exception e) {					
					Log.e("TAG", "error: "+e);
					e.printStackTrace();

				}
				
				
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		addMarkers();
	}

	void addMarkers() {
		
		Map<String, String[]> hashMap1;
		try {

			hashMap1 = new executeQuery().execute("").get();
			Log.e("TAG","hash size: "+hashMap1.size());
			for(String key : hashMap1.keySet()){
				Bitmap.Config conf = Bitmap.Config.ARGB_8888;
				Bitmap bmp = Bitmap.createBitmap(150, 150, conf);
				Canvas canvas1 = new Canvas(bmp);

				// paint defines the text color, // stroke width, size
				Paint color = new Paint();
				color.setTextSize(35);
				color.setColor(Color.BLACK);
				try {
					
					canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.crime1), 0, 0, color);
					
					LatLng latlng = new LatLng(Double.parseDouble(hashMap1.get(key)[0]), Double.parseDouble(hashMap1.get(key)[1]));
					googleMap.addMarker(new MarkerOptions()
							.title("Police Record Crime")
							.snippet(key)
							.position(latlng))
							.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.crime1));

					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));

				}
				catch(Exception e) {					
					Log.e("TAG", "error: "+e);
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	private static SharedPreferences mSharedPreferences;


	// An AsyncTask class for accessing the GeoCoding Web Service
	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
			} catch (IOException e) {
				e.printStackTrace();
			}			
			return addresses;
		}


		@Override
		protected void onPostExecute(List<Address> addresses) {
			if (addresses==null || addresses.size()==0){
				Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
			}

			// Clears all the existing markers on the map
			// googleMap.clear();

			// Adding Markers on Google Map for each matching address
			for(int i=0;i<addresses.size();i++){				

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				latLng = new LatLng(address.getLatitude(), address.getLongitude());

				String addressText = String.format("%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
								address.getCountryName());

				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(addressText);

				// googleMap.addMarker(markerOptions);

				// Locate the first location
				if(i==0)			        	
					googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng)); 	
			}			
		}		
	}
	class updateTwitterStatus extends AsyncTask<String, String, Map<String, String[]>> {
		@Override
		protected Map<String, String[]> doInBackground(String... search) {
			Map<String, String[]> hashMap = new HashMap<String, String[]>();
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey("4gpkP9wI9e5rCunxi7RgTncg3");
				builder.setOAuthConsumerSecret("FCt64H2WjndHiUYz11QaPodDbfb0LymrGekflD4TG34FojM1rW");

				builder.setUseSSL(true);
				// Access Token 
				String access_token = mSharedPreferences.getString("108530990-SS3vUJNgq11XLdHUKA7hdO1zYKSzdYEF61Ghioan", "");
				// Access Token Secret
				String access_token_secret = mSharedPreferences.getString("szpMjHgBPaMNPCg5pnoshsxT3HDwZTBUkplXahuDttMR3", "");

				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

				// Update status
				String[] queryKeyWords = {"crimebuzz", "crime", "theft", "murder", "rape", "assault", "kill", "died", "knife", "gun"};
				//				String[] queryKeyWords = {"crimebuzz"};
				for (String query: queryKeyWords) {       
					QueryResult response = twitter.search(new Query(query));
					String city = "San Francisco";
					Log.e("TAG", "#tweets for "+query+" are: "+response.getTweets().size());
					for (int i = 0; i < response.getTweets().size(); i++){
						Place g = response.getTweets().get(i).getPlace();

						if (g != null) {
							city = g.getName();
							Geocoder geocoder = new Geocoder(getApplicationContext());
							String[] addr = new String[4];
							addr[0] = g.getName();	//Tweet description
							Address a = geocoder.getFromLocationName(city, 1).get(0);
							addr[1] = Double.toString(a.getLatitude());
							addr[2] = Double.toString(a.getLongitude());
							addr[3] = null;
							String[] ur = response.getTweets().get(i).getText().split("http");
							if (ur.length > 1 ) {
								addr[3] = "http" + ur[1];
							}
							Log.e("TAG", "URL:"+addr[3] +" ....." + (ur.length > 1 ? ur[1] : ur));
							hashMap.put(response.getTweets().get(i).getText(), addr);
							Log.w("TAG", "adding tweet: "+  response.getTweets().get(i).getText());
						} else {
							Log.d("TAG", "Ignoring tweet, no place: "+ response.getTweets().get(i).getText());
						}
					}
				}
			} catch (TwitterException e) {
				// Error in updating status
				Log.d("Twitter Update Error", e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return hashMap;
		}
	}





	class executeQuery extends AsyncTask<String, String, Map<String, String[]>> {
		@Override
		protected Map<String, String[]> doInBackground(String... search) {
			
			Map<String, String[]> hashMap = new HashMap<String, String[]>();

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet("https://dataclips.heroku.com/rgqunvjteezxpbnzprqlbqfrpiqm-selectALL.json");
			try {
				httpget.addHeader("Content-Type", "application/json");
				HttpResponse response = httpclient.execute(httpget);
				String result = EntityUtils.toString(response.getEntity());
				JSONObject obj = new JSONObject(result);

				for (int i=0; i<50; i++) {
					JSONArray arr = obj.getJSONArray("values").getJSONArray(i);

					if (arr==null) {
						Log.e("TAG", "null array ");
						return hashMap;
					}

					Log.w("TAG", "adding array: "+  arr.getString(0));
					String[] addr = new String[2];
					addr[0] = arr.getString(2);
					addr[1] =arr.getString(1);

					hashMap.put(arr.getString(0), addr);
					Log.w("TAG", "adding marker: "+  arr.getString(0));

				}
			
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return hashMap;
		}
	}
}