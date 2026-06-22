package pers.project.salesmanagement.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pers.project.salesmanagement.dto.request.CreateSalesOrderRequest;
import pers.project.salesmanagement.dto.response.SalesOrderItemResponse;
import pers.project.salesmanagement.dto.response.SalesOrderResponse;
import pers.project.salesmanagement.entity.Customer;
import pers.project.salesmanagement.entity.ProductVariant;
import pers.project.salesmanagement.entity.SalesOrder;
import pers.project.salesmanagement.entity.SalesOrderItem;
import pers.project.salesmanagement.mapper.SalesOrderItemMapper;
import pers.project.salesmanagement.mapper.SalesOrderMapper;
import pers.project.salesmanagement.repository.CustomerRepository;
import pers.project.salesmanagement.repository.ProductVariantRepository;
import pers.project.salesmanagement.repository.SalesOrderItemRepository;
import pers.project.salesmanagement.repository.SalesOrderRepository;
import pers.project.salesmanagement.service.SalesOrderService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductVariantRepository productVariantRepository;
    private final SalesOrderMapper salesOrderMapper;
    private final SalesOrderItemMapper salesOrderItemMapper;

    @Override
    public SalesOrderResponse createSalesOrder(CreateSalesOrderRequest request) {
        SalesOrder salesOrder = new SalesOrder();

        Customer customer = customerRepository.getReferenceById(request.customerId());
        salesOrder.setCustomer(customer);

        List<SalesOrderItem> salesOrderItems = new ArrayList<>();
        request.salesOrderItemRequests().forEach(salesOrderItemRequest -> {
            SalesOrderItem salesOrderItem = new SalesOrderItem();

            ProductVariant productVariant = productVariantRepository.getReferenceById(salesOrderItemRequest.variantId());
            salesOrderItem.setVariant(productVariant);

            salesOrderItem.setUnitPrice(productVariant.getSellPrice());
            salesOrderItem.setQuantity(salesOrderItemRequest.quantity());
            salesOrderItem.setDiscount(salesOrderItemRequest.discount());

            salesOrderItems.add(salesOrderItem);
        });
        salesOrder.setSalesOrderItems(salesOrderItems);

        SalesOrder savedSalesOrder = salesOrderRepository.save(salesOrder);

        SalesOrderResponse salesOrderResponse = salesOrderMapper.toResponse(savedSalesOrder);
        List<SalesOrderItemResponse> salesOrderItemResponses = salesOrderItemMapper.toResponseList(savedSalesOrder.getSalesOrderItems());
        salesOrderResponse.orderItems().addAll(salesOrderItemResponses);

        return salesOrderResponse;
    }
}
