package org.openjfx.controls;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NumberInput implements Control, InputBox<Integer> {
    @Getter
    private final Node node;


    private TextField field;


    public NumberInput() {
        field = new TextField();
        field.setFocusTraversable(false);
        node = field;
    }

    public Integer getTarget() {
        try {
            return Integer.parseInt(field.getCharacters().toString());
        } catch (Exception e) {
            return 0;
        }
    }

}
