using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace OurWeather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public class DistrictInfo : INotifyPropertyChanged
    {
        public int id;
        public string name { get; set; }
        public bool isFavourite = false;

        public event PropertyChangedEventHandler PropertyChanged;

        public string Icon { get; set; } = "star_not_filled_icon.png";

        public override string ToString()
        {
            return this.name;
        }

        public void ToggleFavourite()
        {
            this.isFavourite = !this.isFavourite;
            this.Icon = this.isFavourite ? "star_filled_icon.png" : "star_not_filled_icon.png";
            this.OnPropertyChanged("Icon");
        }

        protected virtual void OnPropertyChanged(string propertyName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
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