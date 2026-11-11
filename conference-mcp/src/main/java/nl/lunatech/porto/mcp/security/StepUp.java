package nl.lunatech.porto.mcp.security;

import io.quarkiverse.mcp.server.ToolCallException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class StepUp {

    public static final String MFA_ACR = "urn:jfall:mfa";
    /** The realm's loa-max-age for MFA (Browser Stepup flow). A hint for the client; not enforced here. */
    public static final int MAX_AGE_SECONDS = 300;
    /** The RFC 9470 parameters a client needs to step up, in the tool error text. */
    public static final String CHALLENGE_PARAMS = "acr_values=" + MFA_ACR + " max_age=" + MAX_AGE_SECONDS;

    @Inject
    JsonWebToken jwt;

    public void require() {
        if (!satisfied()) {
            throw new ToolCallException(
                    "insufficient_user_authentication: this tool requires step-up MFA. "
                            + "Re-authenticate with " + CHALLENGE_PARAMS + " and retry.");
        }
    }

    /**
     * The acr this server verified on the user's own token, passed on to conference-api: the
     * token exchange drops it from the token the API receives. Null when the token has no acr.
     */
    public String verifiedAcr() {
        if (satisfied()) return MFA_ACR;
        Object acr = jwt.getClaim("acr");
        return acr == null ? null : String.valueOf(acr);
    }

    public boolean satisfied() {
        String acr = String.valueOf((Object) jwt.getClaim("acr"));
        // "2" covers a realm without the acr.loa.map alias
        return MFA_ACR.equals(acr) || "2".equals(acr);
    }
}
