package com.proyectos2recuelink.views;

import com.proyectos2recuelink.security.SecurityUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;

@CssImport("styles/shared-styles.css")
public class MainLayout extends AppLayout {

    private static final String DEFAULT_IMAGE_URL = "images/logo.png";

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        Image logo = new Image(DEFAULT_IMAGE_URL, "Logo");
        logo.setHeight("50px");
        logo.setWidth("50px");
        logo.addClassName("app-logo");

        // Hacer clicable el logo para ir al dashboard
        logo.getStyle().set("cursor", "pointer");
        logo.addClickListener(e -> UI.getCurrent().navigate("dashboard"));

        Icon userIcon = VaadinIcon.USER.create();
        HorizontalLayout userProfile = new HorizontalLayout(userIcon);
        userProfile.addClassName("user-profile");

// Hacer clicable para ir al perfil
        userProfile.getStyle().set("cursor", "pointer");
        userProfile.addClickListener(e -> UI.getCurrent().navigate("profile"));

        HorizontalLayout header = new HorizontalLayout(logo, userProfile);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.addClassName("main-header");

        addToNavbar(header);
    }

    private void createDrawer() {
        // Botones superiores con navegación manual
        Button alertasBtn = new Button("Alertas");
        Button voluntariosBtn = new Button("Voluntarios");
        Button crearAlertaBtn = new Button("Crear Alerta", VaadinIcon.PLUS.create());

        alertasBtn.addClickListener(e -> UI.getCurrent().navigate(AlertsView.class));
        voluntariosBtn.addClickListener(e -> UI.getCurrent().navigate(VolunteersView.class));
        crearAlertaBtn.addClickListener(e -> UI.getCurrent().navigate(CreateAlertView.class));

        alertasBtn.addClassName("sidebar-button");
        voluntariosBtn.addClassName("sidebar-button");
        crearAlertaBtn.addClassName("sidebar-button");

        VerticalLayout mainMenu = new VerticalLayout(alertasBtn, voluntariosBtn, crearAlertaBtn);

        // Botones inferiores
        Button configBtn = new Button(VaadinIcon.COG.create());
        Button logoutBtn = new Button(VaadinIcon.SIGN_OUT.create());
        configBtn.addClassName("config-button");
        logoutBtn.addClassName("config-button");

        VerticalLayout bottomLayout = new VerticalLayout(configBtn, logoutBtn);
        bottomLayout.addClassName("bottom-layout");

        VerticalLayout drawerLayout = new VerticalLayout(mainMenu, bottomLayout);
        drawerLayout.setSizeFull();
        drawerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        drawerLayout.addClassName("drawer-content");

        addToDrawer(drawerLayout);
    }
}
