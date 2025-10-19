
package at.technikum.application.model;

public class AuthRequest {
    private String Username;
    private String Password;

    public String getUsername() { return Username; }
    public void setUsername(String username) { this.Username = username; }
    public String getPassword() { return Password; }
    public void setPassword(String password) { this.Password = password; }
}