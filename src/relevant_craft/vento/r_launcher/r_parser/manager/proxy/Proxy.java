package relevant_craft.vento.r_launcher.r_parser.manager.proxy;

public class Proxy {
    private String host;
    private String port;
    private String user;
    private String password;

    public Proxy() {
        this.host = null;
        this.port = null;
        this.user = null;
        this.password = null;
    }

    public void setHost(String host) {
        this.host = host.trim();
    }

    public void setPort(String port) {
        this.port = port.trim();
    }

    public void setUser(String user) {
        this.user = user.trim();
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public int getPortInt() {
        return Integer.parseInt(port);
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
