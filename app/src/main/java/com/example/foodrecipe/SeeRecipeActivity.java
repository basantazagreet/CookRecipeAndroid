
package com.example.foodrecipe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SeeRecipeActivity extends AppCompatActivity {

//   Log.v("TAGESTO","Line " +line);

    ImageView ivfood;
    TextView tvtitle;
    TextView tvreadyin;
    TextView tvorigin;
    TextView tvdescription;
    TextView tvingredients;
    TextView tvsteps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_recipe);

        //Back Button dekhauna, parent manifest ma set cha
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ivfood = findViewById(R.id.ivfood);
        tvtitle = findViewById(R.id.tvtitle);
        tvreadyin = findViewById(R.id.tvreadyin);
        tvorigin = findViewById(R.id.tvorigin);
        tvdescription = findViewById(R.id.tvdescription);
        tvingredients = findViewById(R.id.tvingredients);
        tvsteps = findViewById(R.id.tvsteps);



        new NetworkAsyncTask().execute();

    }



    private class NetworkAsyncTask extends AsyncTask<Void, Void, String> {



        String imagepathformat = "http://192.168.1.126:8000/storage";





        @Override
        protected String doInBackground(Void... voids) {
            String finalResult = "";



            //Intent bata id lyako
            Bundle bundle = getIntent().getExtras();
            String id = bundle.getString("id");

            try {
                //1. Declare URL Connection.
                URL url = new URL("http://192.168.1.126:8000/api/getrecipe/"+id);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //2. Open InputStream to connection.
                connection.connect();
                InputStream in = connection.getInputStream();

                //3. Download and decode the string response using builder.
                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }


                finalResult = stringBuilder.toString();



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return finalResult;


        }




        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            try {

                //Euta matra object lai yo code use garne
                JSONObject firstRecipe = new JSONObject(s);

                String foodimgurl = imagepathformat+firstRecipe.getString("img");
                String title = firstRecipe.getString("title");
                String description = firstRecipe.getString("description");
                String origin = firstRecipe.getString("origin");
                String steps = firstRecipe.getString("steps");
                String ingredients = firstRecipe.getString("ingredients");
                String readyin = firstRecipe.getString("readyin");



                //Photo load huna time lagni vara asynctask chahincha
                new DownloadImageTask((ImageView) findViewById(R.id.ivfood))
                        .execute(foodimgurl);


                tvtitle.setText(title);
                tvdescription.setText(description);
                tvorigin.setText(origin);
                tvsteps.setText(steps);
                tvingredients.setText(ingredients);
                tvreadyin.setText(readyin);



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    //Photo load garne asynctask

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }







}