﻿using Newtonsoft.Json;
using OurWeather.Models;
using OurWeather.ViewModels;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;

namespace OurWeather
{
    [DesignTimeVisible(false)]
    public partial class MainPage : ContentPage
    {
        private readonly FavouritesPage favouritesPage;
        private readonly List<Color> _backgroundColors = new List<Color>();

        public MainPage()
        {
            InitializeComponent();
            this.favouritesPage = new FavouritesPage();

            // Serialize the district JSON and send it to the favourites page.
            this.favouritesPage.SetDistricts(this.DictionarySerializeDistrictJSON());
        }

        private CarouselViewModel PopulateCarousel(List<ForecastHour> districts)
        {
            List<CarouselItem> items = new List<CarouselItem>();

            Color startC, endC;
            String src, tip, fName;

            foreach (var dist in districts) {

                if (dist.name.IndexOf("da") != -1)
                    fName = dist.name.Substring(dist.name.IndexOf("da" , StringComparison.Ordinal) + 3);
                else if (dist.name.IndexOf("de") != -1)
                    fName = dist.name.Substring(dist.name.IndexOf("de", StringComparison.Ordinal) + 3);
                else if (dist.name.IndexOf("dos") != -1)
                    fName = dist.name.Substring(dist.name.IndexOf("dos", StringComparison.Ordinal) + 4);
                else
                    fName = dist.name.Substring(dist.name.IndexOf("do", StringComparison.Ordinal) + 3);

                if (dist.weather[0].main == "Rain" || dist.weather[0].main == "Drizzle") {
                    startC = Color.FromHex("#174159");
                    endC = Color.FromHex("#4899C6");
                    src = "rainy.png";
                    tip = "Don't forget your umbrella!";
                }
                else if (dist.weather[0].main == "Clear") {
                    startC = Color.FromHex("#FFBC58");
                    endC = Color.FromHex("#FF7448");
                    src = "sunny.png";
                    tip = "Don't forget your sunglasses!";
                }
                else if (dist.weather[0].main == "Clouds") {
                    startC = Color.FromHex("#4F4F4F");
                    endC = Color.FromHex("#959595");
                    src = "cloudy.png";
                    tip = "Seems like it's gonna be a gloomy day";
                }
                else if (dist.weather[0].main == "Snow")
                {
                    startC = Color.FromHex("#174159");
                    endC = Color.FromHex("#4899C6");
                    src = "snowy.png";
                    tip = "Make sure to wear a warm jacket!";
                }
                else if (dist.weather[0].main == "Thunderstorm")
                {
                    startC = Color.FromHex("#174159");
                    endC = Color.FromHex("#4899C6");
                    src = "stormy.png";
                    tip = "Avoid being outside for too long!";
                }
                else {
                    startC = Color.FromHex("#4F4F4F");
                    endC = Color.FromHex("#959595");
                    src = "clear.png";
                    tip = "";
                }

                items.Add(new CarouselItem {
                    Name = dist.name,
                    FormattedName = fName,
                    Temperature = (int) Math.Round(dist.main.temp),
                    Weather = dist.weather[0].main,
                    Tip = tip,
                    BackgroundColor = Color.FromHex("#F5F5F5"),
                    StartColor = startC,
                    EndColor = endC,
                    ImageSrc = src,
            });
            }

            return new CarouselViewModel { Items = items };
        }

        public void Handle_Scrolled(object sender, ItemsViewScrolledEventArgs e)
        {
            var position = e.HorizontalOffset;

            if (_backgroundColors.Count == 0)
            {
                return;
            }

            // Set the background color of our page to the item in the color gradient
            // array, matching the current scrollindex.
            if (position > _backgroundColors.Count - 1)
                page.BackgroundColor = _backgroundColors.Last();
            else if (position < 0)
                page.BackgroundColor = _backgroundColors.First();
            else
                page.BackgroundColor = _backgroundColors[(int)position];
        }

        private List<DistrictInfo> DictionarySerializeDistrictJSON()
        {
            Assembly assembly = IntrospectionExtensions.GetTypeInfo(typeof(MainPage)).Assembly;
            Stream stream = assembly.GetManifestResourceStream("OurWeather.Portugal.json");

            using (var reader = new StreamReader(stream))
            {
                string jsonStr = reader.ReadToEnd();
                return JsonConvert.DeserializeObject<List<DistrictInfo>>(jsonStr);
            }
        }

        protected override void OnAppearing()
        {
            _backgroundColors.Clear();

            List<ForecastHour> districts = new List<ForecastHour>();

            this.favouritesPage.GetFavourites().ForEach(favourite => 
            {
                ForecastHour forecast = RESTClient.SendRequest<ForecastHour>(favourite.id);
                districts.Add(forecast);
            });

            // Populate carousel and bind it to context.
            CarouselViewModel model = this.PopulateCarousel(districts);

            Label label = (Label) FindByName("NoFavouritesFoundLabel");
            label.IsVisible = model.Items.Count == 0 ? true : false;

            BindingContext = model;

            // Create out a list of background colors based on our items colors so we can do a gradient on scroll.
            for (int i = 0; i < model.Items.Count; i++)
            {
                var current = model.Items[i];
                var next = model.Items.Count > i + 1 ? model.Items[i + 1] : null;

                if (next != null)
                    _backgroundColors.AddRange(SetGradients(current.BackgroundColor, next.BackgroundColor, 375));
                else
                    _backgroundColors.Add(current.BackgroundColor);
            }

            base.OnAppearing();
        }


        private async void OnButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PushAsync(favouritesPage);
        }

        private async void OnCarouselTapped(object sender, EventArgs args)
        {
            Grid item = (Grid) sender;
            CarouselItem context = (CarouselItem) item.BindingContext;

            // Fetch district object whose name matches the binding context's title.
            DistrictInfo district = favouritesPage.GetFavourites().Find(fav => fav.name == context.Name);
            await Navigation.PushAsync(new AdvancedPage(district));
        }

        // Create a list of all the colors in between our start and end color.
        public static IEnumerable<Color> SetGradients(Color start, Color end, int steps)
        {
            var colorList = new List<Color>();

            double aStep = ((end.A * 255) - (start.A * 255)) / steps;
            double rStep = ((end.R * 255) - (start.R * 255)) / steps;
            double gStep = ((end.G * 255) - (start.G * 255)) / steps;
            double bStep = ((end.B * 255) - (start.B * 255)) / steps;

            for (int i = 0; i < steps; i++)
            {
                var a = (start.A * 255) + (int)(aStep * i);
                var r = (start.R * 255) + (int)(rStep * i);
                var g = (start.G * 255) + (int)(gStep * i);
                var b = (start.B * 255) + (int)(bStep * i);

                colorList.Add(Color.FromRgba(r / 255, g / 255, b / 255, a / 255));
            }

            return colorList;
        }
    }
}
