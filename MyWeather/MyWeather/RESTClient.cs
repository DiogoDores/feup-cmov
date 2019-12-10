using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Newtonsoft.Json;

namespace MyWeather
{
    public class RESTClient
    {
        public static string apiKey = "06b6cee85ee48cacb7685d8b039e1339";
        private static HttpClient client = new HttpClient();

        public static void Initialize()
        {
            const string @base = "http://api.openweathermap.org";
            client.BaseAddress = new Uri(@base);
        }

        public static District RequestWeatherInfo(int id)
        {
            string url = RESTClient.BuildRefreshUrl(id), res = "";

            Task task = new Task(() => res = RESTClient.AccessWebAsync(url).Result);
            task.Start();
            task.Wait();

            return JsonConvert.DeserializeObject<District>(res);
        }
        
        public async static Task<string> AccessWebAsync(string url)
        {
            Task<string> getStringTask = RESTClient.client.GetStringAsync(url);
            return await getStringTask;
        }

        public static string BuildRefreshUrl(int id)
        {
            return String.Format("/data/2.5/weather?id={0}&appid={1}", id.ToString(), RESTClient.apiKey);
        }
    }
}