package postaurant.model;

import postaurant.exception.InputValidationException;

public class CardPayment extends Payment {
    private Long cardNo;
    private String cardName;
    private String bankName;
    private String cardType;
    private String expDate;

    public CardPayment(){

    }
    public CardPayment(Long paymentId, Long orderId, Double amount, Long cardNo, String cardName, String bankName, String cardType, String expDate) {
        super(paymentId, orderId, amount);
        this.cardNo = cardNo;
        this.cardName = cardName;
        this.bankName = bankName;
        this.cardType = cardType;
        this.expDate=expDate;
    }

    public Long getCardNo() {
        return cardNo;
    }


    public void setCardNo(Long cardNo) throws InputValidationException {
        if(cardNo>=1000000000000000L) {
            this.cardNo = cardNo;
        }else {
            throw new InputValidationException();
        }
    }

    public String getCardName() {
        return cardName;
    }

    public String formattedCardNo(){
        StringBuilder number=new StringBuilder();
        if(getCardNo()!=null) {
            Long buffer = getCardNo() % 1000L;
            number.append("X").append(buffer);
            return number.toString();

        }else{
            return "";
        }
    }

    public void setCardName(String cardName) throws InputValidationException {
        if((cardName.matches("([A-Za-z ]*){5,40}")) && (cardName.substring(1,cardName.length()-2).contains(" "))) {
            this.cardName = cardName.toUpperCase();
        }else{
            throw new InputValidationException();
        }
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName)throws InputValidationException {
        if (bankName.matches("(\\p{ASCII}){2,40}")) {
            this.bankName = bankName.toUpperCase();
        } else {
            throw new InputValidationException();
        }
    }

    public String getCardType() {
        return cardType;
    }
    public void setCardType(String cardType) throws InputValidationException{
        String buffer=cardType.toUpperCase();
        if(buffer.equals("VISA")||buffer.equals("VISA ELECTRON")||buffer.equals("AMERICAN EXPRESS")||buffer.equals("MASTERCARD")|| buffer.equals("MAESTRO")||buffer.equals("DINNERS CLUB")){
            this.cardType=cardType;
        }else{
            throw new InputValidationException();
        }
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) throws InputValidationException {
        if( (expDate.length()<6) &&
                (expDate.length()>2)&&
                (expDate.substring(2,3).equals("/")) &&
                    (Integer.parseInt(expDate.substring(0,2))>0) &&
                        (Integer.parseInt(expDate.substring(0,2))<13)) {
            this.expDate = expDate;
        }else{
            throw new InputValidationException();
        }
    }

    public String toString(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("\\Client: ").append(getCardName()).append("\\CardNo: ").append(formattedCardNo()).append("\\Type: ").append(getCardType());
        return super.toString()+stringBuilder.toString();
    }
}
