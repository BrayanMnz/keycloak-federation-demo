package com.acme.backoffice.views;

import com.acme.backoffice.security.CurrentUser;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;

import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Home | ACME Backoffice")
public class HomeView extends VerticalLayout {

    @Inject
    public HomeView(CurrentUser user) {
        add(new H2("Hola, " + user.displayName()));

        if (user.appRoles().isEmpty()) {
            var warning = new Paragraph("You have no backoffice role. Your corporate (Entra ID) groups "
                    + "are not mapped to this application.");
            warning.getStyle().set("color", "var(--lumo-error-text-color)").set("font-weight", "600");
            add(warning);
        }

        add(new Paragraph("This user was created in Keycloak automatically on first login. "
                + "Groups and roles below come from the corporate IdP groups, re-synced on every login."));

        add(row("Username (Keycloak)", new Span(user.username())),
                row("Email", new Span(user.email())),
                row("Keycloak groups", badges(user.groups())),
                row("App roles", badges(user.appRoles())));

        var claims = new Pre(user.accessTokenClaims().encodePrettily());
        claims.getStyle().set("font-size", "var(--lumo-font-size-s)").set("overflow", "auto");
        add(new Details("Raw access token claims", claims));
    }

    private static HorizontalLayout row(String label, com.vaadin.flow.component.Component value) {
        var caption = new Span(label + ":");
        caption.getStyle().set("font-weight", "600").set("min-width", "180px");
        var row = new HorizontalLayout(caption, value);
        row.setAlignItems(Alignment.BASELINE);
        return row;
    }

    private static Div badges(List<String> values) {
        var box = new Div();
        if (values.isEmpty()) {
            box.add(new Span("none"));
        }
        for (String value : values) {
            var badge = new Span(value);
            badge.getElement().getThemeList().add("badge");
            badge.getStyle().set("margin-right", "var(--lumo-space-xs)");
            box.add(badge);
        }
        return box;
    }
}
