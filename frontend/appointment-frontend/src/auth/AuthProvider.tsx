import { useEffect, useMemo, useState } from "react";
import { AuthContext, User } from "./AuthContext";
import { setTokenGetter } from "../services/http";

export function AuthProvider({children}: {children: React.ReactNode}) {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);

    //lets axios read current token (in-memory)
    useEffect(() => {
        setTokenGetter(() => token);
    }, [token]);

    const value = useMemo(() => {
        return {
            user,
            token,
            isAuthenticated: Boolean(user && token),

            login: ({user, token}: {user: User; token: string}) => {
                setUser(user);
                setToken(token);
            },

            logout: () => {
                setUser(null);
                setToken(null);
            },
        };
    }, [user, token]);
    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}