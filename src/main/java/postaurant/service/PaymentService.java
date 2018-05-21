/**
 * Service, that works with database data related to Payments
 * @see postaurant.model.Payment
 * @see postaurant.model.CardPayment
 */
package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.CardPayment;
import postaurant.model.Order;
import postaurant.model.Payment;
import postaurant.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentService {
    private final UserDatabase userDatabase;

    public PaymentService(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    /**
     * Retrieves all cash Payments that are related to specified order ID
     * @param orderId  ID of order
     * @return ArrayList<Payment>
     */
    public ArrayList<Payment> getCashPayments(Long orderId){
        return (ArrayList<Payment>)userDatabase.getCashPaymentsForTable(orderId);
    }

    /**
     * Retrieves all card Payments that are related to specified order ID
     * @param orderId  ID of order
     * @return ArrayList<CardPayment>
     */
    public ArrayList<CardPayment> getCardPaymentsForTable(Long orderId){
        return (ArrayList<CardPayment>)userDatabase.getCardPaymentsForTable(orderId);
    }

    /**
     *
     * @param list
     * @param user
     */
    public void savePayments(List<Payment> list,User user){
        for(Payment p:list){
            if(p instanceof CardPayment) {
                userDatabase.addNewCardPayment((CardPayment) p,user);
            }else{
                userDatabase.addNewPayment(p,user);
            }
        }
    }
    public void voidPayment(Payment payment){
        userDatabase.voidPayment(payment);

    }
    public void voidPayments(Order order){
        userDatabase.voidPayments(order);
    }

}
