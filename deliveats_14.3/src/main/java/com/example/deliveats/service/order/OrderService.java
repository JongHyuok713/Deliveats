package com.example.deliveats.service.order;

import com.example.deliveats.domain.cart.Cart;
import com.example.deliveats.domain.cart.CartItem;
import com.example.deliveats.domain.order.Order;
import com.example.deliveats.domain.order.OrderItem;
import com.example.deliveats.domain.order.OrderStatus;
import com.example.deliveats.domain.store.Menu;
import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.exception.*;
import com.example.deliveats.exception.order.OrderNotFoundException;
import com.example.deliveats.repository.cart.CartRepository;
import com.example.deliveats.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Transactional
    public Order createFromCart(User user) {

        Cart cart = cartRepository.findWithItemsByUserId(user.getId())
                .orElseThrow(() -> new CustomException(CartErrorCode.CART_NOT_FOUND));

        if (cart.getItems().isEmpty()) {
            throw new CustomException(CartErrorCode.CART_NOT_FOUND);
        }

        Store store = cart.getStore();

        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .store(store)
                .build();

        // CartItem → OrderItem 변환
        for (CartItem cartItem : cart.getItems()) {
            Menu menu = cartItem.getMenu();

            if (!menu.isAvailable()) {
                throw new CustomException(MenuErrorCode.MENU_UNAVAILABLE, "menuId=" + menu.getId());
            }

            OrderItem.builder()
                    .order(order)
                    .menu(menu)
                    .quantity(cartItem.getQuantity())
                    .priceAtOrder(menu.getPrice())
                    .build();
        }

        orderRepository.save(order);

        // 주문 생성 후 장바구니 비우기
        cart.clearItems();

        return order;
    }

    // 주문 상세 페이지 조회 (본인 주문만)
    public Order findOrderForUser(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new CustomException(CommonErrorCode.FORBIDDEN);
        }
        return order;
    }

    public Page<Order> getMyOrders(User user, OrderStatus status, Pageable pageable) {
        if (status != null)
            return orderRepository.findByUserAndStatus(user, status, pageable);
        return orderRepository.findByUser(user, pageable);
    }

    @Transactional
    public void cancelOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!order.getUser().getId().equals(user.getId())) {
            throw new CustomException(CommonErrorCode.FORBIDDEN);
        }

        order.cancel();
    }
}
