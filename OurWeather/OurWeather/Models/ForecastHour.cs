using System;
using System.Collections.Generic;
using System.Text;

namespace OurWeather.Models
{
    public class ForecastHour
    {
        public Coord coord;
        public List<WeatherParameters> weather;
        public string name, dt_text, dt_txt;
        public Main main;
        public int visibility, dt, id, cod;
        public Wind wind;
        public Clouds clouds;
        public Snow snow;
        public Sys sys;

        public override string ToString()
        {
            return this.name;
        }
    }
}
