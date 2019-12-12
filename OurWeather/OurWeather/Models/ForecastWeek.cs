using System;
using System.Collections.Generic;
using System.Text;

namespace OurWeather.Models
{
    public class ForecastWeek
    {
        public string cod;
        public double message;
        public int cnt;
        public List<ForecastHour> list;
        public City city;
    }
}
