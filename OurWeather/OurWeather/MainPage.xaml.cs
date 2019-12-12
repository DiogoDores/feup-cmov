using Newtonsoft.Json;
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
            districts.ForEach(dist => items.Add(new CarouselItem { Name = dist.name }));
            return new CarouselViewModel { Items = items };
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

            base.OnAppearing();
        }


        private async void OnButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PushAsync(favouritesPage);
        }
    }
}
