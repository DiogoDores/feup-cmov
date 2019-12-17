using OurWeather.Models;
using SkiaSharp;
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
        private Microcharts.Forms.ChartView chartViewToday, chartViewTomorrow;

        public AdvancedPage(DistrictInfo district)
        {
            InitializeComponent();

            ForecastHour hourly = RESTClient.SendRequest<ForecastHour>(district.id); // TODO: Probably recycle this API call.
            ForecastWeek weekly = RESTClient.SendRequest<ForecastWeek>(district.id);

            Color startC, endC;

            if (hourly.weather[0].main == "Rain" || hourly.weather[0].main == "Drizzle")
            {
                startC = Color.FromHex("#174159");
                endC = Color.FromHex("#4899C6");
            }
            else if (hourly.weather[0].main == "Clear")
            {
                startC = Color.FromHex("#FFBC58");
                endC = Color.FromHex("#FF7448");
            }
            else if (hourly.weather[0].main == "Clouds")
            {
                startC = Color.FromHex("#4F4F4F");
                endC = Color.FromHex("#959595");
            }
            else if (hourly.weather[0].main == "Snow")
            {
                startC = Color.FromHex("#174159");
                endC = Color.FromHex("#4899C6");

            }
            else if (hourly.weather[0].main == "Thunderstorm")
            {
                startC = Color.FromHex("#174159");
                endC = Color.FromHex("#4899C6");
            }
            else
            {
                startC = Color.FromHex("#4F4F4F");
                endC = Color.FromHex("#959595");
            }

            BindingContext = this.BuildWeatherItem<ForecastHour>(hourly, startC, endC);

            this.chartViewToday = (Microcharts.Forms.ChartView) FindByName("ChartViewToday");
            this.chartViewTomorrow = (Microcharts.Forms.ChartView) FindByName("ChartViewTomorrow");

            this.chartViewToday.Chart = BuildTemperatureGraph(weekly, DateTime.UtcNow);
            this.chartViewTomorrow.Chart = BuildTemperatureGraph(weekly, DateTime.UtcNow.AddDays(1));
        }

        private AdvancedItem BuildWeatherItem<T>(T forecast, Color startColor, Color endColor)
        {
            if (typeof(T) == typeof(ForecastHour))
            {
                ForecastHour hourly = forecast as ForecastHour;
                return new AdvancedItem
                {
                    Name = hourly.name,
                    Temperature = (int) Math.Round(hourly.main.temp),
                    TemperatureMin = (int) Math.Round(hourly.main.temp_min),
                    TemperatureMax = (int) Math.Round(hourly.main.temp_max),
                    Weather = hourly.weather[0].main,
                    Pressure = hourly.main.pressure,
                    Precipitation = hourly.rain == null ? 0 : hourly.rain._3h,
                    WindSpeed = hourly.wind.speed,
                    WindDegrees = hourly.wind.deg,
                    Humidity = hourly.main.humidity,
                    StartColor = startColor,
                    EndColor = endColor,
                };
            }
            else if (typeof(T) == typeof(ForecastWeek))
            {
                ForecastWeek weekly = forecast as ForecastWeek;
                return new AdvancedItem
                {

                };
            }

            return null;
        }

        private void OnTodayButtonClick(object sender, EventArgs e)
        {
            ((Button)FindByName("ButtonTomorrow")).TextColor = Color.LightGray;
            ((Button)FindByName("ButtonToday")).TextColor = Color.White;
            this.chartViewToday.IsVisible = true;
            this.chartViewTomorrow.IsVisible = false;
        }

        private void OnTomorrowButtonClick(object sender, EventArgs e)
        {
            ((Button)FindByName("ButtonTomorrow")).TextColor = Color.White;
            ((Button)FindByName("ButtonToday")).TextColor = Color.LightGray;
            this.chartViewToday.IsVisible = false;
            this.chartViewTomorrow.IsVisible = true;
        }

        private Microcharts.LineChart BuildTemperatureGraph(ForecastWeek forecast, DateTime targetDate)
        {
            List<Microcharts.Entry> entries = new List<Microcharts.Entry>();
            string day = targetDate.ToString("yyyy-MM-dd");
            
            forecast.list.ForEach(entry =>
            {
                if (entry.dt_txt.Contains(day))
                {
                    int value = (int) Math.Round(entry.main.temp);

                    entries.Add(new Microcharts.Entry(value)
                    {
                        Label = DateTime.Parse(entry.dt_txt).ToString("HH:mm"),
                        ValueLabel = value.ToString(),
                        TextColor = SKColors.White,
                        Color = SKColors.White
                    });
                }
            });

            return new Microcharts.LineChart()
            {
                Entries = entries.ToArray(),
                BackgroundColor = SKColors.Transparent,
                LineAreaAlpha = 0,
                LineSize = 10,
                LabelTextSize = 30
            };
        }
    }
}