import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Storage {

    public static final File STORAGE_PATH = new File("./src/Products");
    public static final File SRC_PATH = new File("./src");
    //default group of products
    ProductGroup groceries = new ProductGroup("Groceries", "Water, bread, sugar, etc.");
    ProductGroup nonFood = new ProductGroup("Non-Food", "Clothes, shoes, etc.");

    static List<File> files = new ArrayList<>();
    public static List<ProductGroup> productGroups = new ArrayList<>();

    Storage(){
        STORAGE_PATH.mkdir();
    }
    /**
     * @return true if doesn't find any match
     * */
    public static boolean groupExists(List<ProductGroup> list, String name, String description) {
        return list.stream().anyMatch(o -> o.getName().equals(name));
    }

    /**
     * Creates new group if they haven`t been initialized. !Impossible situation!
     * */
    public static void findGroups(){
        File[] directories = STORAGE_PATH.listFiles();
        for(File file : Objects.requireNonNull(directories)) {
            if(!files.contains(file)) {
                new ProductGroup(file.getName(), "Some nice "+file.getName());
            }
        }
    }

    /**
     * Search a product by name
     * */
    public static List<Goods> goodsSearch(String goodName) {
        List<Goods> foundGoods = new ArrayList<>();
        Pattern myPatt = Pattern.compile("(.*)"+goodName+"(.*)", Pattern.CASE_INSENSITIVE);
        Matcher match;
        for(ProductGroup group : productGroups){
            for(Goods item : group.goods){
                match = myPatt.matcher(item.name);
                if(match.find())
                    foundGoods.add(item);
            }
        }
        if(foundGoods.size()>0)
            return foundGoods;
        else return null;
    }

    /**
     * Read Group folder for unaccounted for Goods
     * Create new groups
     * */
    public static void readDir(ProductGroup group) {
        File[] files = group.PATH.listFiles();
        for(File file : Objects.requireNonNull(files)) {
            if(!group.files.contains(file)) {
                Goods newItem = new Goods(file, group);
                group.goods.add(newItem);
            }
        }
    }
    /**
     * @return JOptionPane if none of the groups exists.
     * */
    static public void printCheckGroups(){
        try {
            if (productGroups.size()==0) throw new NullPointerException();
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "No groups exist!");
        }
    }
    /**
     * true if some groups exist
     * */
    static public boolean checkGroups(){
        return productGroups.size() > 0;
    }


}
