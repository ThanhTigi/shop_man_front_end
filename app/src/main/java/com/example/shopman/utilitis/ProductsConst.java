package com.example.shopman.utilitis;

import com.example.shopman.Product;
import com.example.shopman.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsConst {

    public static List<Product> totalProducts = new ArrayList<>(List.of(
            new Product("Smart Watch Pro X", "High-end smartwatch with fitness tracking, GPS, and water resistance.", "1599000", R.drawable.product_image_1, 4.7f, List.of("S", "M", "L"), "Track your workouts, monitor health metrics, and stay connected on the go."),
            new Product("Wireless Earbuds Max", "Noise-canceling wireless earbuds with charging case.", "1299000", R.drawable.product_image_2, 4.6f, List.of("Standard"), "Enjoy crystal clear sound with deep bass and long battery life."),
            new Product("Portable Mini Fan", "USB rechargeable portable fan, 3-speed settings.", "149000", R.drawable.product_image_3, 4.3f, List.of("Pink", "White", "Blue"), "Compact design ideal for travel, office, or home use."),
            new Product("Bluetooth Speaker Box", "Loud and clear sound, portable Bluetooth speaker with LED lights.", "359000", R.drawable.product_image_4, 4.5f, List.of("Black", "Red"), "Perfect for parties, picnics, or daily music lovers."),
            new Product("Gaming Mouse RGB", "Wired gaming mouse with adjustable DPI and RGB lighting.", "199000", R.drawable.product_image_5, 4.4f, List.of("Standard", "Pro"), "Designed for gamers with precision and style."),
            new Product("Y68 Smart Bracelet D20S", "Smart bracelet with Bluetooth, color touch screen, and sports tracking, compatible with Android and iOS.", "1000000", R.drawable.product_image_6, 4.5f, List.of("S", "M", "L"), "Track your activities and health easily with this sleek smart band."),
            new Product("CJ Hetbahn Korean Porridge", "Korean-style porridge series, 420g, ready-to-eat, multiple flavors.", "796000", R.drawable.product_image_7, 4.5f, List.of("420g", "500g"), "Ready-to-eat Korean porridge with multiple delicious flavors. Perfect for quick meals."),
            new Product("Mini 500 in 1 Game Console", "Mini handheld game console with 500 retro FC games, portable and nostalgic.", "970000", R.drawable.product_image_8, 4.5f, List.of("Standard", "Pro"), "A retro-style game console packed with 500 classic games, great for nostalgic players."),
            new Product("Plasma Electric Arc Lighter", "Windproof and flameless USB-rechargeable plasma arc lighter, perfect for candles and outdoor use.", "545000", R.drawable.product_image_1, 4.5f, List.of("Black", "Silver"), "Rechargeable plasma arc lighter. Windproof and safe for daily or camping use."),
            new Product("LED Desk Lamp Foldable", "Adjustable LED desk lamp with touch controls and USB charging.", "249000", R.drawable.product_image_10, 4.2f, List.of("White", "Black"), "Modern and sleek desk lamp ideal for students and working professionals."),
            new Product("USB-C 5-in-1 Hub", "Multi-port USB-C hub with HDMI, USB 3.0, and SD card reader.", "399000", R.drawable.product_image_11, 4.5f, List.of("Grey", "Silver"), "Expand your device’s connectivity with a compact and fast hub."),
            new Product("Fitness Resistance Bands Set", "Set of 5 resistance bands for workout and physical therapy.", "179000", R.drawable.product_image_12, 4.6f, List.of("Light", "Medium", "Heavy"), "Build strength and flexibility with these color-coded bands."),
            new Product("Electric Milk Frother", "Handheld battery-operated milk frother for coffee and drinks.", "99000", R.drawable.product_image_13, 4.4f, List.of("White", "Black"), "Make barista-quality foam at home in seconds."),
            new Product("Smart LED Strip Lights", "RGB LED strip with remote control and phone app support.", "269000", R.drawable.product_image_14, 4.5f, List.of("1m", "2m", "5m"), "Decorate your space with customizable lighting effects."),
            new Product("Thermal Flask Bottle 500ml", "Vacuum insulated stainless steel bottle for hot/cold drinks.", "225000", R.drawable.product_image_15, 4.7f, List.of("Blue", "Pink", "Black"), "Keeps your drink at the right temperature for hours."),
            new Product("Wireless Charging Pad", "Fast wireless charger compatible with most smartphones.", "159000", R.drawable.product_image_16, 4.3f, List.of("Black", "White"), "Slim, efficient, and cable-free charging experience."),
            new Product("Ergonomic Laptop Stand", "Adjustable and foldable laptop stand for desks.", "315000", R.drawable.product_image_17, 4.6f, List.of("Silver", "Black"), "Improve posture and cooling while working long hours."),
            new Product("Digital Alarm Clock LED", "Modern LED alarm clock with temperature and date display.", "189000", R.drawable.product_image_18, 4.2f, List.of("Wood", "Black"), "Minimalist design perfect for bedrooms and offices."),
            new Product("Multifunctional Vegetable Cutter", "Manual kitchen slicer and dicer with multiple blades.", "259000", R.drawable.product_image_19, 4.5f, List.of("Green", "Grey"), "Make cooking prep faster and cleaner."),
            new Product("Mini Tripod with Phone Holder", "Flexible mini tripod for phones and cameras.", "99000", R.drawable.product_image_20, 4.4f, List.of("Red", "Black"), "Take steady photos and videos anywhere.")
    ));

    public static List<Product> searchProductsByName(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return totalProducts;
        }
        List<Product> result = new ArrayList<>();

        for (Product product : totalProducts) {
            if (product.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                result.add(product);
            }
        }
        return result;
    }
}
