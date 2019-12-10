using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Newtonsoft.Json;

namespace MyWeather
{
    public class DistrictInfo
    {
        public int id;
        public string name;
    }

    [Activity(Label = "FavoriteActivity")]
    public class FavoriteActivity : ListActivity
    {
        private Dictionary<string, int> districts = new Dictionary<string, int>();
        private Dictionary<string, int> favorites = new Dictionary<string, int>();

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);

            SetContentView(Resource.Layout.activity_favorite);

            // Import portuguese districts from JSON file.
            this.districts = SerializeDistrictJSON();

            ListAdapter = new ArrayAdapter<string>(this, Resource.Layout.favorite_item, this.districts.Keys.ToList<string>());
            ListView.TextFilterEnabled = true;

            ListView.ItemClick += delegate (object sender, AdapterView.ItemClickEventArgs args)
            {
                string district = ((TextView) args.View).Text;

                if (!this.favorites.Keys.Contains(district))
                {
                    this.favorites.Add(district, this.districts[district]);
                    Toast.MakeText(Application, "Added to favorites!", ToastLength.Short).Show();
                } 
                else
                {
                    this.favorites.Remove(district);
                    Toast.MakeText(Application, "Removed from favorites!", ToastLength.Short).Show();
                }
            };
        }

        private Dictionary<string, int> SerializeDistrictJSON()
        {
            using (var stream = Assembly.GetExecutingAssembly().GetManifestResourceStream("MyWeather.Resources.portugal.json"))
            using (var reader = new StreamReader(stream))
            {
                string jsonStr = reader.ReadToEnd();
                List<DistrictInfo> distsList = JsonConvert.DeserializeObject<List<DistrictInfo>>(jsonStr);
                
                Dictionary<string, int> distsDict = new Dictionary<string, int>();
                distsList.ForEach(dist => distsDict.Add(dist.name, dist.id));

                return distsDict;
            }
        }

        public override void OnBackPressed()
        {
            Intent intent = new Intent(this, typeof(MainActivity));
            intent.PutExtra("favorites", JsonConvert.SerializeObject(this.favorites));
            
            SetResult(Result.Ok, intent);
            Finish();
        }
    }
}