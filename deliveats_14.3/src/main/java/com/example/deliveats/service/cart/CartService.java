package com.example.deliveats.service.cart;

import com.example.deliveats.domain.cart.Cart;
import com.example.deliveats.domain.cart.CartItem;
import com.example.deliveats.domain.store.Menu;
import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.dto.cart.CartResponse;
import com.example.deliveats.exception.CartErrorCode;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.MenuErrorCode;
import com.example.deliveats.repository.cart.CartItemRepository;
import com.example.deliveats.repository.cart.CartRepository;
import com.example.deliveats.repository.store.MenuRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;

    // 장바구니 생성 - Cart가 없으면 null 반환
    @Transactional
    public CartResponse getMyCart(User user) {
        return cartRepository.findWithItemsByUserId(user.getId())
                .map(CartResponse::from)
                .orElse(null);
    }

    // 장바구니 메뉴 추가 - Cart는 첫 담기 때만 생성
    @Transactional
    public CartResponse addItem(User user, Long menuId, int quantity) {

        // 메뉴 조회
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(MenuErrorCode.MENU_NOT_FOUND));

        Store menuStore = menu.getStore();

        // 내 장바구니 조회
        Cart cart = cartRepository.findWithItemsByUserId(user.getId()).orElse(null);

        // Cart가 없으면 생성 (첫 담기)
        if (cart ==null) {
            cart = createNewCart(user, menuStore);
        }
        // 다른 가게 메뉴 담으면 자동 전환
        else if (!cart.getStore().getId().equals(menuStore.getId())) {
            cart.clearItems();
            cart.changeStore(menuStore);
        }

        // 같은 메뉴 있으면 수량 증가
        CartItem existingItem = cart.findItemByMenuId(menuId);
        if (existingItem != null) {
            existingItem.increase(quantity);
        } else {
            CartItem.builder()
                    .cart(cart)
                    .menu(menu)
                    .quantity(quantity)
                    .build();
        }

        return CartResponse.from(cart);
    }

    // 장바구니 메뉴 수량 변경
    @Transactional
    public CartResponse updateQuantity(User user, Long itemId, int quantity) {

        Cart cart = cartRepository.findWithItemsByUserId(user.getId())
                .orElseThrow(() -> new CustomException(CartErrorCode.CART_NOT_FOUND));

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new CustomException(CartErrorCode.CART_ITEM_NOT_FOUND));

        item.updateQuantity((quantity));
        return CartResponse.from(cart);
    }

    // 장바구니 메뉴 삭제 - 마지막 아이템 삭제 시 Cart 자체 삭제
    @Transactional
    public CartResponse removeItem(User user, Long itemId) {

        Cart cart = cartRepository.findWithItemsByUserId(user.getId())
                .orElseThrow(() -> new CustomException(CartErrorCode.CART_NOT_FOUND));

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new CustomException(CartErrorCode.CART_ITEM_NOT_FOUND));

        cart.removeItem(item);

        // 아이템이 0개면 Cart 삭제
        if (cart.getItems().isEmpty()) {
            cartRepository.delete(cart);
            return null;
        }
        return CartResponse.from(cart);
    }

    // 장바구니 비우기 - Cart 자체 삭제
    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findWithItemsByUserId(user.getId())
                .orElseThrow(() -> new CustomException(CartErrorCode.CART_NOT_FOUND));
        cartRepository.delete(cart);
    }

    // Cart 생성
    private Cart createNewCart(User user, Store store) {
        Cart cart = Cart.builder()
                .user(user)
                .store(store)
                .build();
        return cartRepository.save(cart);
    }
}
