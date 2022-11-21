package com.ssafy.cafe.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Stamp {
    private Integer id;
    private String userId;
    private Integer orderId;
    private Integer quantity;
    
    @Builder
    public Stamp(Integer id, String userId, Integer orderId, Integer quantity) {
        super();
        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
        this.quantity = quantity;
    }
    
    public Stamp(String userId, Integer orderId, Integer quantity) {
        this.userId = userId;
        this.orderId = orderId;
        this.quantity = quantity;
    }
    public Stamp() {
        super();
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
}
