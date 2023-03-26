package ru.uskov.dmitry.transferapp.storage.entity;

import java.util.Date;
import java.util.Objects;

public class Account {
    private long id;
    private String description;
    private long amount;
    private Date lastUpdate;
    private Date creationTime;
    private Boolean blocked;


    public Account() {
    }

    public Account(Account account) {
        this.id = account.id;
        this.description = account.description;
        this.amount = account.amount;
        this.lastUpdate = account.lastUpdate;
        this.creationTime = account.creationTime;
        this.blocked = account.blocked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public static Account createNew(long id, String description) {
        Date currentTime = new Date();
        Account account = new Account();
        account.setId(id);
        account.setDescription(description);
        account.setCreationTime(currentTime);
        account.setLastUpdate(currentTime);
        account.setAmount(0);
        account.setBlocked(false);
        return account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id &&
                amount == account.amount &&
                Objects.equals(description, account.description) &&
                Objects.equals(lastUpdate, account.lastUpdate) &&
                Objects.equals(creationTime, account.creationTime) &&
                Objects.equals(blocked, account.blocked);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", lastUpdate=" + lastUpdate +
                ", creationTime=" + creationTime +
                ", blocked=" + blocked +
                '}';
    }
}
