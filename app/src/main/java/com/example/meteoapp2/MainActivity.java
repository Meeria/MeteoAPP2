package com.example.meteoapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "8466cf82432785431bee4e6fe8be9536"; // Remplacez par votre clé API OpenWeatherMap
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private EditText cityInput;
    private Button addCityButton, refreshButton;
    private ListView cityListView;

    private ArrayList<CityWeather> cityList;
    private CityAdapter cityAdapter;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityInput = findViewById(R.id.cityInput);
        addCityButton = findViewById(R.id.addCityButton);
        refreshButton = findViewById(R.id.refreshButton);
        cityListView = findViewById(R.id.cityListView);

        cityList = new ArrayList<>();
        cityAdapter = new CityAdapter(this, cityList);
        cityListView.setAdapter(cityAdapter);

        client = new OkHttpClient();

        // Ajouter une ville
        addCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityInput.getText().toString().trim();
                if (!city.isEmpty() && !cityList.contains(city)) {
                    cityList.add(new CityWeather(city)); // Ajoute un objet CityWeather
                    cityAdapter.notifyDataSetChanged();
                    cityInput.setText("");
                }
            }
        });

        // Rafraîchir la météo pour toutes les villes
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CityWeather city : cityList) {
                    fetchWeather(city.getCityName()); // Récupère la météo pour chaque ville
                }
            }
        });

        // Supprimer une ville par un clic
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityList.remove(position);
                cityAdapter.notifyDataSetChanged();
            }
        });

        // Modifier une ville par un clic long
        cityListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String oldCity = cityList.get(position).getCityName();
                final EditText input = new EditText(MainActivity.this);
                input.setText(oldCity);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Modifier la ville")
                        .setView(input)
                        .setPositiveButton("Modifier", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newCity = input.getText().toString().trim();
                                if (!newCity.isEmpty() && !cityList.contains(newCity)) {
                                    cityList.set(position, new CityWeather(newCity));
                                    cityAdapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("Annuler", null)
                        .show();

                return true;
            }
        });
    }

    // Récupérer les données météo pour une ville
    private void fetchWeather(final String city) {
        String url = BASE_URL + "?q=" + city + ",fr&appid=" + API_KEY + "&units=metric&lang=fr";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                updateUI(city, "Erreur réseau : " + e.getMessage(), "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String weather = json.getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("description");
                        String iconCode = json.getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("icon");  // Récupérer le code d'icône
                        double temperature = json.getJSONObject("main").getDouble("temp");

                        String result = weather + "\n" + "Température : " + temperature + "°C";
                        updateUI(city, result, iconCode);  // Passer l'icône ici
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateUI(city, "Erreur lors du traitement des données.", "");
                    }
                } else {
                    updateUI(city, "Erreur : " + response.message(), "");
                }
            }
        });
    }

    // Mettre à jour la liste des villes avec les données météo
    private void updateUI(final String city, final String weatherInfo, final String iconCode) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (CityWeather cityWeather : cityList) {
                    if (cityWeather.getCityName().equals(city)) {
                        cityWeather.setWeatherDescription(weatherInfo);
                        cityWeather.setIconCode(iconCode);
                        cityAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
    }

    // Adapter personnalisé pour afficher les villes et leurs données météo
    public class CityAdapter extends ArrayAdapter<CityWeather> {
        public CityAdapter(MainActivity context, ArrayList<CityWeather> cities) {
            super(context, 0, cities);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_city, parent, false);
            }

            CityWeather cityWeather = getItem(position);

            TextView cityName = convertView.findViewById(R.id.cityName);
            TextView weatherDescription = convertView.findViewById(R.id.weatherDescription);
            TextView temperature = convertView.findViewById(R.id.temperature);
            ImageView weatherIcon = convertView.findViewById(R.id.weatherIcon);

            // Remplir les vues avec les données
            cityName.setText(cityWeather.getCityName());
            weatherDescription.setText(cityWeather.getWeatherDescription());
            temperature.setText("");  // Ou mettez ici la température si nécessaire

            String iconCode = cityWeather.getIconCode();
            if (iconCode != null && !iconCode.isEmpty()) {
                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                Glide.with(getContext())
                        .load(iconUrl)
                        .into(weatherIcon);
            }

            return convertView;
        }
    }

    // Classe pour stocker les informations météo d'une ville
    public class CityWeather {
        private String cityName;
        private String weatherDescription;
        private String iconCode;

        public CityWeather(String cityName) {
            this.cityName = cityName;
        }

        public String getCityName() {
            return cityName;
        }

        public String getWeatherDescription() {
            return weatherDescription;
        }

        public void setWeatherDescription(String weatherDescription) {
            this.weatherDescription = weatherDescription;
        }

        public String getIconCode() {
            return iconCode;
        }

        public void setIconCode(String iconCode) {
            this.iconCode = iconCode;
        }
    }
}

