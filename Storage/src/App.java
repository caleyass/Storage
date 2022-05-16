import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class App extends JFrame {
    JPanel mainMenu, groups, searchWin;
    JButton prodGroups, prodOutput, prodSearch, back;

    App(){
        //Main menu
        super("Storage App");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Read directory and files in them
        Storage.findGroups();
        for(ProductGroup group : Storage.productGroups) {
            Storage.readDir(group);
        }
        //Build app
        mainMenu = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        JTextArea welcomeText = new JTextArea();
        JScrollPane scroller = new JScrollPane(welcomeText);
        scroller.setVisible(true);
        String startMsg = "Welcome! \n" +
                "This program operates a Storage system, utilizing files and directories.";
        String authors = "\n\nProgram by Khomenko Max & Petrova Olesya";
        welcomeText.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        welcomeText.setEditable(false);
        welcomeText.setSize(new Dimension(650, 250));
        welcomeText.append(startMsg);
        welcomeText.append(authors);

        //STORAGE
        prodGroups = new JButton(new AbstractAction("Product Storage") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainMenu.setVisible(false);
                JLabel groupText = new JLabel("STORAGE");
                groupText.setFont(new Font("Sans Serif", Font.BOLD, 18));
                groupText.setVerticalAlignment(SwingConstants.TOP);
                groupText.setHorizontalAlignment(SwingConstants.CENTER);
                groups = new JPanel(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                JPanel actButtons = new JPanel();
                back = new JButton(new AbstractAction("Back") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        backToMenu(groups);
                    }
                });
                actButtons.add(back, BorderLayout.SOUTH);
                c.anchor = GridBagConstraints.NORTH;
                c.gridx = 0;
                c.gridy = 0;
                groups.add(groupText, c);
                ProductGroup[] prodGroups = Storage.productGroups.toArray(new ProductGroup[0]);
                DefaultComboBoxModel<ProductGroup> model = new DefaultComboBoxModel<>(prodGroups);
                JComboBox<ProductGroup> productList = new JComboBox<>(model);
                productList.setSize(new Dimension(250, 24));
                JPanel product = new JPanel();
                JPanel groupButtons = new JPanel();
                JButton printAllGoods = new JButton(
                        new AbstractAction("List All Products") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Storage.printCheckGroups();
                                if (Storage.checkGroups()) {
                                    ProductGroup group = (ProductGroup) model.getSelectedItem();
                                    File prodList = new File(Storage.SRC_PATH+"/Statistics/"+group.getName()+" Products.txt");
                                    if(prodList.exists())
                                        prodList.delete();

                                    try {
                                        BufferedWriter writer = new BufferedWriter(new FileWriter(prodList));
                                        ArrayList<Goods> goodsList = new ArrayList<>(group.goods);
                                        goodsList.sort(new Comparator<Goods>() {
                                            @Override
                                            public int compare(Goods o1, Goods o2) {
                                                return o1.getName().compareTo(o2.getName());
                                            }
                                        });
                                        for(Goods product : goodsList) {
                                            writer.write(product.toString() + "\n");
                                        }
                                        writer.close();
                                        Desktop.getDesktop().open(prodList);
                                    } catch (IOException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }
                        }
                );
                JButton printTotalCost = new JButton(new AbstractAction("List Total Product Cost") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Storage.printCheckGroups();
                        if (Storage.checkGroups()) {
                            ProductGroup selectedGroup = (ProductGroup) productList.getSelectedItem();
                            File totalCostGroup = new File(Storage.SRC_PATH+"/Statistics/"+selectedGroup.getName()+" Pricing.txt");
                            if(totalCostGroup.exists())
                                totalCostGroup.delete();
                            int totalCost = 0;
                            ProductGroup group = (ProductGroup) model.getSelectedItem();
                            BufferedWriter writer = null;
                            try {
                                writer = new BufferedWriter(new FileWriter(totalCostGroup, true));
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            for (Goods product : group.goods) {
                                try {
                                    writer.write(product.name + " — $" + (product.amount * product.price) + "\n");
                                    totalCost += (product.amount * product.price);
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                                try {
                                    writer.write("\nTOTAL: $"+totalCost);
                                    writer.close();
                                } catch (IOException ex) {
                                    throw new RuntimeException(ex);
                                }
                            try {
                                Desktop.getDesktop().open(totalCostGroup);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                });
                JPanel allProdButtons = new JPanel();
                JButton showProduct;
                //Browse Goods in ProductGroup
                    showProduct = new JButton(new AbstractAction("Browse Group Products") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Storage.printCheckGroups();
                            if (Storage.checkGroups()) {
                            ProductGroup selectedGroup = (ProductGroup) productList.getSelectedItem();
                            assert selectedGroup != null;
                            JFrame products;
                            JLabel goodsText;
                            goodsText = new JLabel(Objects.requireNonNull(selectedGroup.getName().toUpperCase()));

                            products = new JFrame(Objects.requireNonNull(selectedGroup.getName().toUpperCase()));


                            goodsText.setFont(new Font("Sans Serif", Font.BOLD, 18));
                            goodsText.setVerticalAlignment(SwingConstants.TOP);
                            goodsText.setHorizontalAlignment(SwingConstants.CENTER);
                            Goods[] groupGoods = selectedGroup.goods.toArray(new Goods[0]);
                            DefaultComboBoxModel<Goods> model = new DefaultComboBoxModel<>(groupGoods);
                            JComboBox<Goods> goodsList = new JComboBox<>(model);
                            Goods selectedItem = (Goods) goodsList.getSelectedItem();
                            assert selectedItem != null;
                            JPanel goodsBox = new JPanel();
                            JPanel goodsMenu = new JPanel(new GridBagLayout());
                            GridBagConstraints c = new GridBagConstraints();

                            //Closes window
                            JButton back = new JButton(new AbstractAction("Close") {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    products.dispose();
                                }
                            });
                            products.add(goodsMenu);
                            JPanel prodButtons = new JPanel();
                            //Add product
                            JButton add = new JButton(new AbstractAction("Add Product") {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    JFrame make = new JFrame("Adding Product");
                                    JPanel mainWin = new JPanel(new GridLayout(6, 2));

                                    mainWin.add(new JLabel("Product Name: "));
                                    JTextField prodName = new JTextField();
                                    prodName.setSize(100, 20);
                                    mainWin.add(prodName);

                                    mainWin.add(new JLabel("Description: "));
                                    JTextField prodDesc = new JTextField();
                                    prodDesc.setSize(100, 20);
                                    mainWin.add(prodDesc);

                                    mainWin.add(new JLabel("Producer: "));
                                    JTextField prodProduc = new JTextField();
                                    prodProduc.setSize(100, 20);
                                    mainWin.add(prodProduc);

                                    NumberFormat format = NumberFormat.getInstance();
                                    NumberFormatter formatter = new NumberFormatter(format);
                                    formatter.setValueClass(Integer.class);
                                    formatter.setMinimum(0);
                                    formatter.setMaximum(Integer.MAX_VALUE);
                                    formatter.setAllowsInvalid(false);
                                    mainWin.add(new JLabel("Amount: "));
                                    JFormattedTextField prodNum = new JFormattedTextField(formatter);
                                    prodNum.setSize(100, 20);
                                    mainWin.add(prodNum);

                                    mainWin.add(new JLabel("Price Per Unit: "));
                                    JFormattedTextField prodCost = new JFormattedTextField(formatter);
                                    prodCost.setSize(100, 20);
                                    mainWin.add(prodCost);

                                    JButton confirm = new JButton(new AbstractAction("Confirm") {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String name = prodName.getText();
                                            String description = prodDesc.getText();
                                            String producer = prodProduc.getText();
                                            String amountStr = prodNum.getText().replaceAll("[^\\d.]", "");
                                            int amount = Integer.parseInt(amountStr);
                                            String priceStr = prodCost.getText().replaceAll("[^\\d.]", "");
                                            int price = Integer.parseInt(priceStr);
                                            //Proceeds if all fields are filled with correct values
                                            if (!name.equals("") && !description.equals("") && !producer.equals("") && amount != 0 && price != 0) {
                                                boolean itemExists = false;
                                                //Check if product exists in any other group
                                                for (ProductGroup ignored : Storage.productGroups) {
                                                    if (ProductGroup.productExists(name, description, producer, price)) {
                                                        Toolkit.getDefaultToolkit().beep();
                                                        JOptionPane.showMessageDialog(null, "Product " + name + " already exists!", "Error", JOptionPane.INFORMATION_MESSAGE);
                                                        itemExists = true;
                                                        break;
                                                    }
                                                }
                                                if (!itemExists) {
                                                    Goods product = new Goods(name, description, producer, amount, price, selectedGroup);
                                                    model.addElement(product);
                                                    JOptionPane.showMessageDialog(null, "Successfully added new " + product.getName() + "!", "Message", JOptionPane.INFORMATION_MESSAGE);
                                                    make.dispose();
                                                }
                                            } else {
                                                Toolkit.getDefaultToolkit().beep();
                                                JOptionPane.showMessageDialog(null, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    });
                                    mainWin.add(confirm);
                                    make.add(mainWin);
                                    make.setVisible(true);
                                    make.setSize(400, 500);

                                }
                            });
                            //Edit product
                            JButton edit = new JButton(new AbstractAction("Edit Selected Product") {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Goods selectedItem = (Goods) goodsList.getSelectedItem();
                                    if(selectedItem == null)
                                        JOptionPane.showMessageDialog(null, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
                                    else {
                                        JFrame edit = new JFrame("Editing Product");
                                        JPanel mainWin = new JPanel(new GridLayout(5, 2));
                                        mainWin.add(new JLabel("Product Name: "));
                                        JTextField prodName = new JTextField(selectedItem.getName());
                                        prodName.setSize(100, 20);
                                        mainWin.add(prodName);

                                        mainWin.add(new JLabel("Description: "));
                                        JTextField prodDesc = new JTextField(selectedItem.getDescription());
                                        prodDesc.setSize(100, 20);
                                        mainWin.add(prodDesc);

                                        mainWin.add(new JLabel("Producer: "));
                                        JTextField prodProduc = new JTextField(selectedItem.getProducer());
                                        prodProduc.setSize(100, 20);
                                        mainWin.add(prodProduc);

                                        NumberFormat format = NumberFormat.getInstance();
                                        NumberFormatter formatter = new NumberFormatter(format);
                                        formatter.setValueClass(Integer.class);
                                        formatter.setMinimum(0);
                                        formatter.setMaximum(Integer.MAX_VALUE);
                                        formatter.setAllowsInvalid(false);

                                        mainWin.add(new JLabel("Price Per Unit (Currently $"+selectedItem.price+"): "));
                                        JFormattedTextField prodCost = new JFormattedTextField(formatter);
                                        prodCost.setSize(100, 20);
                                        mainWin.add(prodCost);


                                        JButton confirm = new JButton(new AbstractAction("Confirm") {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                String name = prodName.getText();
                                                String description = prodDesc.getText();
                                                String producer = prodProduc.getText();
                                                String priceStr = prodCost.getText().replaceAll("[^\\d.]", "");
                                                int price = Integer.parseInt(priceStr);
                                                //Proceeds if all fields are filled with correct values
                                                if (!name.equals("") && !description.equals("") && !producer.equals("") && price != 0) {
                                                    boolean itemExists = false;
                                                    //Check if product exists in any other group
                                                    for (ProductGroup ignored : Storage.productGroups) {
                                                        if (ProductGroup.productExists(name, description, producer, price)) {
                                                            Toolkit.getDefaultToolkit().beep();
                                                            JOptionPane.showMessageDialog(null, "Product " + name + " already exists!", "Error", JOptionPane.INFORMATION_MESSAGE);
                                                            itemExists = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!itemExists) {
                                                        Goods newItem = selectedItem.editGoods(name, description, producer, selectedItem.amount, price, selectedGroup);
                                                        model.addElement(newItem);
                                                        model.removeElement(selectedItem);
                                                        JOptionPane.showMessageDialog(null, "Successfully edited " + name + "!", "Message", JOptionPane.INFORMATION_MESSAGE);
                                                        edit.dispose();
                                                    }
                                                } else {
                                                    Toolkit.getDefaultToolkit().beep();
                                                    JOptionPane.showMessageDialog(null, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                                                }
                                            }
                                        });
                                        mainWin.add(confirm);
                                        edit.add(mainWin);
                                        edit.setVisible(true);
                                        edit.setSize(400, 500);
                                    }
                                }
                            });
                            //Delete product
                            JButton delete = new JButton(new AbstractAction("Delete Selected Product") {
                                @Override
                                public void actionPerformed(ActionEvent e) {

                                    Goods selectedItem = (Goods) goodsList.getSelectedItem();
                                    if(selectedItem == null)
                                        JOptionPane.showMessageDialog(null, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
                                    else {
                                        int index = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + selectedItem.getName() + "?");
                                        if (index == JOptionPane.YES_OPTION) {
                                            selectedItem.delete(selectedGroup);
                                            model.removeElement(selectedItem);
                                            JOptionPane.showMessageDialog(null, "Successfully deleted " + selectedItem.getName() + ".", "Message", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }
                                }
                            });

                            JPanel goodsButtons = new JPanel();
                            //Adds given amount of product (returns error JOptionPane if input isn't an integer)
                            JButton procurement = new JButton(new AbstractAction("Procure") {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Goods selectedItem = (Goods) goodsList.getSelectedItem();
                                    if (selectedItem == null)
                                        JOptionPane.showMessageDialog(null, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
                                    else {
                                        String entry = JOptionPane.showInputDialog(null, "Input amount to procure (currently " + selectedItem.getAmount() + ")");
                                        if (entry.matches("\\d+")) {
                                            int procuredNum = Integer.parseInt(entry);
                                            selectedItem.amount += procuredNum;
                                            JOptionPane.showMessageDialog(null, "Successfully procured " + procuredNum + " more " + selectedItem.getName() + "!", "Message", JOptionPane.INFORMATION_MESSAGE);
                                        } else {
                                            Toolkit.getDefaultToolkit().beep();
                                            JOptionPane.showMessageDialog(null, "Integer input required!", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                }
                            });
                            //Removes given amount of product (returns error JOptionPane if an input outside range [0; good.getAmount()] is entered, OR if input is not integer)
                            JButton distribution = new JButton(new AbstractAction("Distribute") {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Goods selectedItem = (Goods) goodsList.getSelectedItem();
                                    if (selectedItem == null)
                                        JOptionPane.showMessageDialog(null, "No item selected!", "Error", JOptionPane.ERROR_MESSAGE);
                                    else {
                                        String entry = JOptionPane.showInputDialog(null, "Input amount to distribute (currently " + selectedItem.getAmount() + ")");
                                        if (entry.matches("\\d+")) {
                                            int distNum = Integer.parseInt(entry);
                                            if (distNum <= selectedItem.getAmount()) {
                                                selectedItem.amount -= distNum;
                                                JOptionPane.showMessageDialog(null, "Successfully distributed " + distNum + " units of " + selectedItem.getName() + "! " + selectedItem.amount + " remain.", "Message", JOptionPane.INFORMATION_MESSAGE);
                                            } else {
                                                Toolkit.getDefaultToolkit().beep();
                                                JOptionPane.showMessageDialog(null, "Not enough units to distribute!", "Error", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else {
                                            Toolkit.getDefaultToolkit().beep();
                                            JOptionPane.showMessageDialog(null, "Integer input required!", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }
                                }
                            });

                            c.anchor = GridBagConstraints.NORTH;
                            c.gridx = 0;
                            c.gridy = 0;
                            goodsMenu.add(goodsText, c);

                            prodButtons.add(add);
                            prodButtons.add(edit);
                            prodButtons.add(delete);
                            c.anchor = GridBagConstraints.NORTH;
                            c.weighty = 0.1;
                            c.gridy = 1;
                            goodsMenu.add(prodButtons, c);

                            goodsBox.add(goodsList);
                            c.anchor = GridBagConstraints.CENTER;
                            c.gridy = 2;
                            goodsMenu.add(goodsBox, c);

                            goodsButtons.add(procurement);
                            goodsButtons.add(distribution);
                            c.anchor = GridBagConstraints.SOUTH;
                            c.gridy = 3;
                            goodsMenu.add(goodsButtons, c);

                            JPanel backButton = new JPanel();
                            backButton.add(back, BorderLayout.SOUTH);
                            c.anchor = GridBagConstraints.SOUTH;
                            c.gridy = 4;
                            goodsMenu.add(backButton, c);

                            products.setSize(700, 300);
                            products.setVisible(true);
                        }
                    }});

                //Create new group
                JButton add = new JButton(new AbstractAction("Add New Group") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFrame addGroup = new JFrame("New Product Group");
                        JPanel create = new JPanel(new GridLayout(3, 2));

                        create.add(new JLabel("Name: "));
                        JTextField groupName = new JTextField();
                        groupName.setPreferredSize(new Dimension(100, 24));
                        create.add(groupName);

                        create.add(new JLabel("Description: "));
                        JTextField groupDesc = new JTextField();
                        groupDesc.setPreferredSize(new Dimension(100, 24));
                        create.add(groupDesc);

                        JButton confirm = new JButton(new AbstractAction("Create Group") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (groupName.getText() == null || groupDesc.getText() == null) {
                                    JOptionPane.showMessageDialog(null, "Missing name or description!", "Error", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    if (!Storage.groupExists(Storage.productGroups, groupName.getText(), groupDesc.getText())) {
                                        ProductGroup newGroup = new ProductGroup(groupName.getText(), groupDesc.getText());
                                        if (model.getIndexOf(newGroup) == -1)
                                            model.addElement(newGroup);
                                        JOptionPane.showMessageDialog(null, "Successfully created " + newGroup.name + "!", "Message", JOptionPane.INFORMATION_MESSAGE);
                                        addGroup.dispose();
                                    } else {
                                        Toolkit.getDefaultToolkit().beep();
                                        JOptionPane.showMessageDialog(null, "Group " + groupName.getText() + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        });
                        create.add(confirm);
                        addGroup.add(create);
                        addGroup.pack();
                        addGroup.setSize(400, 150);
                        addGroup.setVisible(true);
                    }
                });
                //Edit existing group
                JButton edit = new JButton(new AbstractAction("Edit Selected Group") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Storage.printCheckGroups();
                        if (Storage.checkGroups()) {
                            JFrame editGroup = new JFrame("Edit Product Group");
                            JPanel edit = new JPanel(new GridLayout(3, 2));

                            edit.add(new JLabel("Name: "));
                            JTextField groupName = new JTextField();
                            groupName.setPreferredSize(new Dimension(100, 24));
                            edit.add(groupName);

                            edit.add(new JLabel("Description: "));
                            JTextField groupDesc = new JTextField();
                            groupDesc.setPreferredSize(new Dimension(100, 24));
                            edit.add(groupDesc);

                            JButton confirm = new JButton(new AbstractAction("Finish Edits") {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (groupName.getText() == null || groupDesc.getText() == null) {
                                        JOptionPane.showMessageDialog(null, "Missing name or description!", "Error", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        if (!Storage.groupExists(Storage.productGroups, groupName.getText(), groupDesc.getText())) {
                                            ProductGroup newGroup = null;
                                            try {
                                                for (ProductGroup group : Storage.productGroups) {
                                                    ProductGroup g = (ProductGroup) productList.getSelectedItem();
                                                    assert g != null;
                                                    if (g.getName().equals(group.getName())) {
                                                        // find from productGroups selected group and change it!
                                                        newGroup = group.editGroup(groupName.getText(), groupDesc.getText());
                                                        model.removeElement(g);
                                                    }
                                                }
                                            } catch (ConcurrentModificationException ex) {
                                                ex.printStackTrace();
                                            }
                                            if (model.getIndexOf(newGroup) == -1)
                                                model.addElement(newGroup);
                                            JOptionPane.showMessageDialog(null, "Successfully edited " + Objects.requireNonNull(newGroup).name + "!", "Message", JOptionPane.INFORMATION_MESSAGE);
                                            editGroup.dispose();
                                        } else {
                                            Toolkit.getDefaultToolkit().beep();
                                            JOptionPane.showMessageDialog(null, "Group " + groupName.getText() + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                                        }
                                    }

                                }
                            });
                            edit.add(confirm);
                            editGroup.add(edit);
                            editGroup.pack();
                            editGroup.setSize(300, 200);
                            editGroup.setVisible(true);
                        }
                    }
                });
                //Delete existing group
                JButton delete = new JButton(new AbstractAction("Remove Selected Group") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Storage.printCheckGroups();
                        if (Storage.checkGroups()) {
                            ProductGroup delGroup = (ProductGroup) productList.getSelectedItem();
                            assert delGroup != null;
                            int index = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + delGroup.getName() + "?");
                            if (index == JOptionPane.YES_OPTION) {
                                delGroup.deleteGroup();
                                model.removeElement(delGroup);
                            }
                        }
                    }
                });
                groupButtons.add(add);
                groupButtons.add(edit);
                groupButtons.add(delete);
                product.add(productList);
                product.add(showProduct);
                allProdButtons.add(printAllGoods);
                allProdButtons.add(printTotalCost);
                c.anchor = GridBagConstraints.NORTH;
                c.gridy = 1;
                c.weighty = 0.1;
                c.weightx = 0;
                groups.add(groupButtons, c);
                c.anchor = GridBagConstraints.CENTER;
                c.gridy = 2;
                groups.add(product, c);
                c.anchor = GridBagConstraints.SOUTH;
                c.gridy = 4;
                groups.add(allProdButtons, c);
                c.anchor = GridBagConstraints.SOUTH;
                c.gridy = 5;
                groups.add(actButtons, c);
                add(groups);
                groups.setVisible(true);
            }
        });

        //OUTPUT
        prodOutput = new JButton(new AbstractAction("Product Output") {
            @Override
            public void actionPerformed(ActionEvent e) {
                final File PRICING_PATH = new File(Storage.SRC_PATH+"/Statistics/Pricing.txt");
                if(PRICING_PATH.exists())
                    PRICING_PATH.delete();
                final File OUTPUT_PATH = new File(Storage.SRC_PATH+"/Statistics/Product List.txt");
                if(OUTPUT_PATH.exists())
                    OUTPUT_PATH.delete();

                JFrame output = new JFrame("Product Output");
                JPanel outputWin = new JPanel(new BorderLayout());
                JLabel prodText = new JLabel("OUTPUT");
                prodText.setFont(new Font("Sans Serif", Font.BOLD, 18));
                prodText.setVerticalAlignment(SwingConstants.TOP);
                prodText.setHorizontalAlignment(SwingConstants.CENTER);
                JPanel outButtons = new JPanel();
                back = new JButton(new AbstractAction("Close") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        output.dispose();
                    }
                });

                final String[] columns = {"Product Group", "Product Name", "Product Producer", "Product Amount", "Cost Per Unit", "Total Cost"};

                //Determine how many rows in table
                ProductGroup[] groups = Storage.productGroups.toArray(new ProductGroup[0]);
                int rows = 0;
                for (ProductGroup group : groups) {
                    rows += group.goods.size();
                }

                ArrayList<Goods> goodsList = new ArrayList<>();
                String[][] data = new String[rows][6];
                int row = 0;
                for (ProductGroup group : groups) {
                    int groupCost = 0;
                    Goods[] goods = group.goods.toArray(new Goods[0]);
                    for (Goods good : goods) {
                        data[row][0] = group.getName().toUpperCase();
                        data[row][1] = good.getName();
                        data[row][2] = good.getProducer();
                        data[row][3] = String.valueOf(good.getAmount());
                        data[row][4] = String.valueOf(good.price);
                        data[row][5] = String.valueOf(good.amount * good.price);
                        groupCost += (good.amount * good.price);
                        row++;
                        goodsList.add(good);
                    }
                    try {
                        BufferedWriter costWriter = new BufferedWriter(new FileWriter(PRICING_PATH, true));
                        costWriter.write("TOTAL FOR "+group.getName().toUpperCase()+": $"+groupCost+"\n");
                        costWriter.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    Collections.sort(goodsList, new Comparator<Goods>() {
                        @Override
                        public int compare(Goods o1, Goods o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    try {
                        BufferedWriter prodWriter = new BufferedWriter(new FileWriter(OUTPUT_PATH));
                        for(Goods item : goodsList)
                            prodWriter.write(item +"\n");
                        prodWriter.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                JTable table = new JTable(data, columns);
                table.setModel(new DefaultTableModel(data, columns){
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                table.setBounds(30, 40, 600, 400);
                JScrollPane scroller = new JScrollPane(table);
                outButtons.add(back, BorderLayout.SOUTH);
                outputWin.add(outButtons, BorderLayout.SOUTH);
                outputWin.add(prodText);
                outputWin.add(scroller);
                JButton prices = new JButton(new AbstractAction("Total Pricing Per Group") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Desktop.getDesktop().open(PRICING_PATH);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        catch (IllegalArgumentException ex2){}
                    }
                });
                JButton products = new JButton(new AbstractAction("Alphabetic Product List") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            Desktop.getDesktop().open(OUTPUT_PATH);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        catch (IllegalArgumentException ex1){}
                    }
                });
                outButtons.add(prices, BorderLayout.SOUTH);
                outButtons.add(products, BorderLayout.SOUTH);
                output.setSize(700, 500);
                output.add(outputWin);
                output.setVisible(true);
            }
        });

        //SEARCH
        prodSearch = new JButton(new AbstractAction("Product Search") {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainMenu.setVisible(false);
                searchWin = new JPanel(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                JLabel srcText = new JLabel("SEARCH");
                JTextField srcField = new JTextField();
                srcField.setPreferredSize(new Dimension(200, 24));
                srcText.setFont(new Font("Sans Serif", Font.BOLD, 18));
                srcText.setVerticalAlignment(SwingConstants.TOP);
                srcText.setHorizontalAlignment(SwingConstants.CENTER);
                JPanel srcButtons = new JPanel();
                //Executes search for product; opens .txt file of product if found, else opens an error JOptionPane
                JButton search = new JButton(new AbstractAction("Search") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        List<Goods> foundGoods = Storage.goodsSearch(srcField.getText());
                        List<File> files = new ArrayList<>();
                        Desktop desktop = Desktop.getDesktop();


                        if(foundGoods!=null)         //checks file exists or not
                        {
                            /*try {
                                JOptionPane.showMessageDialog(null, "Product "+srcField.getText()+" found!", "Message", JOptionPane.INFORMATION_MESSAGE);
                                desktop.open(file);              //opens the specified file
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }*/
                            JFrame found = new JFrame();
                            found.setSize(500, 400);
                            found.setVisible(true);
                            found.setLayout(new GridLayout(2, 1));

                            Goods[] foundGoodsArr = foundGoods.toArray(new Goods[0]);
                            DefaultComboBoxModel<Goods> model = new DefaultComboBoxModel<>(foundGoodsArr);
                            JComboBox<Goods> foundGoodsModel = new JComboBox<>(model);
                            foundGoodsModel.setSize(new Dimension(100, 24));
                            found.add(foundGoodsModel);

                            JButton open = new JButton("Open file");
                            open.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Goods good = (Goods) foundGoodsModel.getSelectedItem();
                                    try {
                                        assert good != null;
                                        desktop.open(good.file);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });

                            found.add(open);
                            foundGoods.forEach(g -> files.add(g.file));

                        }
                        else {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(null, "Product "+srcField.getText()+" doesn't exist!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                //Return to main menu
                back = new JButton(new AbstractAction("Back") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        backToMenu(searchWin);
                    }
                });
                constraints.fill = GridBagConstraints.SOUTH;
                constraints.gridx = 1;
                constraints.gridy = 0;
                searchWin.add(search,constraints);
                srcButtons.add(back, BorderLayout.SOUTH);
                srcButtons.add(search, BorderLayout.SOUTH);
                constraints.fill = GridBagConstraints.NORTH;
                constraints.gridx = 0;
                constraints.gridy = 0;
                searchWin.add(srcText, constraints);
                constraints.fill = GridBagConstraints.CENTER;
                constraints.gridx = 0;
                constraints.gridy = 1;
                searchWin.add(srcField, constraints);
                constraints.fill = GridBagConstraints.SOUTH;
                constraints.gridx = 0;
                constraints.gridy = 4;
                searchWin.add(srcButtons, constraints);
                add(searchWin);
                searchWin.setVisible(true);
            }
        });

        buttons.add(prodGroups);
        buttons.add(prodOutput);
        buttons.add(prodSearch);
        mainMenu.add(buttons, BorderLayout.SOUTH);
        mainMenu.add(scroller, BorderLayout.CENTER);

        add(mainMenu);
        setVisible(true);
        pack();
        this.setSize(new Dimension(700, 300));
    }
    /**
     * Returns to main menu (with message)
     * */
    private void backToMenu(JPanel currentMenu) {
        currentMenu.setVisible(false);
        mainMenu = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel();
        JTextArea welcomeText = new JTextArea();
        JScrollPane scroller = new JScrollPane(welcomeText);
        scroller.setVisible(true);
        String startMsg = "Welcome! \n" +
                "This program operates a Storage system, utilizing files and directories.";
        String authors = "\n\nProgram by Khomenko Max & Petrova Olesya";
        welcomeText.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        welcomeText.setEditable(false);
        welcomeText.setSize(new Dimension(650, 250));
        welcomeText.append(startMsg);
        welcomeText.append(authors);

        buttons.add(prodGroups);
        buttons.add(prodOutput);
        buttons.add(prodSearch);
        mainMenu.add(buttons, BorderLayout.SOUTH);
        mainMenu.add(scroller, BorderLayout.CENTER);

        add(mainMenu);
        setVisible(true);
    }

    private void openFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
        }
        catch (FileNotFoundException e){
            new JOptionPane("Файл не знайдено!");
        }

    }
}