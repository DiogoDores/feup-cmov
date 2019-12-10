using Android.App;
using Android.OS;
using Android.Support.V7.App;
using Android.Runtime;
using Android.Widget;
using System.Threading.Tasks;
using System.Net.Http;
using System;
using Android.Content;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace MyWeather
{
    [Activity(Label = "@string/app_name", Theme = "@style/AppTheme", MainLauncher = true)]
    public class MainActivity : AppCompatActivity
    {
        private Dictionary<string, int> favorites = new Dictionary<string, int>();
        private List<HourlyForecast> districts = new List<HourlyForecast>();

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            Xamarin.Essentials.Platform.Init(this, savedInstanceState);
            
            // Set our view from the "main" layout resource
            SetContentView(Resource.Layout.activity_main);

            FindViewById<Button>(Resource.Id.button1).Click += delegate
            {
                StartActivityForResult(new Intent(this, typeof(FavoriteActivity)), 0);
            };
        }

        public override void OnRequestPermissionsResult(int requestCode, string[] permissions, [GeneratedEnum] Android.Content.PM.Permission[] grantResults)
        {
            Xamarin.Essentials.Platform.OnRequestPermissionsResult(requestCode, permissions, grantResults);
            base.OnRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        protected override void OnStart()
        {
            RESTClient.Initialize();
            base.OnStart();
        }

        protected override void OnResume()
        {
            this.districts.Clear();

            foreach(KeyValuePair<string, int> entry in this.favorites)
            {
                HourlyForecast dist = RESTClient.SendRequest<HourlyForecast>(entry.Value);
                this.districts.Add(dist);
            }

            base.OnResume();
        }

        protected override void OnActivityResult(int requestCode, [GeneratedEnum] Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);
            
            if (resultCode == Result.Ok)
            {
                string extraString = data.GetStringExtra("favorites");
                this.favorites = JsonConvert.DeserializeObject<Dictionary<string, int>>(extraString);
            }
        }
    }
}