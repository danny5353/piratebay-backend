package bo.edu.ucb.sis.piratebay.bl;

import bo.edu.ucb.sis.piratebay.dao.OrderDao;
import bo.edu.ucb.sis.piratebay.model.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderBl {

    private OrderDao orderDao;

    @Autowired
    public OrderBl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public List<OrderModel> findAllOrder() {
        return this.orderDao.findAllOrder();
    }
}
