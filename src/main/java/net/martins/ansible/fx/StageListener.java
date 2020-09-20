package net.martins.ansible.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class StageListener implements ApplicationListener<StageReadyEvent> {

    private final String applicationTitle;
    private final Resource fxml;
    private final ApplicationContext ac;

    public StageListener(@Value("${spring.application.ui.title}") String applicationTitle,
                         @Value("classpath:/ui.fxml") Resource fxml, ApplicationContext ac) {
        this.applicationTitle = applicationTitle;
        this.fxml = fxml;
        this.ac = ac;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {

        try {
            final Stage window = stageReadyEvent.getStage();
            URL url = this.fxml.getURL();
            FXMLLoader fxmlLoader = new FXMLLoader( url );
            fxmlLoader.setControllerFactory(ac::getBean);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 600, 600);
            window.setScene(scene);
            window.setTitle(this.applicationTitle);
            window.show();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }
}
