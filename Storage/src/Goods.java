import java.io.*;

public class Goods {

     String name;
     String description;
     String producer;
     int amount;
     FileWriter writer;
     int price;
     File file;


    Goods(String name, String description, String producer, int amount, int price, ProductGroup group) {
        this.name=name;
        this.description=description;
        this.producer = producer;
        this.amount=amount;
        this.price=price;
        //create a txt file with name of the product
        file = new File(group.PATH+"/"+name+".txt");
        try {
            writer = new FileWriter(file);
            writer.write(this.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(!ProductGroup.productExists(name, description, producer, price)) {
            group.files.add(file);
            group.goods.add(this);
        }
    }

    Goods(File file, ProductGroup group) {
        this.name=file.getName().substring(0, file.getName().lastIndexOf("."));
        this.amount=1; /*change later?*/
        this.price = (int) (Math.random() * 10);
        try {
            file.createNewFile();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder output = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                if(!line.contains("Amount: ")) {
                    output.append(line);
                }
            }
            String[] desc = output.toString().split("—");
            this.file=file;
            this.description=desc[0].trim();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        group.files.add(file);
    }

    public void delete(ProductGroup group) {
        for(File fileDel : group.files) {
            if(file.equals(fileDel)) {
                file.delete();
                group.files.remove(fileDel);
                break;
            }
        }
        group.goods.remove(this);
    }

    /**
     * editing goods
     *
     * this - old product;
     * @param
     * name - new name;
     * description - new description;
     * producer - new producer
     * amount, price - ...
     * @return new product*/
    public Goods editGoods(String name, String description, String producer, int amount, int price, ProductGroup group){
        //delete old item
        this.delete(group);
        Goods product = new Goods(name, description, producer, amount, price, group);

        // create a new file with a new name
        File file = new File(group.PATH+"/"+name+".txt");

        //write to file description
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(product.toString());
            writer.close();
        }
        catch (IOException e){
            System.out.println(e);
        }

        return product;
    }

    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public int getAmount() {
        return this.amount;
    }
    public String getProducer() {return this.producer;}

    public String toString(){
        return name+"; Amount: "+amount+"; Description: "+description+"; Producer: "+producer+" — $"+price;
    }
}
