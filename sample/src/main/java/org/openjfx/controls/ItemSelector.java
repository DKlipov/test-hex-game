package org.openjfx.controls;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemSelector<T> implements Control {
    @Getter
    private final Node node;

    @Getter
    private int speed = 0;

    @Getter
    private T target;


    public ItemSelector(Map<String, T> values) {
        List<String> cNames = new ArrayList<>(values.keySet());
        cNames.add(0, "NONE");
        ChoiceBox<String> c = new ChoiceBox<>(FXCollections.observableArrayList(cNames));
        c.setFocusTraversable(false);
        node = c;
        // if the item of the list is changed
        c.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
            if ((int) new_value == 0) {
                target = null;
            }
            target = values.get(cNames.get((int) new_value));
        });
    }

}
