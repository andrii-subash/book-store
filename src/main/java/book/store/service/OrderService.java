package book.store.service;

import book.store.dto.order.OrderItemResponseDto;
import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.ShippingAdressRequestDto;
import book.store.dto.order.UpdatedStatusRequestDto;
import java.util.Set;

public interface OrderService {
    OrderResponseDto createOrder(String username, ShippingAdressRequestDto requestDto);

    Set<OrderResponseDto> getAllOrdersByUserEmail(String username);

    OrderResponseDto updateOrderStatus(Long orderId, UpdatedStatusRequestDto requestDto);

    Set<OrderItemResponseDto> getOrderItemsByOrderId(Long orderId);

    OrderItemResponseDto getOrderItemById(Long orderItemId);

    void doesUserHasThisOrder(String username, Long orderId);
}
