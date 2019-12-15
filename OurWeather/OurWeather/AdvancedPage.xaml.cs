using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace OurWeather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class AdvancedPage : ContentPage
    {
        private DistrictInfo district;

        public AdvancedPage(DistrictInfo district)
        {
            InitializeComponent();
            this.district = district;

            // TODO: Make REST call for advanced stuff here.
        }
    }
}