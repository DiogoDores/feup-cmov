using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Xamarin.Forms;
using Xamarin.Essentials;
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

            if (this.isFavourite)
                Preferences.Set(this.id.ToString(), this.name);
            else
                Preferences.Remove(this.id.ToString());
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
            if (Device.RuntimePlatform == Device.Android)
                NavigationPage.SetHasNavigationBar(this, false);
            InitializeComponent();
        }

        public List<DistrictInfo> GetFavourites()
        {
            List<DistrictInfo> dist = this.districts.Where<DistrictInfo>(d => d.isFavourite).ToList();

            if (dist.Count == 0)
            {
                List<DistrictInfo> dist1 = districts.Where(d => Preferences.ContainsKey(d.id.ToString())).ToList();
                dist1.ForEach (d =>
                 {
                     d.isFavourite = true;
                     d.Icon = "star_filled_icon.png";
                 }) ;
                return dist1;
            }
                

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