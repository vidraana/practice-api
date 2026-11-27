package shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.dto.OrderDTO;
import shop.dto.OrderRequestDTO;
import shop.mapper.OrderMapper;
import shop.model.Order;
import shop.model.OrderRequest;
import shop.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO dto) {

        OrderRequest request = orderMapper.toModel(dto);

        Order order = orderService.createOrder(request);

        OrderDTO response = orderMapper.toDTO(order);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}