package ru.mail.commons;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;

public final class DSLContextHelper {
    public static @NotNull DSLContext getContext() throws SQLException {
        Connection connection = DbConnectionHelper.getConnection();
        return DSL.using(connection, SQLDialect.POSTGRES);
    }
}
