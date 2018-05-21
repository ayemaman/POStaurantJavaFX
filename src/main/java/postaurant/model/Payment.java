/**
 * Class that represents Cash Payments in this system
 */
package postaurant.model;


import postaurant.exception.InputValidationException;

public class Payment {
    private Long paymentId;
    private Long orderId;
    private Double amount;

    public Payment(){

    }
    public Payment(Long paymentId, Long orderId, Double amount) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    /**
     * Payment's amount Setter
     * @throws InputValidationException if amount is 0
     * @param amount
     */

    public void setAmount(Double amount) throws InputValidationException{
        if(amount!=0) {
            this.amount = amount;
        }else{
            throw new InputValidationException();
        }
    }

    public String toString(){
        if(getPaymentId()==null){
            return "ID: not saved\\Amount: £"+getAmount();
        }
        return "ID: " + getPaymentId() + "\\Amount: £"+getAmount();

    }


}
