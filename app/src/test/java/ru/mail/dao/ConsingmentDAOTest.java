package ru.mail.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mail.commons.DbConnectionHelper;
import ru.mail.commons.FlywayInitializer;
import ru.mail.dto.entity.Consingment;

import java.sql.Date;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConsingmentDAOTest {
    private ConsingmentDAO dao;

    @Test
    void add() {
        Consingment consingment = new Consingment(0, Date.valueOf("2022-08-11"), 1);
        dao.save(consingment);
        assertEquals(consingment, dao.get(consingment.consingmentId()));
    }

    @Test
    void all() {
        final var oldSize = dao.all().size();
        Consingment consingment = new Consingment(0, Date.valueOf("2022-08-11"), 1);
        dao.save(consingment);
        assertEquals(oldSize + 1, dao.all().size());
    }

    @Test
    void update() {
        Consingment consingment1 = new Consingment(0, Date.valueOf("2022-08-11"), 1);
        dao.save(consingment1);
        Consingment consingment2 = new Consingment(0, Date.valueOf("2022-08-12"), 2);
        dao.update(consingment2);
        assertEquals(consingment2, dao.get(consingment1.consingmentId()));
    }

    @Test
    void delete() {
        Consingment consingment = new Consingment(0, Date.valueOf("2022-08-11"), 1);
        dao.save(consingment);
        dao.delete(consingment);
        assertThrows(IllegalStateException.class, () -> dao.get(consingment.consingmentId()));
    }

    @AfterEach
    public void afterEach() throws SQLException {
        DbConnectionHelper.closeConnection();
    }

    @BeforeEach
    public void beforeEach() throws SQLException {
        dao = new ConsingmentDAO();
        DbConnectionHelper.setAutoCommit(false);
    }

    @BeforeAll
    public static void beforeAll() {
        FlywayInitializer.initDb();
    }
}