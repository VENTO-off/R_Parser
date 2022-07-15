package relevant_craft.vento.r_launcher.r_parser.manager.version;

public class Version {
    private String name;
    private String value;

    Version(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
