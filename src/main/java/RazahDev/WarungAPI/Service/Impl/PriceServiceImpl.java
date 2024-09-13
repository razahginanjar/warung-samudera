package RazahDev.WarungAPI.Service.Impl;

import RazahDev.WarungAPI.Entity.ProductPrice;
import RazahDev.WarungAPI.Repository.ProductPriceRepository;
import RazahDev.WarungAPI.Service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {
    private final ProductPriceRepository priceRepository;
    public ProductPrice getPrice(Long priceProduct)
    {
        ProductPrice price = new ProductPrice();
        if(!priceRepository.existsByPrice(priceProduct))
        {
            price.setPrice(priceProduct);
            price.setId(UUID.randomUUID().toString());
            priceRepository.save(price);
        }else {
            price = priceRepository.findFirstByPrice(priceProduct).orElse(null);
        }
        return price;
    }
}
