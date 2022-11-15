package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import ru.mail.commons.DAO;
import ru.mail.commons.DSLContextHelper;
import ru.mail.dto.entity.Consingment;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static generated.Tables.CONSINGMENT;

public final class ConsingmentDAO implements DAO<Consingment> {

    @Override
    public @NotNull Consingment get(int id) {
        try {
            var context = DSLContextHelper.getContext();
            var result = context
                    .select()
                    .from(CONSINGMENT)
                    .where(CONSINGMENT.CONSINGMENT_ID.eq(id))
                    .fetch();

            for (Record record : result) {
                int consingmentId = record.getValue(CONSINGMENT.CONSINGMENT_ID);
                LocalDate date = record.getValue(CONSINGMENT.ORDER_DATE);
                int companyId = record.getValue(CONSINGMENT.COMPANY_ID);
                return new Consingment(consingmentId, Date.valueOf(date), companyId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @Override
    public @NotNull List<@NotNull Consingment> all() {
        final var list = new ArrayList<Consingment>();
        try {
            var context = DSLContextHelper.getContext();
            var result = context
                    .select()
                    .from(CONSINGMENT)
                    .fetch();

            for (Record record : result) {
                int consingmentId = record.getValue(CONSINGMENT.CONSINGMENT_ID);
                LocalDate date = record.getValue(CONSINGMENT.ORDER_DATE);
                int companyId = record.getValue(CONSINGMENT.COMPANY_ID);
                list.add(new Consingment(consingmentId, Date.valueOf(date), companyId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void save(@NotNull Consingment entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .insertInto(CONSINGMENT)
                    .values(entity.consingmentId(), entity.orderDate().toLocalDate(), entity.companyId())
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull Consingment entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .update(CONSINGMENT)
                    .set(CONSINGMENT.COMPANY_ID, entity.companyId())
                    .set(CONSINGMENT.ORDER_DATE, entity.orderDate().toLocalDate())
                    .where(CONSINGMENT.CONSINGMENT_ID.eq(entity.consingmentId()))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Consingment entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .delete(CONSINGMENT)
                    .where(CONSINGMENT.CONSINGMENT_ID.eq(entity.consingmentId()))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
