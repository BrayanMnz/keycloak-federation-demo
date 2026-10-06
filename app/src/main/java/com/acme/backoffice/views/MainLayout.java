package com.acme.backoffice.views;

import com.acme.backoffice.security.CurrentUser;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import jakarta.inject.Inject;

public class MainLayout extends AppLayout {

    @Inject
    public MainLayout(CurrentUser user) {
        var title = new H1("ACME Backoffice");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        var who = new Span(user.displayName() + " (" + user.username() + ")");
        who.getStyle().set("color", "var(--lumo-secondary-text-color)");

        // Handled by quarkus-oidc (RP-initiated logout), not by the Vaadin router
        var logout = new Anchor("/logout", "Logout");
        logout.setRouterIgnore(true);

        var header = new HorizontalLayout(title, who, logout);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.expand(title);
        header.setWidthFull();
        header.getStyle().set("padding-right", "var(--lumo-space-m)");

        var nav = new SideNav();
        nav.addItem(new SideNavItem("Home", HomeView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem("Operador page", OperadorView.class, VaadinIcon.TASKS.create()));
        nav.addItem(new SideNavItem("Supervisor page", SupervisorView.class, VaadinIcon.CHECK_SQUARE_O.create()));

        addToNavbar(new DrawerToggle(), header);
        addToDrawer(nav);
    }
}
