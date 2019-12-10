using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Newtonsoft.Json;

namespace MyWeather
{   
    public class Coord
    {
        public double lon, lat;
    }

    public class City
    {
        public int id, timezone, sunrise, sunset;
        public string name, country;
        public Coord coord;
    }

    public class Weather
    {
        public int id;
        public string main, description, icon;
    }

    public class Main
    {
        public double temp, temp_min, temp_max, pressure, sea_level, grnd_level, humidity, temp_kf;
    }

    public class Wind
    {
        public int deg;
        public double speed, gust;
    }

    public class Snow
    {
        [JsonProperty("3h")]
        public double _3h;
    }

    public class Rain
    {
        [JsonProperty("3h")]
        public double _3h;
    }

    public class Clouds
    {
        public int all;
    }

    public class Sys
    {
        public int type, id, sunrise, sunset;
        public double message;
        public string country, pod;
    }

    public class HourlyForecast
    {
        public Coord coord;
        public List<Weather> weather;
        public string name, dt_text;
        public Main main;
        public int visibility, dt, id, cod;
        public Wind wind;
        public Clouds clouds;
        public Snow snow;
        public Sys sys;
    }

    public class WeeklyForecast
    {
        public string cod;
        public double message;
        public int cnt;
        public List<HourlyForecast> list;
        public City city;
    }
}