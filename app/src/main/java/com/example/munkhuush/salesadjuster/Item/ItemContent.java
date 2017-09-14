package com.example.munkhuush.salesadjuster.Item;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ItemContent {

    private static final double TEST_PERCENTAGE = 10.15;
    private static final double TEST_PERCENTAGE_ADD = 0.5;
    private  static  String[] favoriteNames = {"Summer Sale", "Happy hour", "Extra Charge",
            "Custom Tax", "Military Discount", "State Tax", "10% Discount",
            "Custom Tax", "Military Discount" };
    /**
     * An array of sample (dummy) items.
     */
    public static final List<SaleItem> ITEMS = new ArrayList<SaleItem>();



    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, SaleItem> ITEM_MAP = new HashMap<String, SaleItem>();

    private static final int COUNT = 9;


//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
//    }

    public static void addItem(SaleItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(""+item.favoriteID, item);
    }

    private static SaleItem createDummyItem(int position) {
        SaleItem retVal ;
        if(position%2==0){
            retVal = new SaleItem(position, ""+(TEST_PERCENTAGE+0.5 *position), makeDetails(position) , "0.00", "percent");

        }else{
            retVal = new SaleItem(position, ""+(TEST_PERCENTAGE+0.5 *position), makeDetails(position) , "0.00","amount");

        }
        return retVal;
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
       // builder.append();
        return favoriteNames[position-1];
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class SaleItem {
        public final int favoriteID;
        public String favoriteAmount;
        public String favoritePercent;
        public String favoriteName;
        public String favoriteType;

        public SaleItem(int id, String favoritePercent, String theFavName, String theAmount, String favoriteType) {
            this.favoriteID = id;
            this.favoriteAmount = theAmount;
            this.favoriteName = theFavName;
            this.favoritePercent = favoritePercent;
            this.favoriteType = favoriteType;
        }

        @Override
        public String toString() {
            return favoritePercent;
        }
    }
}
