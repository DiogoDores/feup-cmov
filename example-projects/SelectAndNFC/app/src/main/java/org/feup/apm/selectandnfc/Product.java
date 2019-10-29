package org.feup.apm.selectandnfc;

class Product {
  final static String[] products = {
    "Oranges",
    "Mandarins",
    "Peaches",
    "Pears",
    "Apples",
    "Pineapples",
    "Plums",
    "Grapes"
  };

  String name;
  int type;
  boolean selected;

  Product(String name, int type) {
    this.name = name;
    this.type = type;
    selected = false;
  }
}
