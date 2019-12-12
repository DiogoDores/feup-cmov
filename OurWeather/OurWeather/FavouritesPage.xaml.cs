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
        public bool isFavourite = false;

        public override string ToString()
        {
            return this.name;
        }

        public void ToggleFavourite()
        {
            this.isFavourite = !this.isFavourite;
        }
    }

    public partial class FavouritesPage : ContentPage
    {
        public IList<DistrictInfo> districts { get; private set; }

        public FavouritesPage()
        {
            InitializeComponent();
        }

        public List<DistrictInfo> GetFavourites()
        {
            List<DistrictInfo> dist = this.districts.Where<DistrictInfo>(d => d.isFavourite).ToList();
            return dist;
        }


        public void SetDistricts(List<DistrictInfo> districts)
        {
            this.districts = districts;
            BindingContext = this;
        }

        private void OnListViewItemSelected(object sender, SelectedItemChangedEventArgs args)
        {
            DistrictInfo selectedItem = args.SelectedItem as DistrictInfo;
        }

        private void OnListViewItemTapped(object sender, ItemTappedEventArgs args)
        {
            DistrictInfo tappedItem = args.Item as DistrictInfo;
            tappedItem.ToggleFavourite();
        }
    }
}