package heath.com.test2_jmessage.tools;

import org.litepal.crud.DataSupport;

public class LoginInformation extends DataSupport {
    private String password;
    private String account;
    public LoginInformation(String account,String password){
        this.account=account;
        this.password=password;
    }
    public String getPassword(){return password;}
    public String getAccount(){return account;}
}
