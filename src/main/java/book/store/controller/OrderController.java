package book.store.controller;

import book.store.dto.order.OrderItemResponseDto;
import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.ShippingAdressRequestDto;
import book.store.dto.order.UpdatedStatusRequestDto;
import book.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all orders")
    public Set<OrderResponseDto> getAllOrdersByUser(Authentication authentication) {
        return orderService.getAllOrdersByUserEmail(authentication.getName());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Create new order")
    public OrderResponseDto createOrder(Authentication authentication,
                                        @RequestBody @Valid ShippingAdressRequestDto requestDto) {
        return orderService.createOrder(authentication.getName(), requestDto);
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update order status")
    public OrderResponseDto updateOrderStatus(@PathVariable Long orderId,
                                              @RequestBody @Valid
                                              UpdatedStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(orderId, requestDto);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all order items from order")
    public Set<OrderItemResponseDto> getAllOrderItemsByOrder(@PathVariable Long orderId,
                                                             Authentication authentication) {
        orderService.doesUserHasThisOrder(authentication.getName(), orderId);
        return orderService.getOrderItemsByOrderId(orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get order items by id")
    public OrderItemResponseDto getOrderItemById(@PathVariable Long orderId,
                                                      @PathVariable Long itemId,
                                                      Authentication authentication) {
        orderService.doesUserHasThisOrder(authentication.getName(), orderId);
        return orderService.getOrderItemById(itemId);
    }
}
