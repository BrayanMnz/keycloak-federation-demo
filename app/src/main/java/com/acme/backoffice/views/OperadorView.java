package com.acme.backoffice.views;

import com.acme.backoffice.security.CurrentUser;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;

@Route(value = "operador", layout = MainLayout.class)
@PageTitle("Operador | ACME Backoffice")
public class OperadorView extends ProtectedView {

    @Inject
    public OperadorView(CurrentUser user) {
        super(user, CurrentUser.OPERADOR, "Bandeja de operaciones", "Here an operador works cases.");
    }
}
