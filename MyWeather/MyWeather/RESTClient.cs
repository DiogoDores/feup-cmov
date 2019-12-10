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
    public static class RESTClient
    {
        public static string apiKey = "06b6cee85ee48cacb7685d8b039e1339";

        public class RootObject
        {
            public int cnt;
            public List<Info> list;
        }
        /*
        public static void Refresh()
        {
            string res;

            Task task = new Task(() => res = RESTClient.AccessWebAsync(url).Result);
            task.Start();
            task.Wait();
            RootObject obj = JsonConvert.DeserializeObject<RootObject>(res);
        }
        */
        
        public async static Task<string> AccessWebAsync(string url)
        {
            HttpClient client = new HttpClient();
            Task<string> getStringTask = client.GetStringAsync(url);
            return await getStringTask;
        }
    }
}