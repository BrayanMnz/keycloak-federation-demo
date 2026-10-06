package com.acme.backoffice.views;

import com.acme.backoffice.security.CurrentUser;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;

@Route(value = "supervisor", layout = MainLayout.class)
@PageTitle("Supervisor | ACME Backoffice")
public class SupervisorView extends ProtectedView {

    @Inject
    public SupervisorView(CurrentUser user) {
        super(user, CurrentUser.SUPERVISOR, "Aprobaciones", "Only supervisors can approve/reject.");
    }
}
