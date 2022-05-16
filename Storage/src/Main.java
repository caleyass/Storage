public class Main {

    public static void main(String[] args){
        Storage storage = new Storage();

        //Default grocery goods
        Goods milk = new Goods("Milk", "Low fat!", "Milky", 5, 12, storage.groceries);
        Goods icecream = new Goods("Ice Cream", "With chocolate!", "Bear", 8, 15, storage.groceries);
        Goods pepper = new Goods("Cayenne Pepper", "Very hot pepper, for those feeling SPICY.","Spic", 13, 5, storage.groceries);

        //default non-food goods
        Goods shampoo = new Goods("Dove", "Stunning smell, 250 ml","Dove", 2, 2, storage.nonFood);
        Goods paper = new Goods("A4", "White paper A4", "White",100, 1, storage.nonFood);
        Goods skirt = new Goods("Skirt", "Black striped mini skirt","Zara", 15, 30, storage.nonFood);
        Goods sneakers = new Goods("Sneakers", "Women`s brown sneakers", "Zara", 10, 50, storage.nonFood);

        new App();
    }

}
