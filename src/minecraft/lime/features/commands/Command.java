package lime.features.commands;

public abstract class Command {

    public abstract String getUsage();
    public abstract String[] getPrefixes();

    public abstract void onCommand(String[] args) throws Exception;
}
