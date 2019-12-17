using System;
using System.Collections.Generic;
using System.Text;
using Xamarin.Forms;


namespace OurWeather.Models
{
    class AdvancedItem
    {
        public string Name { get; set; }
        public int Temperature { get; set; }
        public int TemperatureMin { get; set; }
        public int TemperatureMax { get; set; }
        public string Weather { get; set; }
        public double Pressure { get; set; }
        public double Precipitation { get; set; }
        public double WindSpeed { get; set; }
        public int WindDegrees { get; set; }
        public double Humidity { get; set; }
        public Color StartColor { get; set; }
        public Color EndColor { get; set; }

    }
}
