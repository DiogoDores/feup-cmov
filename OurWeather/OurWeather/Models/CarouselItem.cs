using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using Xamarin.Forms;

namespace OurWeather.Models
{
    public class CarouselItem : INotifyPropertyChanged
    {
        public string Name { get; set; }
        public string FormattedName { get; set; }
        public int Temperature { get; set; }
        public string Weather { get; set; }
        public string Tip { get; set; }
        public Uri Icon { get; set; }


        public Color StartColor { get; set; }
        public Color EndColor { get; set; }
        public Color BackgroundColor { get; set; }
        public string ImageSrc { get; set; }

        private double _position;
        private double _scale;

        public CarouselItem()
        {
            Scale = 1;
        }

        public double Position
        {
            get { return _position; }
            set
            {
                _position = value;
                OnPropertyChanged();
            }
        }

        public double Scale
        {
            get { return _scale; }
            set
            {
                _scale = value;
                OnPropertyChanged();
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        protected virtual void OnPropertyChanged([CallerMemberName] string propertyName = "")
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
}
