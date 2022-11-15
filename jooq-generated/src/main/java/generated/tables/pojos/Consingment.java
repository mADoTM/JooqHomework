/*
 * This file is generated by jOOQ.
 */
package generated.tables.pojos;


import java.io.Serializable;
import java.time.LocalDate;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Consingment implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer consingmentId;
    private final LocalDate orderDate;
    private final Integer companyId;

    public Consingment(Consingment value) {
        this.consingmentId = value.consingmentId;
        this.orderDate = value.orderDate;
        this.companyId = value.companyId;
    }

    public Consingment(
        Integer consingmentId,
        LocalDate orderDate,
        Integer companyId
    ) {
        this.consingmentId = consingmentId;
        this.orderDate = orderDate;
        this.companyId = companyId;
    }

    /**
     * Getter for <code>public.consingment.consingment_id</code>.
     */
    public Integer getConsingmentId() {
        return this.consingmentId;
    }

    /**
     * Getter for <code>public.consingment.order_date</code>.
     */
    public LocalDate getOrderDate() {
        return this.orderDate;
    }

    /**
     * Getter for <code>public.consingment.company_id</code>.
     */
    public Integer getCompanyId() {
        return this.companyId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Consingment other = (Consingment) obj;
        if (this.consingmentId == null) {
            if (other.consingmentId != null)
                return false;
        }
        else if (!this.consingmentId.equals(other.consingmentId))
            return false;
        if (this.orderDate == null) {
            if (other.orderDate != null)
                return false;
        }
        else if (!this.orderDate.equals(other.orderDate))
            return false;
        if (this.companyId == null) {
            if (other.companyId != null)
                return false;
        }
        else if (!this.companyId.equals(other.companyId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.consingmentId == null) ? 0 : this.consingmentId.hashCode());
        result = prime * result + ((this.orderDate == null) ? 0 : this.orderDate.hashCode());
        result = prime * result + ((this.companyId == null) ? 0 : this.companyId.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Consingment (");

        sb.append(consingmentId);
        sb.append(", ").append(orderDate);
        sb.append(", ").append(companyId);

        sb.append(")");
        return sb.toString();
    }
}