package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import ru.mail.commons.DAO;
import ru.mail.commons.DSLContextHelper;
import ru.mail.dto.entity.Company;

import java.sql.SQLException;
import java.util.*;

import static generated.Tables.COMPANY;

public final class CompanyDAO implements DAO<Company> {

    @Override
    public @NotNull Company get(int id) {
        try {
            var context = DSLContextHelper.getContext();
            var result = context
                    .select()
                    .from(COMPANY)
                    .where(COMPANY.COMPANY_ID.eq(id))
                    .fetch();

            for (Record record : result) {
                int companyId = record.getValue(COMPANY.COMPANY_ID);
                String companyName = record.getValue(COMPANY.NAME);
                int TIN = record.getValue(COMPANY.TIN);
                int checkingAccount = record.getValue(COMPANY.CHECKING_ACCOUNT);
                return new Company(companyId, companyName, TIN, checkingAccount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @Override
    public @NotNull List<@NotNull Company> all() {
        final var list = new ArrayList<Company>();
        try {
            var context = DSLContextHelper.getContext();
            var result = context
                    .select()
                    .from(COMPANY)
                    .fetch();

            for (Record record : result) {
                int companyId = record.getValue(COMPANY.COMPANY_ID);
                String companyName = record.getValue(COMPANY.NAME);
                int TIN = record.getValue(COMPANY.TIN);
                int checkingAccount = record.getValue(COMPANY.CHECKING_ACCOUNT);
                list.add(new Company(companyId, companyName, TIN, checkingAccount));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void save(@NotNull Company entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .insertInto(COMPANY)
                    .values(entity.companyId(), entity.name(), entity.TIN(), entity.checkingAccount())
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull Company entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .update(COMPANY)
                    .set(COMPANY.TIN, entity.TIN())
                    .set(COMPANY.NAME, entity.name())
                    .set(COMPANY.CHECKING_ACCOUNT, entity.checkingAccount())
                    .where(COMPANY.COMPANY_ID.eq(entity.companyId()))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Company entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .delete(COMPANY)
                    .where(COMPANY.COMPANY_ID.eq(entity.companyId()))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
