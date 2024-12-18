package com.kennedy.demo_park_api.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingUtils {
    
    private static final double FIRST_15_MINUTES = 5.00;
    private static final double FIRST_60_MINUTES = 9.25;
    private static final double ADDITIONAL_15_MINUTES = 1.75;
    private static final double DISCOUNT = 0.30;
    
    public static String generateReceipt(){
        LocalDateTime date = LocalDateTime.now();
        String receipt = date.toString().substring(0,19);

        return receipt
                .replace("-", "")
                .replace(":", "")
                .replace("T","-");
    }



    public static BigDecimal calculatePrice(LocalDateTime entrada, LocalDateTime saida) {
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {

            total += FIRST_15_MINUTES;

        } else if (minutes <= 60) {

            total += FIRST_60_MINUTES;

        } else {

            total += FIRST_60_MINUTES;
            minutes -= 60;

            while (minutes > 0){
                total += ADDITIONAL_15_MINUTES;
                minutes -= 15;
            }

        }

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calculateDiscount(BigDecimal price, long numberOfTimes) {

        // Complete o código com a sua lógica
        BigDecimal discount = new BigDecimal(0);
        if(numberOfTimes > 0 && numberOfTimes%10 == 0){

            discount =
                    price.multiply(
                            new BigDecimal(DISCOUNT)
                    );
        }


        return discount.setScale(2, RoundingMode.HALF_EVEN);
    }
}
