package relevant_craft.vento.r_launcher.r_parser.manager.dependence;

public class Dependence {
    private String title;
    private String href;

    public Dependence(String name, String href) {
        this.title = name;
        this.href = href;
    }

    String getTitle() {
        return title;
    }

    String getHref() {
        return href;
    }
}
