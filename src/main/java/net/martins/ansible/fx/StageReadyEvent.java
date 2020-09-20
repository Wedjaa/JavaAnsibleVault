package net.martins.ansible.fx;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

class StageReadyEvent extends ApplicationEvent {

    public StageReadyEvent(Stage source) {
        super(source);
    }

    public Stage getStage() {
        return Stage.class.cast(getSource());
    }
}
