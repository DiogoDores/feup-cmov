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
        private ForecastHour hourly;
        private ForecastWeek weekly;

        public AdvancedPage(DistrictInfo district)
        {
            InitializeComponent();

            this.hourly = RESTClient.SendRequest<ForecastHour>(district.id); // TODO: Probably recycle this API call.
            this.weekly = RESTClient.SendRequest<ForecastWeek>(district.id);

            Color startColor = Color.Transparent, endColor = Color.Transparent;
            GetWeatherColors(hourly.weather[0].main, ref startColor, ref endColor);

            BindingContext = this.BuildWeatherItem<ForecastHour>(hourly);

            this.chartViewToday = (Microcharts.Forms.ChartView) FindByName("ChartViewToday");
            this.chartViewTomorrow = (Microcharts.Forms.ChartView) FindByName("ChartViewTomorrow");

            this.chartViewToday.Chart = BuildTemperatureGraph(weekly, DateTime.UtcNow);
            this.chartViewTomorrow.Chart = BuildTemperatureGraph(weekly, DateTime.UtcNow.AddDays(1));

            Label warning = (Label)FindByName("LabelUnavailablePlot");
            warning.IsVisible = this.chartViewToday.Chart.Entries.Count() == 0;
        }

        private void GetWeatherColors(string weather, ref Color startColor, ref Color endColor)
        {
            if (weather == "Rain" || weather == "Drizzle")
            {
                startColor = Color.FromHex("#174159");
                endColor = Color.FromHex("#4899C6");
            }
            else if (weather == "Clear")
            {
                startColor = Color.FromHex("#FFBC58");
                endColor = Color.FromHex("#FF7448");
            }
            else if (weather == "Clouds")
            {
                startColor = Color.FromHex("#4F4F4F");
                endColor = Color.FromHex("#959595");
            }
            else if (weather == "Snow")
            {
                startColor = Color.FromHex("#174159");
                endColor = Color.FromHex("#4899C6");

            }
            else if (weather == "Thunderstorm")
            {
                startColor = Color.FromHex("#174159");
                endColor = Color.FromHex("#4899C6");
            }
            else
            {
                startColor = Color.FromHex("#4F4F4F");
                endColor = Color.FromHex("#959595");
            }
        }

        private AdvancedItem BuildWeatherItem<T>(T forecast)
        {
            if (typeof(T) == typeof(ForecastHour))
            {
                ForecastHour hourly = forecast as ForecastHour;
                Color startColor = Color.Transparent, endColor = Color.Transparent;
                GetWeatherColors(hourly.weather[0].main, ref startColor, ref endColor);

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
                string tomorrow = DateTime.UtcNow.AddDays(1).ToString("yyyy-MM-dd");

                double TemperatureAvg = 0.0;
                double TemperatureLowest = Double.MaxValue, TemperatureHighest = Double.MinValue;
                double PressureAvg = 0.0, WindSpeedAvg = 0.0, PrecipitationAvg = 0.0, HumidityAvg = 0.0;

                List<ForecastHour> filter = weekly.list.Where(entry => entry.dt_txt.Contains(tomorrow)).ToList();

                filter.ForEach(entry =>
                {
                    // Find the average temperature.
                    TemperatureAvg += entry.main.temp;
                    
                    // Find the lowest temperature.
                    if (entry.main.temp_min < TemperatureLowest) { TemperatureLowest = entry.main.temp_min; }

                    // Find the highest temperature.
                    if (entry.main.temp_max > TemperatureHighest) { TemperatureHighest = entry.main.temp_max; }

                    // Get the average pressure.
                    PressureAvg += entry.main.pressure;

                    // Get the average wind speed.
                    WindSpeedAvg += entry.wind.speed;

                    // Get the sum of the precipitation.
                    if (entry.rain != null) { PrecipitationAvg += entry.rain._3h; }

                    // Get the average humidity.
                    HumidityAvg += entry.main.humidity;
                });

                Color startColor = Color.Transparent, endColor = Color.Transparent;
                this.GetWeatherColors(weekly.list[0].weather[0].main, ref startColor, ref endColor);

                return new AdvancedItem
                {
                    Name = weekly.city.name,
                    Temperature = (int) Math.Round(TemperatureAvg / filter.Count),
                    TemperatureMin = (int) Math.Round(TemperatureLowest),
                    TemperatureMax = (int) Math.Round(TemperatureHighest),
                    Weather = weekly.list[0].weather[0].main,
                    Pressure = (int) Math.Round(PressureAvg / filter.Count),
                    Precipitation = (int) Math.Round(PrecipitationAvg / filter.Count),
                    WindSpeed = (int) Math.Round(WindSpeedAvg / filter.Count),
                    Humidity = (int) Math.Round(HumidityAvg / filter.Count),
                    StartColor = startColor,
                    EndColor = endColor
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

            Label warning = (Label) FindByName("LabelUnavailablePlot");
            warning.IsVisible = this.chartViewToday.Chart.Entries.Count() == 0;

            BindingContext = BuildWeatherItem<ForecastHour>(this.hourly);
        }

        private void OnTomorrowButtonClick(object sender, EventArgs e)
        {
            ((Button)FindByName("ButtonTomorrow")).TextColor = Color.White;
            ((Button)FindByName("ButtonToday")).TextColor = Color.LightGray;
            this.chartViewToday.IsVisible = false;
            this.chartViewTomorrow.IsVisible = true;

            ((Label)FindByName("LabelUnavailablePlot")).IsVisible = false;

            BindingContext = BuildWeatherItem<ForecastWeek>(this.weekly);
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