package RazahDev.WarungAPI.DTO.Transaction;

import lombok.Builder;

@Builder
public record GetTotalSalesResponse(Long eatIn, Long takeAway, Long Online) {
}
