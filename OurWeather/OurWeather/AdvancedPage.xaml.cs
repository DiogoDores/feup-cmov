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

            this.chartViewToday = (Microcharts.Forms.ChartView) FindByName("ChartViewToday");
            this.chartViewTomorrow = (Microcharts.Forms.ChartView) FindByName("ChartViewTomorrow");

            this.chartViewToday.Chart = BuildTemperatureGraph(weekly, DateTime.UtcNow);
            this.chartViewTomorrow.Chart = BuildTemperatureGraph(weekly, DateTime.UtcNow.AddDays(1));
        }

        private void OnTodayButtonClick(object sender, EventArgs e)
        {
            this.chartViewToday.IsVisible = true;
            this.chartViewTomorrow.IsVisible = false;
        }

        private void OnTomorrowButtonClick(object sender, EventArgs e)
        {
            this.chartViewToday.IsVisible = false;
            this.chartViewTomorrow.IsVisible = true;
        }

        private Microcharts.PointChart BuildTemperatureGraph(ForecastWeek forecast, DateTime targetDate)
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
                        ValueLabel = value.ToString()
                    });
                }
            });

            return new Microcharts.PointChart()
            {
                Entries = entries.ToArray(),
                BackgroundColor = SKColors.Transparent,
                LabelTextSize = 25
            };
        }
    }
}