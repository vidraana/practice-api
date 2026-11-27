package shop.mapper;

import org.springframework.stereotype.Component;
import shop.dto.AddressDTO;
import shop.dto.OrderDTO;
import shop.dto.OrderRequestDTO;
import shop.model.Address;
import shop.model.Order;
import shop.model.OrderDetail;
import shop.model.OrderItem;
import shop.model.OrderRequest;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public OrderRequest toModel(OrderRequestDTO dto) {

        Address address = new Address(
                dto.address().country(),
                dto.address().city(),
                dto.address().county(),
                dto.address().streetAddress()
        );

        List<OrderItem> items = new ArrayList<>();

        for (OrderRequestDTO.Item item : dto.items()) {
            items.add(new OrderItem(
                    item.productId(),
                    item.quantity()
            ));
        }

        return new OrderRequest(
                dto.userId(),
                dto.createdAt(),
                address,
                items
        );
    }

    public OrderDTO toDTO(Order order) {

        Address address = order.getAddress();

        AddressDTO addressDTO = new AddressDTO(
                address.getCountry(),
                address.getCity(),
                address.getCounty(),
                address.getStreetAddress()
        );

        List<OrderDTO.Item> items = new ArrayList<>();

        for (OrderDetail detail : order.getOrderDetails()) {
            items.add(new OrderDTO.Item(
                    detail.getProduct().getId(),
                    detail.getProduct().getName(),
                    detail.getShippedFrom().getId(),
                    detail.getShippedFrom().getName(),
                    detail.getQuantity()
            ));
        }

        return new OrderDTO(
                order.getId(),
                order.getUser().getId(),
                order.getCreatedAt(),
                addressDTO,
                items
        );
    }
}