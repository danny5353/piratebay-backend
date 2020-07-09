package bo.edu.ucb.sis.piratebay.model;

public class OrderModel {
    private Integer order_id;
    private String username;
    private String payday;

    public OrderModel() {
        
    }

    public OrderModel(Integer order_id, String username, String payday) {
        this.order_id = order_id;
        this.username = username;
        this.payday = payday;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPayday() {
        return payday;
    }

    public void setPayday(String payday) {
        this.payday = payday;
    }

    @Override
    public String toString() {
        return "OrderModel{" +
                "order_id=" + order_id +
                ", username='" + username + '\'' +
                ", payday='" + payday + '\'' +
                '}';
    }
}
    
