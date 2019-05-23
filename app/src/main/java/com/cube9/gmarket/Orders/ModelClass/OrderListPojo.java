package com.cube9.gmarket.Orders.ModelClass;

public class OrderListPojo {
    String order_id,ship_to,order_date,order_total,order_status;

    public OrderListPojo(String order_id, String ship_to, String order_date, String order_total, String order_status) {
        this.order_id = order_id;
        this.ship_to = ship_to;
        this.order_date = order_date;
        this.order_total = order_total;
        this.order_status = order_status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getShip_to() {
        return ship_to;
    }

    public void setShip_to(String ship_to) {
        this.ship_to = ship_to;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }
}
