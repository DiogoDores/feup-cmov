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

            /*
            var model = new CarouselViewModel
            {
                Items = new List<CarouselItem>()
                {
                    // Just create some dummy data here for now.
                    new CarouselItem{ Type="JUICY AND ORANGE", ImageSrc="logo.png", Name = "Porto", Price = 120, Title = "22º", BackgroundColor= Color.FromHex("#000000"), StartColor=Color.FromHex("#f3463f"),  EndColor=Color.FromHex("#fece49")},
                    new CarouselItem{ Type="NOT A TYPICAL FRUIT", ImageSrc="tomato.png", Name = "TERRIBLE TOMATO", Price = 129, Title = "TERRIBLE TOMATO", BackgroundColor= Color.FromHex("#fab62a"), StartColor=Color.FromHex("#42a7ff"),  EndColor=Color.FromHex("#fab62a")},
                    new CarouselItem{ Type="SWEET AND GREEN", ImageSrc="pear.png", Name = "PEAR PARTY", Price = 140, Title = "PEAR PARTY", BackgroundColor= Color.FromHex("#425cfc"), StartColor=Color.FromHex("#33ccf3"),  EndColor=Color.FromHex("#ccee44")}
                }
            };
            BindingContext = model;
            */




            this.favouritesPage = new FavouritesPage();

            // Serialize the district JSON and send it to the favourites page.
            this.favouritesPage.SetDistricts(this.DictionarySerializeDistrictJSON());
        }

        private CarouselViewModel PopulateCarousel(List<ForecastHour> districts)
        {
            List<CarouselItem> items = new List<CarouselItem>();
            districts.ForEach(dist => items.Add(new CarouselItem
            {
                Name = dist.name,
                Temperature = (int) Math.Round(dist.main.temp),
                Weather = dist.weather[0].main,
                Tip = "Don't forget your frozen lasagne!",
                BackgroundColor = Color.FromHex("#000000"),
                StartColor = Color.FromHex("#f3463f"),
                EndColor = Color.FromHex("#fece49")
            }));
            return new CarouselViewModel { Items = items };
        }

        public void Handle_Scrolled(object sender, ItemsViewScrolledEventArgs e)
        {
            var position = e.HorizontalOffset;

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
            List<ForecastHour> districts = new List<ForecastHour>();

            this.favouritesPage.GetFavourites().ForEach(favourite => 
            {
                ForecastHour forecast = RESTClient.SendRequest<ForecastHour>(favourite.id);
                districts.Add(forecast);
            });

            // Populate carousel and bind it to context.
            CarouselViewModel model = this.PopulateCarousel(districts);
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