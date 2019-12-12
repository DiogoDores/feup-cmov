using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace OurWeather
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

        public static dynamic SendRequest<Type>(int id)
        {
            string url = BuildAPIUrl(typeof(Type), id), res = "";
            Task task = new Task(() => res = RESTClient.AccessWebAsync(url).Result);
            task.Start();
            task.Wait();

            return JsonConvert.DeserializeObject<Type>(res);
        }

        public async static Task<string> AccessWebAsync(string url)
        {
            Task<string> getStringTask = RESTClient.client.GetStringAsync(url);
            return await getStringTask;
        }

        private static string BuildAPIUrl(System.Type type, int id)
        {
            string header = (type == typeof(HourlyForecast) ? "weather" : "forecast");
            return String.Format("/data/2.5/{0}?id={1}&appid={2}&units=metric", header, id.ToString(), RESTClient.apiKey);
        }
    }
}