package pizza;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long pizzaId;
    // LDH 소스추가 초기값 설정
    private String orderStatus ="Ordered";
    private Long qty;

    @PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        pizza.external.Payment payment = new pizza.external.Payment();

        // checkpoint4. req/res LDH 202011041036
        // [소스추가] payment 에 order 정보 setting
        payment.setOrderId(this.getId());
        payment.setPaymentStatus("Paid");

        // mappings goes here
        OrderApplication.applicationContext.getBean(pizza.external.PaymentService.class)
            .doPayment(payment);


    }

    @PostUpdate
    public void onPostUpdate(){
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getPizzaId() {
        return pizzaId;
    }

    public void setPizzaId(Long pizzaId) {
        this.pizzaId = pizzaId;
    }
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }




}
