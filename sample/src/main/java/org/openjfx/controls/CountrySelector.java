package org.openjfx.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.openjfx.map.Country;
import org.openjfx.map.DataStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CountrySelector implements Control {
    @Getter
    private final Node node;

    @Getter
    private int speed = 0;

    @Getter
    private Country country;

    @Getter
    private LocalDate date;


    public CountrySelector(DataStorage dataStorage) {
        Map<String, Country> countries = dataStorage.getCountries().stream()
                .collect(Collectors.toMap(c -> c.getName(), c -> c));
        List<String> cNames = new ArrayList<>(countries.keySet());
        cNames.add(0,"NONE");
        ChoiceBox<String> c = new ChoiceBox<>(FXCollections.observableArrayList(cNames));
        c.setFocusTraversable(false);
        node = c;
        // if the item of the list is changed
        c.getSelectionModel().selectedIndexProperty().addListener((ov, value, new_value) -> {
            if ((int) new_value == 0) {
                country = null;
            }
            country = countries.get(cNames.get((int) new_value));
        });
    }

}
