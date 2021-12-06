package org.openjfx.controls;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

public class SpeedPane implements Control {
    private final HBox hbox;

    @Getter
    private int speed = 0;

    private final Button speedButton;

    @Getter
    private LocalDate date;

    private final Label label;

    private final Map<Integer, String> icons = Map.of(
            0, "||",
            1, ">",
            2, ">>",
            3, ">>>"
    );

    public SpeedPane(LocalDate date) {
        this.date = date;
        label = new Label(date.toString());
        speedButton = new Button(icons.get(0));
        speedButton.setFocusTraversable(false);

        speedButton.setOnAction(value -> setSpeed(speed + 1));

        hbox = new HBox(speedButton, label);
        hbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        hbox.setMinWidth(100);
    }

    public void redraw(){
        speedButton.setText(icons.get(this.speed));
        label.setText(date.toString());
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        if (speed >= icons.size()) {
            this.speed = 0;
        }
    }

    @Override
    public Node getNode() {
        return hbox;
    }
}
