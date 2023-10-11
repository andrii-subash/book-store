package book.store.service.impl;

import book.store.dto.order.OrderItemResponseDto;
import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.ShippingAdressRequestDto;
import book.store.dto.order.UpdatedStatusRequestDto;
import book.store.mapper.OrderItemMapper;
import book.store.mapper.OrderMapper;
import book.store.model.CartItem;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.CartItemRepository;
import book.store.repository.OrderItemRepository;
import book.store.repository.OrderRepository;
import book.store.repository.ShoppingCartRepository;
import book.store.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderResponseDto createOrder(String username, ShippingAdressRequestDto requestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findShoppingCartByUserEmail(username);
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new RuntimeException("Your shopping cart is empty!");
        }
        Order savedOrder = getOrderFromDb(shoppingCart.getUser(), requestDto);
        Set<OrderItem> orderItems = new HashSet<>();
        double total = 0;
        for (CartItem cartItem : shoppingCart.getCartItems()) {
            OrderItem orderItem = getOrderItemFromDb(cartItem, savedOrder);
            orderItems.add(orderItem);
            total += orderItem.getPrice().doubleValue() * orderItem.getQuantity();
            cartItemRepository.delete(cartItem);
        }
        shoppingCart.setCartItems(Collections.emptySet());
        shoppingCartRepository.save(shoppingCart);

        savedOrder.setTotal(BigDecimal.valueOf(total));
        savedOrder.setOrderItems(orderItems);
        return orderMapper.toDto(orderRepository.save(savedOrder));
    }

    @Override
    public Set<OrderResponseDto> getAllOrdersByUserEmail(String username) {
        return orderRepository.findAllByUserEmail(username).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, UpdatedStatusRequestDto requestDto) {
        Order order = orderRepository.getReferenceById(orderId);
        order.setStatus(Order.Status.valueOf(requestDto.getStatus()));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Set<OrderItemResponseDto> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId).stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public OrderItemResponseDto getOrderItemById(Long orderItemId) {
        return orderItemMapper.toDto(orderItemRepository.getReferenceById(orderItemId));
    }

    @Override
    public void doesUserHasThisOrder(String username, Long orderId) {
        if (!orderRepository.getReferenceById(orderId).getUser().getEmail().equals(username)) {
            throw new RuntimeException("You don`t have such an order!");
        }
    }

    private Order getOrderFromDb(User user, ShippingAdressRequestDto requestDto) {
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.Status.PENDING);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setTotal(BigDecimal.ZERO);
        return orderRepository.save(order);
    }

    private OrderItem getOrderItemFromDb(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setBook(cartItem.getBook());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getBook().getPrice());
        orderItem.setOrder(order);
        return orderItemRepository.save(orderItem);
    }
}
