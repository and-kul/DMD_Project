package dmd;

public class MessageStart {
    boolean ok;
    String error;

    MessageStart(boolean ok, String error) {
        this.ok = ok;
        this.error = error;
    }
}
