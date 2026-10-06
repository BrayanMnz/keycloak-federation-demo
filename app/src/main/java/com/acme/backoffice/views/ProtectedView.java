package com.acme.backoffice.views;

import com.acme.backoffice.security.CurrentUser;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/** A page that is only usable with a given app role; shows a 403 message otherwise. */
abstract class ProtectedView extends VerticalLayout {

    protected ProtectedView(CurrentUser user, String role, String title, String description) {
        if (user.hasRole(role)) {
            var heading = new H2("✔ " + title);
            heading.getStyle().set("color", "var(--lumo-success-text-color)");
            add(heading, new Paragraph(description), new Paragraph("Access granted by role \"" + role + "\"."));
        } else {
            var heading = new H2("✘ 403 – " + title);
            heading.getStyle().set("color", "var(--lumo-error-text-color)");
            add(heading, new Paragraph("You need role \"" + role + "\". "
                    + "Ask to be added to the corresponding Entra ID group."));
        }
    }
}
