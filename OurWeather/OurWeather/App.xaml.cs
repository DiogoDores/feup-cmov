using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace OurWeather
{
    public partial class App : Application
    {
        public App()
        {
            InitializeComponent();
            MainPage = new NavigationPage(new MainPage());
        }

        protected override void OnStart()
        {
            RESTClient.Initialize();    // Setup the REST client.
        }

        protected override void OnSleep()
        {
        }

        protected override void OnResume()
        {
        }
    }
}
