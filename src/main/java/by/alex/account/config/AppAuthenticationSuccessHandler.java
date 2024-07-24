package by.alex.account.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class AppAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final String ADMIN_HOME = "/admin/accounts";
    private static final String USER_HOME = "/user/account";

    protected void handle(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication) throws IOException {

        String redirectUrl = isRoleAdmin(authentication) ? ADMIN_HOME : USER_HOME;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private static boolean isRoleAdmin(Authentication authentication) {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        return role.equals("ROLE_ADMIN");
    }
}
