package ru.mail.dao;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import ru.mail.commons.DAO;
import ru.mail.commons.DSLContextHelper;
import ru.mail.dto.entity.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static generated.Tables.PRODUCT;

public final class ProductDAO implements DAO<Product> {
    @Override
    public @NotNull Product get(int id) {
        try {
            var context = DSLContextHelper.getContext();
            var result = context
                    .select()
                    .from(PRODUCT)
                    .where(PRODUCT.INNER_CODE.eq(id))
                    .fetch();

            for (Record record : result) {
                int innerCode = record.getValue(PRODUCT.INNER_CODE);
                String name = record.getValue(PRODUCT.NAME);
                return new Product(innerCode, name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException("Record with id " + id + "not found");
    }

    @Override
    public @NotNull List<@NotNull Product> all() {
        final var list = new ArrayList<Product>();
        try {
            var context = DSLContextHelper.getContext();
            var result = context
                    .select()
                    .from(PRODUCT)
                    .fetch();

            for (Record record : result) {
                int innerCode = record.getValue(PRODUCT.INNER_CODE);
                String name = record.getValue(PRODUCT.NAME);
                list.add(new Product(innerCode, name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void save(@NotNull Product entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .insertInto(PRODUCT)
                    .values(entity.innerCode(), entity.name())
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(@NotNull Product entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .update(PRODUCT)
                    .set(PRODUCT.NAME, entity.name())
                    .where(PRODUCT.INNER_CODE.eq(entity.innerCode()))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(@NotNull Product entity) {
        try {
            var context = DSLContextHelper.getContext();
            context
                    .delete(PRODUCT)
                    .where(PRODUCT.INNER_CODE.eq(entity.innerCode()))
                    .execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
