using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Text;

namespace OurWeather.Models
{
    public class CarouselItem : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;

        public string Name { get; set; }
    }
}
