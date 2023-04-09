package tdt.major.global;

import java.util.ArrayList;
import java.util.List;

import tdt.major.model.Product;

public class GlobalData {
    //tao bien toan cuc
    public static List<Product> cart;

    static {
        cart = new ArrayList<>();
    }

}
