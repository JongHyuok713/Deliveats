package com.example.deliveats.service.menu;

import com.example.deliveats.domain.store.Menu;
import com.example.deliveats.domain.store.Store;
import com.example.deliveats.domain.user.User;
import com.example.deliveats.dto.store.MenuResponse;
import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.StoreErrorCode;
import com.example.deliveats.exception.menu.MenuNotFoundException;
import com.example.deliveats.exception.store.StoreNotFoundException;
import com.example.deliveats.repository.store.MenuRepository;
import com.example.deliveats.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    // 메뉴 추가
    @Transactional
    public MenuResponse addMenu(Long storeId, User owner, String name, int price, String imageUrl) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        if (!store.getOwner().getId().equals(owner.getId())) {
            throw new CustomException(StoreErrorCode.STORE_PERMISSION_DENIED);
        }

        Menu menu = Menu.builder()
                .store(store)
                .name(name)
                .price(price)
                .imageUrl(imageUrl)
                .available(true)
                .build();

        Menu savedMenu = menuRepository.save(menu);
        return MenuResponse.from(savedMenu);
    }

    // 메뉴 수정
    @Transactional
    public Menu updateMenu(Long storeId, Long menuId, User owner, String name, int price, String imageUrl, Boolean isAvailable) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(MenuNotFoundException::new);

        if (!menu.getStore().getId().equals(storeId)) {
            throw new CustomException(StoreErrorCode.STORE_NOT_FOUND, "storeId mismatch with menu.store");
        }

        if (!menu.getStore().getOwner().getId().equals(owner.getId())) {
            throw new CustomException(StoreErrorCode.STORE_PERMISSION_DENIED);
        }

        menu.updateInfo(name, price);

        if (imageUrl != null) {
            menu.updateImage(imageUrl);
        }

        if (isAvailable != null) {
            if (isAvailable) menu.enable();
            else menu.disable();
        }
        return menu;
    }

    // 메뉴 삭제
    @Transactional
    public void deleteMenu(Long storeId, Long menuId, User owner) {

        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(MenuNotFoundException::new);

        Store store = menu.getStore();
        if (!store.getId().equals(storeId)) {
            throw new CustomException(StoreErrorCode.STORE_NOT_FOUND, "storeId mismatch with menu.store");
        }

        if (!store.getOwner().getId().equals(owner.getId())) {
            throw new CustomException(StoreErrorCode.STORE_PERMISSION_DENIED);
        }

        menuRepository.delete(menu);
    }
}
