using OurWeather.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace OurWeather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class AdvancedPage : ContentPage
    {
        public AdvancedPage(DistrictInfo district)
        {
            InitializeComponent();

            ForecastHour hourly = RESTClient.SendRequest<ForecastHour>(district.id); // TODO: Probably recycle this API call.
            ForecastWeek weekly = RESTClient.SendRequest<ForecastWeek>(district.id);

            BindingContext = new AdvancedItem
            {
                Name = hourly.name,
                Temperature = (int)Math.Round(hourly.main.temp),
                Weather = hourly.weather[0].main,
                Pressure = hourly.main.pressure,
                WindSpeed = hourly.wind.speed,
                WindDegrees = hourly.wind.deg,
                Humidity = hourly.main.humidity
            };
        }
    }
}