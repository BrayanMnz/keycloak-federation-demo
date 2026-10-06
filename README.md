# Keycloak federated identity demo (Entra ID → Keycloak → App)

Shows that users do **not** need to be created one by one in Keycloak: they are
created on first login, and their access comes from their corporate (Entra ID) groups.

| Component | URL | Plays the role of |
|---|---|---|
| `fake-entra` (Keycloak, realm `corp`) | http://localhost:18081/admin (admin/admin) | **Entra ID**: owns users + groups |
| `keycloak` (realm `acme`) | http://localhost:18080/admin (admin/admin) | **Our Keycloak**: broker, *zero* users |
| `app` (Quarkus + Vaadin, Java 25) | http://localhost:13000 | Backoffice app, OIDC client of our Keycloak |

```
docker compose up -d --build   # first build ~2-3 min (Maven + Vaadin bundle); Keycloak ~30s
docker compose down       # wipes everything; next `up` starts clean
```

The fake Entra login page uses Keycloak's unstyled `base` theme (`loginTheme` in
`fake-entra/corp-realm.json`), so it's easy to tell apart from our Keycloak's pages.

Always reset **both** Keycloaks together (`down` / `up`). Recreating only `fake-entra` gives
its users new IDs, and our Keycloak then shows *"account already exists"* (link-account
prompt) for users it already knows. This is the same thing that happens in real life if
users were created locally before federation was turned on.

## Corporate users (in fake Entra)

| User / password | Entra groups | Expected access |
|---|---|---|
| `ana` / `ana` | ACME-Backoffice-Operadores | operador |
| `carlos` / `carlos` | Operadores + Supervisores | operador + supervisor |
| `maria` / `maria` | Marketing | none (not mapped to the app) |

## Mapping (configured once, in realm `acme`)

```
Entra group                    →  Keycloak group          →  App role
ACME-Backoffice-Operadores     →  backoffice-operador     →  operador
ACME-Backoffice-Supervisores   →  backoffice-supervisor   →  operador, supervisor
```
See: `acme` realm → Identity providers → `entra` → Mappers (sync mode **FORCE**).

## Demo script (use a private/incognito window per user)

1. **Show the starting point:** `acme` realm → Users is **empty**.
2. Open http://localhost:13000. Every page requires login, so you go straight
   to the "Entra" login page (`kc_idp_hint`). Log in as `ana`.
3. The app shows Ana's groups and roles. *Operador page* is ✔, *Supervisor page* is 403.
4. **Back in Keycloak, refresh Users:** `ana` now exists, created automatically,
   already in group `backoffice-operador`, and linked to the `entra` IdP (*Identity provider links* tab).
5. Log in as `maria` → user created, but **no roles** → both pages are 403.
6. **Change access in Entra, not Keycloak:** fake-entra admin → realm `corp` → Users → `ana`
   → Groups → join `ACME-Backoffice-Supervisores`. Logout from the app and log in again
   as `ana` → Supervisor page is now ✔. (*Logout* also ends the Entra session, so you can switch users.)
7. Remove `ana` from all ACME groups in fake Entra, log in again → access gone.
8. Optional: disable `ana` in fake Entra → she can't log in at all.

## What maps to real Entra ID

| Demo | Production |
|---|---|
| `fake-entra` realm `corp` | Entra tenant (`https://login.microsoftonline.com/<tenant>/v2.0`) |
| client `keycloak-broker` | App registration, redirect `https://<kc>/realms/<realm>/broker/entra/endpoint` |
| `groups` claim with group names | `groups` claim (object IDs) or, better, **App Roles** (`roles` claim) |
| `maria` gets in with no roles | Enterprise app **"Assignment required = Yes"**: Entra blocks her before Keycloak |
| No HTTPS | HTTPS everywhere |

## The app (`app/`)

Quarkus 3.32 + Vaadin 25, Java 25. Built inside Docker (`app/Dockerfile`), so no local JDK is needed.

| File | What it does |
|---|---|
| `src/main/resources/application.properties` | All the OIDC wiring: `quarkus-oidc` in `web-app` mode, `kc_idp_hint=entra`, roles from `realm_access/roles`, logout |
| `security/CurrentUser.java` | User info from the ID/access tokens (name, email, Keycloak groups, app roles) |
| `views/HomeView.java` | Shows who you are, your groups/roles and the raw token claims |
| `views/OperadorView.java`, `SupervisorView.java` | Role-gated pages (✔ or 403) |
| `api/MeResource.java` | `GET /api/me`, `/api/operador`, `/api/supervisor` (`@RolesAllowed`), handy for curl checks |

The app has **no user store and no Entra configuration**. It only trusts Keycloak; Keycloak trusts Entra.

Local dev without Docker for the app (needs JDK 25 + Maven; Keycloak still in Docker):
```
KC_URL=http://localhost:18080/realms/acme mvn -f app quarkus:dev -Dquarkus.http.port=13000
```

Note: demo only. Secrets are hardcoded and everything is plain HTTP.
