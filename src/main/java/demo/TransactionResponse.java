package demo;

import java.util.Date;
import java.util.UUID;

public class TransactionResponse 
{
    private Date timestamp;
    private UUID transactionId;
    private String hostName;
    private long itemCount;

    public static TransactionResponse Create(long itemCount)
    {   
        // Create the Entity
        TransactionResponse response = new TransactionResponse();

        // Set the entity properties
        response.setTransactionId(UUID.randomUUID());
        response.setTimestamp(new Date());
        response.setHostName(System.getenv().get("HOSTNAME"));
        response.setItemCount(itemCount);

        return response;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp(Date value)
    {
        this.timestamp = value;
    }


    public UUID getTransactionId() 
    {
        return transactionId;
    }

    public String getHostName() 
    {
        return hostName;
    }

    public void setHostName(String value) 
    {
        this.hostName = value;
    }

    public void setTransactionId(UUID value)
    {
        transactionId = value;
    }


    /**
     * @return long return the itemCount
     */
    public long getItemCount() {
        return itemCount;
    }

    /**
     * @param itemCount the itemCount to set
     */
    public void setItemCount(long itemCount) {
        this.itemCount = itemCount;
    }

}