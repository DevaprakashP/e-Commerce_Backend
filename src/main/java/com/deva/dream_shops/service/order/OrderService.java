package com.deva.dream_shops.service.order;

import com.deva.dream_shops.dto.OrderDto;
import com.deva.dream_shops.enums.OrderStatus;
import com.deva.dream_shops.exceptions.ResourceNotFoundException;
import com.deva.dream_shops.model.Cart;
import com.deva.dream_shops.model.Order;
import com.deva.dream_shops.model.OrderItem;
import com.deva.dream_shops.model.Product;
import com.deva.dream_shops.repository.OrderRepository;
import com.deva.dream_shops.repository.ProductRepository;
import com.deva.dream_shops.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order,cart);
        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(cart.getId());
        return savedOrder;
    }

    private Order createOrder(Cart cart){
        Order order = new Order();
        //Set the user ...
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart){
        return cart.getCartItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            product.setInventory(product.getInventory()-cartItem.getQuantity());
            return new OrderItem(
                    order,
                    product,
                    cartItem.getQuantity(),
                    product.getPrice());
        }).toList();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList){
        return orderItemList
                .stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId).map(this :: convertToDto)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId){
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this :: convertToDto).toList();
    }

    @Override
    public OrderDto convertToDto(Order order){
        return modelMapper.map(order,OrderDto.class);
    }
}
