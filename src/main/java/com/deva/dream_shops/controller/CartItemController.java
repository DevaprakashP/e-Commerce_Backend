package com.deva.dream_shops.controller;

import com.deva.dream_shops.exceptions.ResourceNotFoundException;
import com.deva.dream_shops.model.Cart;
import com.deva.dream_shops.model.User;
import com.deva.dream_shops.response.ApiResponse;
import com.deva.dream_shops.service.cart.CartService;
import com.deva.dream_shops.service.cart.ICartItemService;
import com.deva.dream_shops.service.cart.ICartService;
import com.deva.dream_shops.service.user.IUserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import static org.ietf.jgss.GSSException.UNAUTHORIZED;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/cartItems")
public class CartItemController {
    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IUserService userService;

    @PostMapping("/item/add")
    public ResponseEntity<ApiResponse> addItemToCart(@RequestParam Long productId,
                                                     @RequestParam Integer quantity){
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart =  cartService.initializeNewCart(user);

            cartItemService.addItemToCart(cart.getId(),productId,quantity);
            return ResponseEntity.ok(new ApiResponse("Item Added Successfully!",null));
        } catch (ResourceAccessException e) {
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }catch (JwtException e){
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @DeleteMapping("/cart/{cartId}/item/{itemId}/remove")
    public ResponseEntity<ApiResponse> removeItemFromCart(@PathVariable Long cartId, @PathVariable  Long itemId){
        try {
            cartItemService.removeItemFromCart(cartId,itemId);
            return ResponseEntity.ok(new ApiResponse("Item Removed Successfully!",null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PutMapping("/cart/{cartId}/item/{itemId}/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId,
                                                          @PathVariable Long itemId,
                                                          @RequestParam Integer quantity){
        try {
            cartItemService.updateItemQuantity(cartId,itemId,quantity);
            return ResponseEntity.ok(new ApiResponse("Item Quantity Updated Successfully!",null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }
    }


}
