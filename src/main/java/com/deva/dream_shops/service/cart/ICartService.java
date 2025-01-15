package com.deva.dream_shops.service.cart;

import com.deva.dream_shops.model.Cart;
import com.deva.dream_shops.model.User;

import java.math.BigDecimal;

public interface ICartService {

    Cart getCart(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
