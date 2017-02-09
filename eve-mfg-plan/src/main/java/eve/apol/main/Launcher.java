package eve.apol.main;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Launcher extends Application {

    private static final class RuntimeStats implements Runnable {
        private static Logger log = LoggerFactory.getLogger(RuntimeStats.class);
        List<String> stats = new ArrayList<>();
        long sleepMs = 200;
        private volatile boolean stop = false;
        private static Runtime rt = Runtime.getRuntime();

        @Override
        public void run() {

            while (!stop) {
                recordStat();
                
            }
            sleepMs = 10;
            for(int i = 0; i < 20; i++) {
                recordStat();
            }
            writeStat();
        }

        private void writeStat() {
            for(String stat : stats) {
                log.info(stat);
            }
        }

        private void recordStat() {
            long mem = (rt.totalMemory() - rt.freeMemory()) / 1024;
            //stats.add(String.format("%d;%d", System.currentTimeMillis(), mem));
            log.info("{};{}", System.currentTimeMillis(), mem);
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                stop();
            }
        }

        public void stop() {
            this.stop = true;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select yaml");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().setAll(new ExtensionFilter("YAML", "*.yaml"));
        File result = fc.showOpenDialog(primaryStage);
        Thread typeids = null;
        Thread statsThread = null;
        if (result != null) {
            RuntimeStats stats = new RuntimeStats();
            statsThread = new Thread(stats, "Statistics thread");
            typeids = new Thread(() -> {
                Collection<?> items = new Snaker(result).rock();
                stats.stop();
                items.size();
                items.size();
                items.size();
            }, "TypeID read thread");
            typeids.start();
            statsThread.start();
            Platform.exit();
        } else {
            Platform.exit();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
