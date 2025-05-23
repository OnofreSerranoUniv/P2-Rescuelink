package com.proyectos2recuelink.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport("./styles/shared-styles.css")
public class EmptyLayout extends AppLayout {

    public EmptyLayout() {
        // Layout vacío para vistas sin navbar ni sidebar
        setDrawerOpened(false);
        setPrimarySection(Section.NAVBAR);
    }
}
