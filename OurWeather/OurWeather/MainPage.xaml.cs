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
    // Learn more about making custom code visible in the Xamarin.Forms previewer
    // by visiting https://aka.ms/xamarinforms-previewer
    [DesignTimeVisible(false)]
    public partial class MainPage : ContentPage
    {
        private FavouritesPage favouritesPage;

        private Dictionary<string, int> favorites = new Dictionary<string, int>();
        private List<HourlyForecast> districts = new List<HourlyForecast>();

        public MainPage()
        {
            InitializeComponent();
            this.favouritesPage = new FavouritesPage();

            // Serialize the district JSON and send it to the favourites page.
            Dictionary<string, int> dist = this.DictionarySerializeDistrictJSON();
            this.favouritesPage.SetDistricts(dist);
        }

        private Dictionary<string, int> DictionarySerializeDistrictJSON()
        {
            Assembly assembly = IntrospectionExtensions.GetTypeInfo(typeof(MainPage)).Assembly;
            Stream stream = assembly.GetManifestResourceStream("OurWeather.Portugal.json");

            using (var reader = new StreamReader(stream))
            {
                string jsonStr = reader.ReadToEnd();
                List<DistrictInfo> distsList = JsonConvert.DeserializeObject<List<DistrictInfo>>(jsonStr);

                Dictionary<string, int> distsDict = new Dictionary<string, int>();
                distsList.ForEach(dist => distsDict.Add(dist.name, dist.id));

                return distsDict;
            }
        }


        private async void OnButtonClicked(object sender, EventArgs args)
        {
            await Navigation.PushAsync(favouritesPage);
        }

        
    }
}
