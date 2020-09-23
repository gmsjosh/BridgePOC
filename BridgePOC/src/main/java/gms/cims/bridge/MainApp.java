package gms.cims.bridge;

import org.apache.camel.main.Main;

import java.util.stream.Stream;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        Main main = new Main();
        //main.configure().addRoutesBuilder(new MyRouteBuilder());
        StreamJoiner streamJoiner = new StreamJoiner();
        streamJoiner.Start();
        main.run(args);
    }

}

