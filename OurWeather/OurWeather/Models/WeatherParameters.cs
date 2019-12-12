using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace OurWeather.Models
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

    public class WeatherParameters
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
}