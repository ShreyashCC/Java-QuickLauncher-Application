import java.util.ArrayList;
import java.util.List;

public class Option {
    private String description;
    private List<String> commands;
    private List<String> links;

    public Option(String description) {
        this.description = description;
        this.commands = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void addLink(String link) {
        links.add(link);
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return description;
    }
}
