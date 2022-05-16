import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ProductGroup {
    // txt files in a folder
    protected List<File> files = new ArrayList<>();

    // default information
    protected String name;
    protected String description;

    //set of products in a group
    public List<Goods> goods;

    public File PATH;

    public ProductGroup(String name, String description) {
        PATH = new File(Storage.STORAGE_PATH + "/"+name);
        PATH.mkdir();

        if(!Storage.groupExists(Storage.productGroups, name, description)) {
            Storage.productGroups.add(this);
            Storage.files.add(PATH);
        }

        goods = new ArrayList<>();

        this.name = name;
        this.description = description;
    }

    /**
     * Prints list of goods to console
     */
    public void groceryList(List<Goods> goods) {
        goods.forEach(good -> System.out.println(good.toString()));
    }

    /**
     * Повне видалення групи
     * */
    public void deleteGroup() {
        //видаляє всі файли в каталозі
        for(File file : files){
            file.delete();
        }
        File folder = this.PATH;
        //delete file from Storage.files
        for(File fileDel : Storage.files) {
            if(folder.equals(fileDel))
                fileDel.delete();
        }
        Storage.productGroups.remove(this);
        String path = Storage.SRC_PATH +"/Statistics";
        File folderStatistics = new File(path);
        String name = this.getName();
        for(File file : Objects.requireNonNull(folderStatistics.listFiles())){
            if(file.getName().equals( name+" Pricing.txt") || file.getName().equals(name+" Products.txt")){
                file.delete();
            }
        }
    }

    /**
     * editing group
     * this - old group;
     * name - new name;
     * description - new description;
     * @return new group*/
    public ProductGroup editGroup(String name, String description) {
        this.deleteGroup();
        ProductGroup group = new ProductGroup(name, description);
        group.goods = goods;

        //create new txt files in a new folder
        for(Goods good : goods){
            File file = new File(group.PATH+"/"+good.getName()+".txt");
            group.files.add(file);
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(good.description+" — "+"$"+good.price);
                writer.close();
            }
            catch (IOException e){
                System.out.println(e);
            }
        }
        return group;
    }
    /**
     * Check if product exists by name and description in all groups
     * @return true if product already exists
     * */
    public static boolean productExists(String name, String description, String producer, int price){
        return Storage.productGroups
                .stream()
                .flatMap(group -> group.goods.stream())
                .anyMatch(p -> p.getName().equals(name));
    }


    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return name+" — "+description;
    }

}
