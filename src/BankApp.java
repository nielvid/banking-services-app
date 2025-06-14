
import services.impl.AccountServiceImpl;


public class BankApp {

    public static void main(String[] args) {
        System.out.println("Welcome to the Niel Digital Bank!");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AccountServiceImpl accountServiceImpl = new AccountServiceImpl();
        accountServiceImpl.promptUserForIntendedAction();
    }
}
