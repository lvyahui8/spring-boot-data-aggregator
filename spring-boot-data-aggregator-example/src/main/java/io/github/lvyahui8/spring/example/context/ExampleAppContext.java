package io.github.lvyahui8.spring.example.context;

import io.github.lvyahui8.spring.example.model.User;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2019/7/18 22:19
 */
public class ExampleAppContext {
    /**
     * 这里必须使用, InheritableThreadLocal, 只有InheritableThreadLocal会向子线程传递.
     */
    private static ThreadLocal<User> LOGGED_USER = new InheritableThreadLocal<>();

    public static void setLoggedUser(User user) {
        LOGGED_USER.set(user);
    }

    public static boolean isLogged() {
        return LOGGED_USER.get() != null;
    }

    public static Long getUserId() {
        return LOGGED_USER.get().getId();
    }

    public static String getUsername() {
        return LOGGED_USER.get() != null ? LOGGED_USER.get().getUsername() : null;
    }

    public static void remove() {
        LOGGED_USER.remove();
    }
}
