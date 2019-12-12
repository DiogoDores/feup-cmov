using Newtonsoft.Json;
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
        private FavouritesPage favouritesPage;
        private List<HourlyForecast> districts = new List<HourlyForecast>();

        public MainPage()
        {
            InitializeComponent();
            this.favouritesPage = new FavouritesPage();

            // Serialize the district JSON and send it to the favourites page.
            this.favouritesPage.SetDistricts(this.DictionarySerializeDistrictJSON());
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
            this.favouritesPage.GetFavourites().ForEach(favourite => 
            {
                HourlyForecast forecast = RESTClient.SendRequest<HourlyForecast>(favourite.id);
                this.districts.Add(forecast);
            });

            base.OnAppearing();
        }


        private async void OnButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PushAsync(favouritesPage);
        }
    }
}
