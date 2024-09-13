package RazahDev.WarungAPI.Service;

import RazahDev.WarungAPI.Entity.ProductPrice;

public interface PriceService {
    ProductPrice getPrice(Long priceProduct);
}
