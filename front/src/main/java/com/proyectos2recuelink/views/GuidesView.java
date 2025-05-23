package com.proyectos2recuelink.views;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "guides", layout = MainLayout.class)
@CssImport("./styles/shared-styles.css")
public class GuidesView extends VerticalLayout {

    public GuidesView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H2 title = new H2("Guías de Emergencia");
        add(title);
    }
}
