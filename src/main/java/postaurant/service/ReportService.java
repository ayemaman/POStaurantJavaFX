package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.CardPayment;
import postaurant.model.Payment;
import postaurant.model.User;

import javax.print.PrintException;
import java.io.*;
import java.util.List;

@Component
public class ReportService {
    private final UserDatabase userDatabase;
    private final TimeService timeService;
    private final PrintTextFileService printTextFileService;

    public ReportService(UserDatabase userDatabase, TimeService timeService, PrintTextFileService printTextFileService) {
        this.userDatabase = userDatabase;
        this.timeService = timeService;
        this.printTextFileService = printTextFileService;
    }

    public Double getTotalSales(String start, String end) {
        Double buffer = userDatabase.getTotalSoldSQL(start, end);
        if (buffer == null) {
            return 0.00;
        } else {
            return buffer;
        }
    }

    public Double getTotalSales(User user,String start, String end){
        Double buffer= userDatabase.getDubTotalSold(user,start,end);
        if(buffer==null){
            return 0.00;
        }else{
            return buffer;
        }
    }

    public List<Payment> getReport(User user, String date){
            return userDatabase.getDubReport(user,date,(timeService.createDateOnly(timeService.createNextDayLocalDateTimeFromString(date))) );
    }
    public List<Payment> getDayReport(String date){
        return userDatabase.getDayReport(date,(timeService.createDateOnly(timeService.createNextDayLocalDateTimeFromString(date))));
    }

    public void createFullReport(String date){
        try{
            List<Payment> paymentList=getDayReport(date);
            PrintWriter out=new PrintWriter("./checks/"+date+"dayReport.txt");
            out.println("Date: " + timeService.createTime());
            out.println("Report for "+date);
            Double cash=0.00;
            Double card=0.00;
            for(Payment p:paymentList) {
                if (!(p instanceof CardPayment)) {
                    cash += p.getAmount();
                }else{
                    card+=p.getAmount();
                }
            }
            out.println("----------------------------------------------------------");
            out.println("\t\tTOTAL AMOUNT");
            out.println("CASH: "+String.format("%.2f", cash));
            out.println("CARDS: "+String.format("%.2f", card));
            out.println("TOTAL SALES:"+String.format("%.2f",getTotalSales(date,(timeService.createDateOnly(timeService.createNextDayLocalDateTimeFromString(date)))) ));
            int visa=0;
            int visaE=0;
            int dinners=0;
            int american=0;
            int master=0;
            int maestro=0;
            for(Payment p:paymentList){
                if(p instanceof CardPayment){
                    switch(((CardPayment) p).getCardType()){
                        case "VISA":
                            visa++;
                            break;
                        case "VISA ELECTRON":
                            visaE++;
                            break;
                        case "DINNERS CLUB":
                            dinners++;
                            break;
                        case "AMERICAN EXPRESS":
                            american++;
                            break;
                        case "MASTERCARD":
                            master++;
                            break;
                        case "MAESTRO":
                            maestro++;
                            break;
                    }

                }
            }
            out.println("----------------------------------------------------------");
            out.println("\t\tCARDSLIPS Qty.");
            out.println("VISA:         "+visa);
            out.println("VISAELECTRON: "+visaE);
            out.println("DINNERSCLUB:  "+dinners);
            out.println("AMERICAN EXP: "+american);
            out.println("MASTERCARD:   "+master);
            out.println("MAESTRO:      "+maestro);
            out.close();
            File file=new File("./checks/"+date+"dayReport.txt");
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }

            printTextFileService.printFileTest(file);

        } catch (PrintException | IOException e) {
            e.printStackTrace();
        }
    }


    public void createDubReport(User user,String date){
        try{
            List<Payment> paymentList=getReport(user,date);
            PrintWriter out=new PrintWriter("./checks/dubReport.txt");
            out.println("Date: " + timeService.createTime());
            out.println("Report for "+date);
            out.println("Server name: " + user.getFirstName());
            Double cash=0.00;
            Double card=0.00;
            for(Payment p:paymentList) {
                if (!(p instanceof CardPayment)) {
                    cash += p.getAmount();
                }else{
                    card+=p.getAmount();
                }
            }
            out.println("----------------------------------------------------------");
            out.println("\t\tTOTAL AMOUNT");
            out.println("CASH: "+String.format("%.2f", cash));
            out.println("CARDS: "+String.format("%.2f", card));
            out.println("TOTAL SALES:"+String.format("%.2f",getTotalSales(user,date,(timeService.createDateOnly(timeService.createNextDayLocalDateTimeFromString(date)))) ));
            int visa=0;
            int visaE=0;
            int dinners=0;
            int american=0;
            int master=0;
            int maestro=0;
            for(Payment p:paymentList){
                if(p instanceof CardPayment){
                    switch(((CardPayment) p).getCardType()){
                        case "VISA":
                            visa++;
                            break;
                        case "VISA ELECTRON":
                            visaE++;
                            break;
                        case "DINNERS CLUB":
                            dinners++;
                            break;
                        case "AMERICAN EXPRESS":
                            american++;
                            break;
                        case "MASTERCARD":
                            master++;
                            break;
                        case "MAESTRO":
                            maestro++;
                            break;
                    }

                }
            }
            out.println("----------------------------------------------------------");
            out.println("\t\tCARDSLIPS Qty.");
            out.println("VISA:         "+visa);
            out.println("VISAELECTRON: "+visaE);
            out.println("DINNERSCLUB:  "+dinners);
            out.println("AMERICAN EXP: "+american);
            out.println("MASTERCARD:   "+master);
            out.println("MAESTRO:      "+maestro);
            out.close();
            File file=new File("./checks/dubReport.txt");
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }

            printTextFileService.printFileTest(file);

        } catch (PrintException | IOException e) {
            e.printStackTrace();
        }
    }

}
