using OurWeather.Models;
using System;
using System.Collections.Generic;
using System.Text;

namespace OurWeather.ViewModels
{
    public class CarouselViewModel
    {
        public List<CarouselItem> Items { get; set; } = new List<CarouselItem>();
    }
}
