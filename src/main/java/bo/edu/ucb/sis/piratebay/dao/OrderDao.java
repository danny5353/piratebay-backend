package bo.edu.ucb.sis.piratebay.dao;

import bo.edu.ucb.sis.piratebay.model.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class OrderDao {


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public List<OrderModel> findAllOrder() {

        String query = "SELECT a.order_id ,c.username,a.payday " +
                "\tFROM public.\"order\" a, product b,public.\"user\" c,product_order d " +
                "\tWHERE d.product_id = b.product_id " +
                "\tAND d.order_id= a.order_id " +
                "\tAND a.user_id = c.user_id ";
        List<OrderModel> result = null;
        try {
            result = jdbcTemplate.query(query, new RowMapper<OrderModel>() {
                @Override
                public OrderModel mapRow(ResultSet resultSet, int i) throws SQLException {
                    return new OrderModel(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3));

                }
            });
        } catch (Exception ex) {
            throw new RuntimeException();
        }
        return result;
    }}