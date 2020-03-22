package heath.com.test2_jmessage.tools;

import org.litepal.crud.DataSupport;

public class LoginInformation extends DataSupport {
    private String password;
    private String account;
    private String avatarPath;
    public LoginInformation(){}
    public LoginInformation(String account,String password){
        this.account=account;
        this.password=password;
    }
    public String getPassword(){return password;}
    public String getAccount(){return account;}
    public String getAvatarPath(){return avatarPath;}
    public void setAvatarPath(String avatarPath){
        this.avatarPath=avatarPath;
    }
}
