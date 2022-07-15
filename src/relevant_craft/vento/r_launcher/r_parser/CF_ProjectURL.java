package relevant_craft.vento.r_launcher.r_parser;

public enum CF_ProjectURL {
    Mods("https://www.curseforge.com/minecraft/mc-mods"),
    Textures("https://www.curseforge.com/minecraft/texture-packs"),
    Worlds("https://www.curseforge.com/minecraft/worlds"),
    ModPacks("https://www.curseforge.com/minecraft/modpacks"),
    ;

    private final String project_url;

    CF_ProjectURL(String project_url) {
        this.project_url = project_url;
    }

    public String getProject_url() {
        return project_url;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
