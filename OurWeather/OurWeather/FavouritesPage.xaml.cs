using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace OurWeather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public class DistrictInfo
    {
        public int id;
        public string name;
    }

    public partial class FavouritesPage : ContentPage
    {
        private Dictionary<string, int> districts = new Dictionary<string, int>();

        public FavouritesPage()
        {
            InitializeComponent();
        }

        public void SetDistricts(Dictionary<string, int> districts)
        {
            this.districts = districts;
        }
    }
}