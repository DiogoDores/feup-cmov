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

    public class Weather
    {
        public int id;
        public string main, description, icon;
    }

    public class Main
    {
        public double temp, temp_min, temp_max;
        public int pressure, humidity;
    }

    public class Wind
    {
        public double speed;
        public int deg;
    }

    public class Clouds
    {
        public int all;
    }

    public class Sys
    {
        public int type, id, sunrise, sunset;
        public double message;
        public string country;
    }

    public class District
    {
        public Coord coord;
        public List<Weather> weather;
        public string name;
        public Main main;
        public int visibility, dt, id, cod;
        public Wind wind;
        public Clouds clouds;
        public Sys sys;

        public double GetTemp()
        {
            return this.main.temp;
        }

        public double GetTempMin()
        {
            return this.main.temp_min;
        }

        public double GetTempMax()
        {
            return this.main.temp_max;
        }

        public int GetPressure()
        {
            return this.main.pressure;
        }

        public int GetPrecipitation()
        {   
            // TODO: Where to get precipitation?
            return -1;
        }

        public int GetHumidity()
        {
            return this.main.humidity;
        }


        public double GetWindSpeed()
        {
            return this.wind.speed;
        }
    }
}