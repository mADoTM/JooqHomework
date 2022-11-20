package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import ru.mail.commons.DAO;
import ru.mail.commons.DSLContextHelper;
import ru.mail.dto.CompaniesWithAmountOfProductsReport;
import ru.mail.dto.PerDayProductsReport;
import ru.mail.dto.entity.Company;
import ru.mail.dto.entity.Position;
import ru.mail.dto.entity.Product;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static generated.Tables.*;
import static generated.Tables.PRODUCT;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.sum;

public final class PositionDAO implements DAO<Position> {
    @Override
    public @NotNull Position get(int consingmentId) {
        try {
            var context = DSLContextHelper.getContext();
            var result = context
                    .select()
                    .from(POSITION)
                    .where(POSITION.CONSINGMENT_ID.eq(consingmentId))
                    .fetch();

            for (Record record : result) {
                int innerCode = record.getValue(POSITION.INNER_CODE);
                int tableConsingmentId  = record.getValue(POSITION.CONSINGMENT_ID);
                int amount = record.getValue(POSITION.AMOUNT);
                int cost = record.getValue(POSITION.COST);
                return new Position(cost, innerCode, amount, tableConsingmentId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Record with id " + consingmentId + "not found");
    }

    @Override
    public @NotNull List<@NotNull Position> all() {
        final var list = new ArrayList<Position>();
        try {
            var context = DSLContextHelper.getContext();
            var result = context
                    .select()
                    .from(POSITION)
                    .fetch();

            for (Record record : result) {
                int innerCode = record.getValue(POSITION.INNER_CODE);
                int tableConsingmentId  = record.getValue(POSITION.CONSINGMENT_ID);
                int amount = record.getValue(POSITION.AMOUNT);
                int cost = record.getValue(POSITION.COST);
                list.add(new Position(cost, innerCode, amount, tableConsingmentId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void save(@NotNull Position entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .insertInto(POSITION)
                    .values(entity.cost(), entity.innerCode(), entity.amount(), entity.consingmentId())
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull Position entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .update(POSITION)
                    .set(POSITION.AMOUNT, entity.amount())
                    .set(POSITION.COST, entity.cost())
                    .where(POSITION.CONSINGMENT_ID
                            .eq(entity.consingmentId())
                            .and(POSITION.INNER_CODE.eq(entity.innerCode())))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Position entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .delete(POSITION)
                    .where(POSITION.CONSINGMENT_ID
                            .eq(entity.consingmentId())
                            .and(POSITION.INNER_CODE.eq(entity.innerCode())))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull Map<Product, Double> getAverageProductCostInPeriod(Date from, Date to) {
        final var map = new HashMap<Product, Double>();

        try {
            var context = DSLContextHelper.getContext();
            var temp = context
                    .select(POSITION.INNER_CODE, avg(POSITION.COST.div(POSITION.AMOUNT)))
                    .from(POSITION)
                    .join(CONSINGMENT)
                    .on(CONSINGMENT.CONSINGMENT_ID.eq(POSITION.CONSINGMENT_ID))
                    .where(CONSINGMENT.ORDER_DATE.between(from.toLocalDate(), to.toLocalDate()))
                    .groupBy(POSITION.INNER_CODE);

            var result = context
                    .select(PRODUCT.NAME, temp.field(0), temp.field(1))
                    .from(PRODUCT)
                    .join(temp)
                    .on(PRODUCT.INNER_CODE.eq(temp.field(0, Integer.class)))
                    .fetch();

            for(Record record : result) {
                int innerCode = record.getValue(record.field(1, Integer.class));
                String name = record.getValue(record.field(0, String.class));
                double average = record.getValue(record.field(2, BigDecimal.class)).intValue();
                map.put(new Product(innerCode, name), average);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public @NotNull PerDayProductsReport getPerDayProductsReportInPeriod(Date from, Date to) {
        final var report = new PerDayProductsReport();

        try {
            var context = DSLContextHelper.getContext();
            var temp = context
                    .select(CONSINGMENT.ORDER_DATE, PRODUCT.INNER_CODE, sum(POSITION.AMOUNT).as("total_amount"), sum(POSITION.COST))
                    .from(POSITION)
                    .join(CONSINGMENT)
                    .on(CONSINGMENT.CONSINGMENT_ID
                            .eq(POSITION.CONSINGMENT_ID))
                    .join(PRODUCT)
                    .on(PRODUCT.INNER_CODE.eq(POSITION.INNER_CODE))
                    .where(CONSINGMENT.ORDER_DATE.between(from.toLocalDate(), to.toLocalDate()))
                    .groupBy(CONSINGMENT.ORDER_DATE, PRODUCT.INNER_CODE);

            var result = context
                    .select(temp.field(0),
                            PRODUCT.INNER_CODE,
                            PRODUCT.NAME,
                            temp.field(2),
                            temp.field(3))
                    .from(PRODUCT, temp)
                    .where(PRODUCT.INNER_CODE.eq(temp.field(1, Integer.class)))
                    .fetch();

            for(Record record : result) {
                final var innerCode = record.getValue(PRODUCT.INNER_CODE);
                final var name = record.getValue(PRODUCT.NAME);
                final var orderDate = record.getValue(0, LocalDate.class);
                final var count = record.getValue(3, Integer.class);
                final var sum = record.getValue(4, Integer.class);

                final var product = new Product(innerCode, name);

                report.add(Date.valueOf(orderDate), product, count, sum);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return report;
    }

    public @NotNull List<Company> getFirstTenCompaniesWithBiggestAmountOfProducts() {
        List<Company> list = new ArrayList<>();

        try {
            var context = DSLContextHelper.getContext();
            var temp = context
                    .select(CONSINGMENT.COMPANY_ID, sum(POSITION.AMOUNT))
                    .from(CONSINGMENT)
                    .join(POSITION)
                    .on(CONSINGMENT.CONSINGMENT_ID.eq(POSITION.CONSINGMENT_ID))
                    .groupBy(CONSINGMENT.COMPANY_ID);

            var result = context
                    .select(COMPANY.COMPANY_ID, COMPANY.NAME, COMPANY.TIN, COMPANY.CHECKING_ACCOUNT, temp.field(1))
                    .from(COMPANY)
                    .leftJoin(temp)
                    .on(COMPANY.COMPANY_ID.eq(temp.field(0, Integer.class)))
                    .orderBy(temp.field(0))
                    .limit(10)
                    .fetch();

            for(Record record : result) {
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

    public @NotNull CompaniesWithAmountOfProductsReport getCompaniesWithAmountSatisfiedCondition(Map<Product, Integer> products) {
        final var report = new CompaniesWithAmountOfProductsReport();

        try {
            var context = DSLContextHelper.getContext();

            var temp = context
                    .select(CONSINGMENT.COMPANY_ID,
                            POSITION.INNER_CODE,
                            sum(POSITION.AMOUNT))
                    .from(CONSINGMENT)
                    .join(POSITION)
                    .on(CONSINGMENT.CONSINGMENT_ID.eq(POSITION.CONSINGMENT_ID))
                    .groupBy(CONSINGMENT.COMPANY_ID, POSITION.INNER_CODE)
                    .asTable();

            var result = context
                    .select(COMPANY.COMPANY_ID,
                            COMPANY.NAME,
                            COMPANY.TIN,
                            COMPANY.CHECKING_ACCOUNT,
                            temp.field(1),
                            temp.field(2))
                    .from(COMPANY)
                    .join(temp)
                    .on(COMPANY.COMPANY_ID.eq(temp.field(0, Integer.class)))
                    .fetch();

            for(Record record : result) {
                final var dbInnerCode = record.getValue(4, Integer.class);
                final var dbAmount = record.getValue(5, BigInteger.class).intValue();

                final var product = products.keySet().stream().filter(x -> x.innerCode() == dbInnerCode).findFirst();

                if (product.isEmpty() || products.get(product.get()) > dbAmount) {
                    continue;
                }

                report.add(new Company(
                                record.getValue(COMPANY.COMPANY_ID),
                                record.getValue(COMPANY.NAME),
                                record.getValue(COMPANY.TIN),
                                record.getValue(COMPANY.CHECKING_ACCOUNT)),
                        product.get(),
                        dbAmount);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return report;
    }

    public @NotNull Map<Company, Set<Product>> getCompaniesWithProductsInPeriod(Date from, Date to) {
        final var map = new HashMap<Company, Set<Product>>();

        try {
            var context = DSLContextHelper.getContext();

            var result = context
                    .select(COMPANY.COMPANY_ID,
                            COMPANY.NAME,
                            COMPANY.TIN,
                            COMPANY.CHECKING_ACCOUNT,
                            PRODUCT.INNER_CODE,
                            PRODUCT.NAME)
                    .from(CONSINGMENT)
                    .leftJoin(POSITION)
                    .on(CONSINGMENT.CONSINGMENT_ID
                            .eq(POSITION.CONSINGMENT_ID)
                            .and(CONSINGMENT.ORDER_DATE
                                    .between(from.toLocalDate(), to.toLocalDate())))
                    .leftJoin(PRODUCT)
                    .on(PRODUCT.INNER_CODE.eq(POSITION.INNER_CODE))
                    .rightJoin(COMPANY)
                    .on(COMPANY.COMPANY_ID.eq(CONSINGMENT.CONSINGMENT_ID))
                    .fetch();

            for(Record record : result) {
                int companyId = record.getValue(COMPANY.COMPANY_ID);
                String companyName = record.getValue(COMPANY.NAME);
                int TIN = record.getValue(COMPANY.TIN);
                int checkingAccount = record.getValue(COMPANY.CHECKING_ACCOUNT);
                final var company = new Company(companyId, companyName, TIN, checkingAccount);
                if (!map.containsKey(company)) {
                    map.put(company, new HashSet<>());
                }
                if (record.getValue(PRODUCT.NAME) == null) {
                    continue;
                }

                final var product = new Product(
                        record.getValue(PRODUCT.INNER_CODE),
                        record.getValue(PRODUCT.NAME)
                );
                map.get(company).add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return map;
    }
}
